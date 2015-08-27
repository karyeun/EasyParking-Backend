/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package challenge.api;

import static challenge.services.OfyService.ofy;
import classes.ChartData;
import classes.Device;
import classes.SingleValue;
import classes.Parking;
import classes.ParkingInfo;
import classes.ParkingStatus;
import classes.RecentData;
import classes.ResponseStatus;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.repackaged.com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(
    name = "attapi",
    version = "v1",
    description = "AT&T M2X API",
    scopes = {Constants.EMAIL_SCOPE},
    clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
    audiences = {Constants.ANDROID_AUDIENCE}
)
public class Atnt {
    static {
    }
    
    //@ApiMethod(name = "att.myClass", httpMethod = "get")
//    @ApiMethod(httpMethod="get", path = "att/getMyClass")
//    public MyClass getMyClass() {
//        MyClass myClass=new MyClass();
//        myClass.msg="Now is "+ DateTime.now().toString();
//        return myClass;
//    }    
   
//    @ApiMethod(httpMethod="post", path = "att/saveObj")
//    public ResponseStatus saveObj(Car car) {
//        ResponseStatus apiResponse=new ResponseStatus();
//
//        ofy().save().entity(car).now();
//        
//        return apiResponse;
//    }    
    
    @ApiMethod(httpMethod="post", path = "att/park")
    public ResponseStatus park(Parking parking) {
        ResponseStatus apiResponse=new ResponseStatus();
        String urlString="http://api-m2x.att.com/v2/devices/{deviceId}/streams/{streamId}/value";
        urlString=urlString.replace("{deviceId}", parking.deviceId);
        urlString=urlString.replace("{streamId}", parking.streamId);
        
        URL url;
        try {
            url = new URL(urlString);
            HttpURLConnection hurl=(HttpURLConnection) url.openConnection();
            hurl.setRequestMethod("PUT");
            hurl.setDoOutput(true);
            hurl.setRequestProperty("Content-Type", "application/json");
            hurl.setRequestProperty("Accept", "application/json");
            hurl.setRequestProperty("X-M2X-KEY", parking.apiKey);
            
            SingleValue singleValue=new SingleValue();
            singleValue.value=parking.value;
            
            Gson json =new Gson();
            String payload=json.toJson(singleValue);
            
            OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();
            
            StringBuilder sb=new StringBuilder();
            String data;
            BufferedReader br=new BufferedReader(new InputStreamReader(hurl.getInputStream()));
            while((data=br.readLine())!=null)
                sb=sb.append(data);
            br.close();
            
            hurl.disconnect();
            
            if(sb.toString().contains("accepted")) {         
                apiResponse=saveParkingInfo(apiResponse, parking);
            }
            else {
                apiResponse.Status=false;
                apiResponse.Exception=sb.toString();
            }
        } catch (MalformedURLException e) {
            apiResponse.Status=false;
            apiResponse.Exception=e.toString();
        } catch (IOException e) {
            apiResponse.Status=false;
            apiResponse.Exception=e.toString();            
        }
        
        return apiResponse;
    }     
    
    private String getNowInString() {
        final int TIME_ZONE=8;
        final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        GregorianCalendar now=new GregorianCalendar();
        now.add(GregorianCalendar.HOUR, TIME_ZONE);
        return sdf.format(now.getTime());
    }
    
    private ResponseStatus saveParkingInfo(ResponseStatus apiResponseStatus, Parking p) {
        ParkingInfo pi=p.parkingInfo;
        if(p.value.equalsIgnoreCase("0")) { //Becomes available
            pi=ofy().load().type(ParkingInfo.class)
                .filter("streamId", p.streamId)
                .filter("timeOut", "").first().now();    
            if(pi==null) { //no in, so no out.
                apiResponseStatus.Status=false;
                apiResponseStatus.Exception="Parking is never being occupied.";
                return apiResponseStatus;
            }
            
            pi.payment=p.parkingInfo.payment;
            pi.timeOut=getNowInString();
        }
        else {
            pi.streamId=p.streamId;
            pi.timeIn=getNowInString();
            pi.dateCreated=new GregorianCalendar().getTimeInMillis();
        }
        
        ofy().save().entity(pi).now();
        
        return apiResponseStatus;
    }
    
    @ApiMethod(httpMethod="post", path = "att/latest")
    public ResponseStatus status(Device device) {
        ResponseStatus apiResponse=new ResponseStatus();
        List<ParkingStatus> parkingStatuses=new ArrayList<>();
        String[] parks=new String[]{"A1","A2","A3","B1","B2","B3"};
        
        String urlString="http://api-m2x.att.com/v2/devices/{deviceId}/streams/{streamId}/values?limit=1";
        urlString=urlString.replace("{deviceId}", device.deviceId);
        
        URL url;
        try {
            for (String park : parks) {
                url = new URL(urlString.replace("{streamId}", park));
                HttpURLConnection hurl=(HttpURLConnection) url.openConnection();
                hurl.setRequestMethod("GET");
                hurl.setDoOutput(true);
                hurl.setRequestProperty("Content-Type", "application/json");
                hurl.setRequestProperty("Accept", "application/json");
                hurl.setRequestProperty("X-M2X-KEY", device.apiKey);
                StringBuilder sb=new StringBuilder();
                String data;
                BufferedReader br=new BufferedReader(new InputStreamReader(hurl.getInputStream()));
                while((data=br.readLine())!=null)
                    sb=sb.append(data);
                br.close();
                hurl.disconnect();
                
//                if(!sb.toString().contains("accepted")) {
//                    apiResponse.Status=false;
//                    apiResponse.Exception=sb.toString();
//                    break;
//                }
                
                Gson json=new Gson();
                RecentData recentData=json.fromJson(sb.toString(), RecentData.class);
                
                ParkingStatus parkStatus=new ParkingStatus();
                parkStatus.streamId=park;
                if( recentData.values.size()>0) parkStatus.status=recentData.values.get(0).value;
                else parkStatus.status=0d;
                
                parkingStatuses.add(parkStatus);
            }
            
            apiResponse.Status=true;
            apiResponse.Result=parkingStatuses;
        } catch (MalformedURLException e) {
            apiResponse.Status=false;
            apiResponse.Exception=e.toString();
        } catch (IOException e) {
            apiResponse.Status=false;
            apiResponse.Exception=e.toString();            
        }
        
        return apiResponse;
    }     
    
    @ApiMethod(httpMethod="post", path = "parkingInfo")
    public ResponseStatus getParkingInfo() {
        ResponseStatus apiResponse=new ResponseStatus();
        try {
            List<ParkingInfo> parkingInfoList=ofy().load().type(ParkingInfo.class)
                .order("-dateCreated").limit(10).list();
            apiResponse.Status=true;
            apiResponse.Result=parkingInfoList;
        } catch(Exception e) {
            apiResponse.Status=false;
            apiResponse.Exception=e.toString();
        }
        
        return apiResponse;
    }         
    
    @ApiMethod(httpMethod="post", path = "parkingSummary")
    public ResponseStatus getParkingGraph(SingleValue thisDate) {
        final SimpleDateFormat sdf=new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        
        Map<Integer, ChartData> graph=new HashMap<>();
        for (int idx=0; idx<24; idx++) { //24 hrs graph
            ChartData chartData=new ChartData();
            chartData.hours=idx;
            graph.put(idx, chartData);
        }

        ResponseStatus apiResponse=new ResponseStatus();
        try {
            String[] dates=thisDate.value.split("-");
            if(dates.length==3) {
                GregorianCalendar calFrom=new GregorianCalendar(Integer.parseInt(dates[0]), 
                    Integer.parseInt(dates[1])-1, 
                    Integer.parseInt(dates[2]));
                GregorianCalendar calTo=(GregorianCalendar) calFrom.clone();
                calTo.add(GregorianCalendar.HOUR, 24);

//                System.out.println(sdf.format(calFrom.getTime())+","+
//                        sdf.format(calTo.getTime()));
                
                //get the date
                List<ParkingInfo> parkingInfos=ofy().load().type(ParkingInfo.class)
                    .filter("timeIn >=", sdf.format(calFrom.getTime()))
                    .filter("timeIn <", sdf.format(calTo.getTime())).list();

                //count the time
                for(ParkingInfo parkingInfo:parkingInfos) {
                    String[] datetimes=parkingInfo.timeIn.split(" ");
                    if(datetimes.length==2) {
                        int hours=Integer.parseInt(datetimes[1].split(":")[0]);
                        ChartData chartData=graph.get(hours);
                        if(parkingInfo.streamId.startsWith("A"))
                            chartData.parkingA++;
                        else if(parkingInfo.streamId.startsWith("B"))
                            chartData.parkingB++;
                    }
                }
                
                apiResponse.Status=true;
                apiResponse.Result=graph.values().toArray();
            }
            else {
                apiResponse.Status=false;
                apiResponse.Result="Wrong date format, use yyyy-MM-dd";
            }
        } catch(NumberFormatException e) {
            apiResponse.Status=false;
            apiResponse.Exception=e.toString();
        }
        
        return apiResponse;
    }         
}
