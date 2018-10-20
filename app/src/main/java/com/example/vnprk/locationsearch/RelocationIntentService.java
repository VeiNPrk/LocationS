package com.example.vnprk.locationsearch;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by VNPrk on 18.02.2018.
 */

public class RelocationIntentService extends IntentService {

    public RelocationIntentService() {
        super("RelocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        RelocationTasks.executeTask(this, action);
    }
}
