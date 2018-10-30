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
    private int codeOperation=1;
    public static final int ALL_USERS = 1;
    public static final int REQUEST_USERS = 2;
    public static final String OPERATION_KEY = "operation_key";

    public UserLoader(Context context , Bundle args) {
        super(context);
        if (args != null) {
            codeOperation = args.getInt(OPERATION_KEY,1);
        }
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, "loadInBackground");
        Cursor cursor = null;
        try {

            switch (codeOperation) {
                case ALL_USERS:
                    cursor = getAllUsers();
                    break;
                case REQUEST_USERS:
                    cursor = getRequests();
                    break;
                default:
                    cursor = null;
                break;
            }
            return cursor;
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

    protected Cursor getAllUsers() throws IOException {
        List<UserClass> users = new ArrayList<>();
        Cursor usersCursor = null;
        try {
            users.addAll(App.getApi().getUsers(App.iam.getId()).execute().body());
        }
        catch (Exception ex)
        {
            Log.d("getAllUsers", ex.getMessage());
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

    private Cursor getRequests() throws IOException {
        List<UserClass> users = new ArrayList<>();
        Cursor usersCursor = null;
        try {
            users.addAll(App.getApi().getRequests(App.iam.getId()).execute().body());
        }
        catch (Exception ex)
        {
            Log.d("getRequest", ex.getMessage());
        }
        if(users!=null && users.size()>0)
        {
            SQLite.delete(UserClass.class)
                    .where(UserClass_Table.type.eq(2))
                    .execute();
            for (UserClass user: users) {
                user.save();
            }
        }
        usersCursor = new Select().from(UserClass.class).where(UserClass_Table.type.eq(2)).query();
        return usersCursor;
    }
}
