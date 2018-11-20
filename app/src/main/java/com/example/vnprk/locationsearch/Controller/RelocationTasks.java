package com.example.vnprk.locationsearch.Controller;

import android.content.Context;
import android.location.Location;

import com.example.vnprk.locationsearch.Controller.DataBase;
import com.example.vnprk.locationsearch.Controller.LocationTools;
import com.example.vnprk.locationsearch.Model.LocationClass;

import java.util.List;

/**
 * Created by VNPrk on 18.02.2018.
 */

public class RelocationTasks {
    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_CHARGING_REMINDER = "charging-reminder";
    static Location nowLocation = null;
    public static void executeTask(Context context, String action) {
        nowLocation = LocationTools.getNowLocation(context);
        issueRelocation(context);
    }

    private static void issueRelocation(Context context) {
        //PreferenceUtilities.incrementChargingReminderCount(context);
        //DataBase.operationDataBase(DataBase.TYPE_OPERATION_INSERT, 0, 1, nowLocation.getLatitude(), nowLocation.getLongitude());
        LocationClass newLocation = new LocationClass(nowLocation);
        newLocation.save();

        List<LocationClass> newLocations = DataBase.getNewLocations();
        for(LocationClass location : newLocations)
        {
            DataBase.setLocationData(location);
        }

        //NotificationUtils.remindUserBecauseCharging(context);
    }
}
