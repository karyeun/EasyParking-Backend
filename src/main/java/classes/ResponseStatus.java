/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

/**
 *
 * @author ky.yong
 */
public class ResponseStatus {
    public boolean Status=true;
    public String Exception="";
    public Object Result=null;

    public ResponseStatus() {
    }
    
    public ResponseStatus(boolean defaultStatus) {
        Status=defaultStatus;
    }
}