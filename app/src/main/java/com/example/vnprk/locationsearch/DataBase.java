package com.example.vnprk.locationsearch;

import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by VNPrk on 23.01.2018.
 */

public class DataBase {
    private static final String password = "#xo?AEtn%nZD"; //Database Password
	private static final String LOCATION_TABLE = "ls_location";
	public static final int SELECT_CONDITION_LAST = 1;
	public static final int	SELECT_CONDITION_DAY = 2;
	public static final int	SELECT_CONDITION_ = 3;
	public static final int TYPE_OPERATION_SELECT = 1;
	public static final int TYPE_OPERATION_INSERT = 2;
	private static final String COLUMN_USER = "id_user";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_DATE = "date_location";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_NAME="name";
    public static final String USER_COLUMN_TYPE="type";
    private boolean success = false; // boolean
    //@SuppressLint("NewApi")

    public DataBase(){
    }

    public List<LocationClass> getLastLocation(int idUser)
    {
        long maxDateMills = 0;
        /*Cursor cursor = new Select(Method.ALL_PROPERTY, Method.max(LocationClass_Table.dateTimeMils))
                .from(LocationClass.class)
                .where(LocationClass_Table.idUser.eq(idUser)).query();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                maxDateMills = cursor.getLong(0);
            }
        }*/
        Cursor cursor = SQLite.select(Method.max(LocationClass_Table.dateTimeMils).as("max")).from(LocationClass.class)
                .where(LocationClass_Table.idUser.eq(idUser))
                .query();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                maxDateMills = cursor.getLong(0);
                //Log.d("cursor", ""+cursor.getLong(0));// maxDateMills = cursor.getLong(0);
            }
        }
        //maxDateMills = cursor.getLong(0);

        cursor.close();
        List<LocationClass> locations = null;
        LocationClass lastLocation = null;
        try{
            locations = new Select().from(LocationClass.class).where(LocationClass_Table.dateTimeMils.eq(maxDateMills)).queryList();
        }
        catch (Exception ex)
        {

        }
        return locations;
    }

    public List<LocationClass> getLocationForDay(int idUser)
    {

        Date now = new Date();
        long startDay = atStartOfDay(now).getTime();
        long endDay = atEndOfDay(now).getTime();
        List<LocationClass> listLocations = new ArrayList<>();
        try {
            listLocations = new Select().from(LocationClass.class).where(LocationClass_Table.dateTimeMils.between(startDay).and(endDay)).queryList();
        }
        catch (Exception ex)
        {

        }
        return listLocations;
    }

    public Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static void setLocationData(LocationClass locationClass) {
        if (locationClass != null) {
            try {
                ResponseResult response = App.getApi().setLocation(locationClass.getIdUser(), locationClass.getLocationLatitude(),
                        locationClass.getLocationLongitude(), locationClass.getDateTimeMils()).execute().body();
                if(response.getResult()==1)
                {
                    locationClass.setStatus(1);
                    locationClass.save();
                }
            }
            catch (Exception ex)
            {
                Log.e("CATCH setLocationData", ex.getMessage());
            }
        }
    }


    public static List<LocationClass> getNewLocations()
    {
        List<LocationClass> locations = null;
        try{
            locations = new Select().from(LocationClass.class).where(LocationClass_Table.status.eq(0)).queryList();
        }
        catch (Exception ex)
        {
            Log.e("CATCH getNewLocations", ex.getMessage());
        }
        return locations;
    }
    public static List<UserClass> getAllUsers()
    {
        List<UserClass> users = null;
        try{
            users = new Select().from(UserClass.class).where(UserClass_Table.type.eq(0)).queryList();
        }
        catch (Exception ex)
        {
            Log.e("CATCH getAllUsers", ex.getMessage());
        }
        return users;
    }

    public static List<UserClass> getRequestUsers()
    {
        List<UserClass> users = null;
        try{
            users = new Select().from(UserClass.class).where(UserClass_Table.type.eq(2)).queryList();
        }
        catch (Exception ex)
        {
            Log.e("CATCH getAllUsers", ex.getMessage());
        }
        return users;
    }

    public static List<UserClass> getAcceptedUsers()
    {
        List<UserClass> users = null;
        try{
            users = new Select().from(UserClass.class).where(UserClass_Table.type.eq(0)).and(UserClass_Table.status.eq(1)).queryList();
        }
        catch (Exception ex)
        {
            Log.e("CATCH getAcceptedUsers", ex.getMessage());
        }
        return users;
    }

    public static List<UserClass> getUser(int id)
    {
        List<UserClass> users = null;
        try{
            users = new Select().from(UserClass.class).where(UserClass_Table.id.eq(id)).and(UserClass_Table.type.eq(0)).queryList();
            //users = new Select().from(UserClass.class).where(UserClass_Table.type.eq(0)).queryList();
            //users = new Select().from(UserClass.class).queryList();
        }
        catch (Exception ex)
        {
            Log.e("CATCH getAcceptedUsers", ex.getMessage());
        }
        return users;
    }
}
