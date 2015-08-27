/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package challenge.services;

import classes.Car;
import classes.ParkingInfo;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 *
 * @author ky.yong
 */
public class OfyService {
    static {
        factory().register(Car.class);
        factory().register(ParkingInfo.class);
    }
    
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }    
}
