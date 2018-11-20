package com.example.vnprk.locationsearch.Services;

import android.content.Context;
import android.os.AsyncTask;

import com.example.vnprk.locationsearch.Controller.RelocationTasks;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by VNPrk on 18.02.2018.
 */

public class LocationFirebaseJob extends JobService {
    private AsyncTask mBackgroudTask;
    @Override
    public boolean onStartJob(final JobParameters params) {
        mBackgroudTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = LocationFirebaseJob.this;
                RelocationTasks.executeTask(context, RelocationTasks.ACTION_CHARGING_REMINDER);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };
        mBackgroudTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(mBackgroudTask!=null) mBackgroudTask.cancel(true);
        return true;
    }
}
