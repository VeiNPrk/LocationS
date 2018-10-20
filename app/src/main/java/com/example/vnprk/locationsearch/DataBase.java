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
    private static final String  ip = "81.1.243.39:1433/mssqlnilfmk"; // if you have to add port then it would be like .i.e. 182.50.133.109:1433
    private static final String  db = "GenerateExcel"; //Name of Database
    private static final String un = "ProgramUser"; //Database user
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
/*
    public List<UserClass> getUsersList()
    {
        List<UserClass> users = null;
        ArrayList<UserClass> usersApi = getUsersListFromApi();
        if(usersApi!=null && usersApi.size()>0)
        {
            SQLite.delete(UserClass.class)
                    .where(UserClass_Table.type.eq(0))
                    .execute();
            for (UserClass user: users) {
                user.save();
            }
            users = new Select().from(UserClass.class).queryList();
        }
        return users;
    }
/*
    private ArrayList<UserClass> getUsersListFromApi() {
        final ArrayList<UserClass> users = new ArrayList<>();
        App.getApi().getAcceptedUser(App.iam.getId()).enqueue(new Callback<List<UserClass>>() {
            @Override
            public void onResponse(Call<List<UserClass>> call, Response<List<UserClass>> response) {
                try {
                    users.addAll(response.body());
                    Log.d("getUsersListFromApi", response.body().toString());
                } catch (Exception ex) {
                    //Toast.makeText(MainActivity.this, "An error " + ex, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserClass>> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });
        return users;
    }*/

    public static Connection openConnection()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
	
	public static ArrayList<LocationClass> operationDataBase(int typeOperation, int typeConditions, int idUser, double lat, double longit){
		ArrayList<LocationClass> result = new ArrayList<LocationClass>();
		switch(typeOperation){
			case TYPE_OPERATION_INSERT:
				result = insertLocationData(idUser, lat, longit);
				break;
			case TYPE_OPERATION_SELECT:
				result = selectLocationData(idUser, typeConditions);
				break;
			default:
				result=null;
				break;
		}
		
		return result;
	}
    private static ArrayList<LocationClass> selectLocationData(int idUser, int typeConditions){
        ResultSet rs = null;
		ArrayList<LocationClass> result = new ArrayList<LocationClass>();
        String msg = "Internet/DB_Credentials/Windows_FireWall_TurnOn Error, See Android Monitor in the bottom For details!";
		String conditionStr = "";
		switch(typeConditions){
			case SELECT_CONDITION_LAST:
				conditionStr = " and date_location=(select max(date_location) from ls_location where id_user=" + idUser + ")";
				break;
			case SELECT_CONDITION_DAY:
                conditionStr = " and datetime_location>=CAST(GETDATE() AS DATE) and datetime_location<DATEADD(day, 1,  CAST(GETDATE() AS DATE))";
				break;
			default:
				break;
		}
        try
        {
            Connection conn = openConnection(); //Connection Object
            if (conn != null){
                // Change below query according to your own database.
                String query = "SELECT * FROM "+LOCATION_TABLE+" where "+COLUMN_USER+"="+idUser +" "+ conditionStr;
                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
				while (rs.next())
				{
					try {
						int id = rs.getInt(COLUMN_USER);
						float latitude = rs.getFloat(COLUMN_LATITUDE);
						float longitude = rs.getFloat(COLUMN_LONGITUDE);
						long dateMils = rs.getLong(COLUMN_DATE);
						
						LocationClass location = new LocationClass(id, latitude, longitude, dateMils);
						result.add(location);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
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

	private static ArrayList<LocationClass> insertLocationData(int idUser, double latitude, double longitude){
		ArrayList<LocationClass> result = null;
		
        try
        {
            Connection conn = openConnection();
            if (conn != null){
				result = new ArrayList<LocationClass>();
                LocationClass location = new LocationClass(idUser, latitude, longitude);
						
                String query = "INSERT INTO "+LOCATION_TABLE+" VALUES ("+ idUser + ", " + latitude + ", " + longitude + ", " + location.getDateTimeMils() + ", getdate())";
                Statement stmt = conn.createStatement();
				stmt.executeUpdate(query);
				result.add(location);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
