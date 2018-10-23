package com.example.vnprk.locationsearch;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VNPrk on 23.09.2018.
 */

public class UserLoader extends AsyncTaskLoader<Cursor> {

    public final String TAG = getClass().getSimpleName();

    public UserLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, "loadInBackground");
        try {
            return apiCall();
        } catch (IOException e) {
            Log.d("loadInBackground", e.getMessage());
            return null;
        }

        //getData(typeOperation, typeCondition);
    }

    @Override
    public void forceLoad() {
        Log.d(TAG, "forceLoad");
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, "onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d(TAG, "onStopLoading");
    }

    @Override
    public void deliverResult(Cursor data) {
        Log.d(TAG, "deliverResult");
        super.deliverResult(data);
    }

    protected Cursor apiCall() throws IOException {
        List<UserClass> users = new ArrayList<>();
        Cursor usersCursor = null;
        try {
            users.addAll(App.getApi().getUsers(App.iam.getId()).execute().body());
        }
        catch (Exception ex)
        {
            Log.d("apiCall", ex.getMessage());
        }
        if(users!=null && users.size()>0)
        {
            SQLite.delete(UserClass.class)
                    .where(UserClass_Table.type.eq(0))
                    .execute();
            for (UserClass user: users) {
                user.save();
            }
        }
        usersCursor = new Select().from(UserClass.class).where(UserClass_Table.type.eq(0)).query();
        return usersCursor;
    }
}
