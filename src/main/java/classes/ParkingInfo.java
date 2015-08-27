/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classes;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 *
 * @author ky.yong
 */
@Entity
public class ParkingInfo {
    public @Id Long id;
    @Index public String streamId;
    @Index public long dateCreated=0l;
    public String carManufacturer="";
    public String carPlateNo="";
    public String parkingName="";
    public String location="";
    public double payment=0l;
    @Index public String timeIn="";
    @Index public String timeOut="";    
}
