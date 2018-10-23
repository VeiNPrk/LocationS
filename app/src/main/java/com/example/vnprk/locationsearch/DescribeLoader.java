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

public class DescribeLoader extends AsyncTaskLoader<Cursor> {

    public final String TAG = getClass().getSimpleName();

    private int idDepend;
    private int status;
    private int codeOperation;
    public static final int NEW_DESCRIBE = 1;
    public static final int UPDATE_DESCRIBE = 2;
    public static final String IDDEPEND_KEY="id_depend";
    public static final String STATUS_KEY="status";
    public static final String OPERATION_KEY="operation";

    public DescribeLoader(Context context, Bundle args/*int idDepend, int status, int codeOperation*/) {
        super(context);
        if (args != null) {
            idDepend = args.getInt(IDDEPEND_KEY);
            status = args.getInt(STATUS_KEY);
            codeOperation = args.getInt(OPERATION_KEY);
        }
    }

    @Override
    public Cursor loadInBackground() {
        Log.d(TAG, "loadInBackground");
        Cursor cursor = null;
        try {
            switch(codeOperation) {
                case NEW_DESCRIBE:
                    cursor= newDescribeCall(idDepend);
                    break;
                case UPDATE_DESCRIBE:
                    cursor= updateDescribeCall(idDepend, status);
                    break;
                default:
                    cursor= null;
                    break;
            }
            return cursor;
        } catch (IOException e) {
            Log.e("loadInBackground", e.getMessage());
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

    protected Cursor newDescribeCall(int idDepend) throws IOException {
        List<UserClass> users = new ArrayList<>();
        Cursor usersCursor = null;
        try {
            users.addAll(App.getApi().setDescribe(App.iam.getId(), idDepend).execute().body());
        }
        catch (Exception ex)
        {
            Log.e("newDescribeCall", ex.getMessage());
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

    protected Cursor updateDescribeCall(int idDepend, int status) throws IOException {
        List<UserClass> users = new ArrayList<>();
        Cursor usersCursor = null;
        try {
            users.addAll(App.getApi().updateDescribe(App.iam.getId(), idDepend, status).execute().body());
        }
        catch (Exception ex)
        {
            Log.e("updateDescribeCall", ex.getMessage());
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
