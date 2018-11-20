package com.example.vnprk.locationsearch.Model;

import android.database.SQLException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.vnprk.locationsearch.App;
import com.example.vnprk.locationsearch.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VNPrk on 23.01.2018.
 */
@Table(database = AppDataBase.class)
public class LocationClass extends BaseModel{

	@Column
	private double locationLatitude = 0;
	@Column
	private double locationLongitude = 0;
	@PrimaryKey
	@Column
	private int idUser = 0;
	@Column
	private long dateTimeMils = 0;
	@Column(defaultValue = "0")
	private int status;


	public LocationClass(){
	}

    public LocationClass(int _id, double _latitude, double _longitude){
		idUser = _id;
		locationLatitude = _latitude;
		locationLongitude = _longitude;
		dateTimeMils = new Date().getTime();
		//dateTimeMils = currentTimeMillis();
    }
	
	public LocationClass(int _id, double _latitude, double _longitude, long _dateMills){
		idUser = _id;
		locationLatitude = _latitude;
		locationLongitude = _longitude;
		dateTimeMils = _dateMills;
    }
	
	public LocationClass(double _latitude, double _longitude){
		locationLatitude = _latitude;
		locationLongitude = _longitude;
		dateTimeMils = new Date().getTime();
    }


	public LocationClass(Location location){
		idUser = App.iam.getId();
		locationLatitude = location.getLatitude();
		locationLongitude = location.getLongitude();
		dateTimeMils = new Date().getTime();
	}

    public int getIdUser(){
		return idUser;
	}
	
	public double getLocationLatitude(){
		return locationLatitude;
	}
	
	public double getLocationLongitude(){
		return locationLongitude;
	}
	
	public long getDateTimeMils(){
		return dateTimeMils;
	}

	public void setIdUser(int id){
		idUser=id;
	}

	public void setLocationLatitude(double latitude){
		locationLatitude = latitude;
	}

	public void setLocationLongitude(double longitude){
		locationLongitude = longitude;
	}

	public void setDateTimeMils(long dateTime){
		dateTimeMils = dateTime;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int _status){
		status=_status;
	}


	public String getStringDateTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String strDt = simpleDateFormat.format(new Date(dateTimeMils));
		return strDt;
	}
}
