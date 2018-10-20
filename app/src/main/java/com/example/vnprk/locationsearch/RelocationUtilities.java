package com.example.vnprk.locationsearch;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by VNPrk on 18.02.2018.
 */

public class RelocationUtilities {
        // TODO (15) Create three constants and one variable:
        public static final int RELOCATION_INTERVAL_MINUTES = 5;
        public static final int RELOCATION_INTERVAL_SECONDS = 20;//(int)(TimeUnit.MINUTES.toSeconds(RELOCATION_INTERVAL_MINUTES));
        public static final int SYNC_FLEXTIME_SECONDS = RELOCATION_INTERVAL_SECONDS;
        public static final String REMINDER_JOB_TAG = "hydration_reminder_tag";

        private static boolean sInitialized;

    synchronized public static void scheduleChargingReminder(Context context) {
        if(sInitialized)return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job conRemJob = dispatcher.newJobBuilder()
                .setService(LocationFirebaseJob.class)
                .setTag(REMINDER_JOB_TAG)

                //.setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(RELOCATION_INTERVAL_SECONDS,
                        RELOCATION_INTERVAL_SECONDS+SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(conRemJob);
        sInitialized = true;
    }
}
