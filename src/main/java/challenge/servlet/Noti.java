/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package challenge.servlet;

import challenge.services.Notification;
import classes.NotificationMessage;
import classes.Trigger;
import com.google.appengine.repackaged.com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ky.yong
 */
public class Noti extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder sb=new StringBuilder();
        BufferedReader r= request.getReader();
        String d;
        while ((d=r.readLine())!=null)
            sb=sb.append(d);    
        
        Gson json=new Gson();
        Trigger trigger=json.fromJson(sb.toString(), Trigger.class);
        
        NotificationMessage msg=new NotificationMessage();
//        msg.Message="Device: "+trigger.device_id+",\n"+
//                "Stream: "+trigger.stream+",\n"+
//                "Trigger: "+String.valueOf(trigger.value)+",\n"+
//                "Status: "+(trigger.value==0?"Available":"Occupied");
        msg.Message="Parking Lot: "+trigger.stream+",\n"+
                "Status: "+(trigger.value==0?"Available":"Occupied");        
        
        Logger.getLogger("Noti").log(Level.INFO, msg.Message);
        
        Notification notification=new Notification();
        notification.trigger(msg);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
