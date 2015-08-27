/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package challenge.services;

import classes.NotificationMessage;
import classes.ResponseStatus;
import com.google.appengine.repackaged.com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author ky.yong
 */
public class Notification {
//{
//  "GCMServerAPIKey" : "AIzaSyADLTGQMpXufdvlPqOmMdsn7F6Kp_wvSbc",
//  "DeviceToken" : "dIDvtrAOVdU:APA91bFznzLehyeHunSfGDqD3i4Jk_NAjnmgtxcGOcxF8wOlcJrHoGJmQaNOGD-CvoOaG2A1VxIAwqSeQCzYnu5QZ0myvJ3G3jfMKXXZhp6RLhp2AjNTkTdZBa-dNDQfoi7h_Pd1dpzM",
//  "Message" : "this is cy testing for secure chat"
//}
    public ResponseStatus trigger(NotificationMessage n) {
        ResponseStatus apiResponse=new ResponseStatus();
        final String urlNoti="http://223.27.128.247/TestingNotificationWebApi/api/androidNotification/gcm";
        
        URL url;
        try {
            url = new URL(urlNoti);
            HttpURLConnection hurl=(HttpURLConnection) url.openConnection();
            hurl.setRequestMethod("POST");
            hurl.setDoOutput(true);
            hurl.setRequestProperty("Content-Type", "application/json");
            hurl.setRequestProperty("Accept", "application/json");
            
            Gson json =new Gson();
            String payload=json.toJson(n);
            
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
            
           if (!sb.toString().equalsIgnoreCase("200")) {
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
}
