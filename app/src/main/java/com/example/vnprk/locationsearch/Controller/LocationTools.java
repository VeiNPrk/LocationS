package com.example.vnprk.locationsearch.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by VNPrk on 23.01.2018.
 */
class LocationTools implements LocationListener {

    public static Location nowLocation = null; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
    Context context;
    private static LocationManager locationManager = null;
    //private double defaultLaitude = Double.valueOf(R.string.default_latitude);
    //private double defaultLongitude = Double.valueOf(R.string.default_latitude);
    public LocationTools(Context cont) {
        super();
        context = cont;
        //nowLocation = new Location(cont);
        //nowLocation.setLatitude(defaultLaitude);
        //nowLocation.setLongitude(defaultLongitude);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        initLocation(context);
    }

    public void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
    {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //LocationListener locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                this); // здесь можно указать другие более подходящие вам параметры
        initLocation(context);
	}
	
	@SuppressLint("MissingPermission")
    private static void initLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        nowLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(nowLocation!=null){
            return;
        }
        // Receive information from GPS provider
        nowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(nowLocation!=null){
            return;
        }
        nowLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location loc) {
		nowLocation = loc;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        initLocation(context);
    }

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}

    public static Location getNowLocation(Context context){
        if(nowLocation==null)
            initLocation(context);
        return nowLocation;
    }
}