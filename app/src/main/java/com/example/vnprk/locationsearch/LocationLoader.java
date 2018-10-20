package com.example.vnprk.locationsearch;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VNPrk on 23.01.2018.
 */

public class LocationLoader extends AsyncTaskLoader<Cursor> {

    public final String TAG = getClass().getSimpleName();
    public static final String KEY_TYPE_OPERATION = "type_operation_key";
	public static final String KEY_SELECT_CONDITION = "type_condition_key";
    public static final String KEY_LATITUDE = "latitude_key";
    public static final String KEY_LONGITUDE = "longitude_key";
	public static final String KEY_USER_ID = "id_user_key";
    public static final int RANDOM_STRING_LENGTH = 100;
    private int typeOperation;
	private int typeCondition;
	private double latitude;
	private double longitude;
	private int idUser;

    public LocationLoader(Context context, Bundle args) {
        super(context);
        if (args != null){
            /*typeOperation = args.getInt(KEY_TYPE_OPERATION,0);
			typeCondition = args.getInt(KEY_SELECT_CONDITION,0);
            latitude = args.getDouble(KEY_LATITUDE,0);
            longitude = args.getDouble(KEY_LONGITUDE,0);*/
			idUser = args.getInt(KEY_USER_ID,0);
		}
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return apiCall(idUser);
        } catch (IOException e) {
            Log.d("loadInBackground", e.getMessage());
            return null;
        }
    }

    protected Cursor apiCall(int idUser) throws IOException {
        List<LocationClass> locations = new ArrayList<>();
        Cursor locationCursor = null;
        try {
            Log.d("", App.getApi().getLocations(idUser).execute().body().toString());
            locations.addAll(App.getApi().getLocations(idUser).execute().body());
        }
        catch (Exception ex)
        {
            Log.d("apiCall", ex.getMessage());
        }
        if(locations!=null && locations.size()>0)
        {
            //Delete.table(LocationClass.class);
            SQLite.delete().from(LocationClass.class).where(LocationClass_Table.idUser.eq(idUser))
                    //.where(LocationClass_Table)
                    .execute();
            for (LocationClass location: locations) {
                location.save();
            }
        }
        locationCursor = new Select().from(LocationClass.class).query();
        return locationCursor;
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
}
