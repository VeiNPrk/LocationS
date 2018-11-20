package com.example.vnprk.locationsearch.View;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vnprk.locationsearch.App;
import com.example.vnprk.locationsearch.Model.LocationClass;
import com.example.vnprk.locationsearch.R;
import com.example.vnprk.locationsearch.Model.ResponseResult;
import com.example.vnprk.locationsearch.Model.UserClass;
import com.example.vnprk.locationsearch.Model.UserClass_Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

	TextView tvMyId = null;
	Button btnMap = null;
    Button btnDescribe = null;
    Button logTokenButton = null;
    private static final String TAG = "MainActivity";
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("init", "onCreate");
        setContentView(R.layout.activity_main);
        initViews();
        setClickListener();
        if (!canAccessContacts()) {
            Log.d("init", "runTimePermissions");
            runTimePermissions();
            //initLocation();
        }
        Log.d("init", "past runTimePermissions");
        //locationTools = new LocationTools(this);
        //RelocationUtilities.scheduleChargingReminder(this);
        CheckMyId();
    }
    private void initViews() {
        btnMap = (Button) findViewById(R.id.btn_map);
        btnDescribe = (Button) findViewById(R.id.btn_describe);
        tvMyId = (TextView) findViewById(R.id.tv_my_id);
        logTokenButton = findViewById(R.id.logTokenButton);

    }

    private void setClickListener() {
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        btnDescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDescribe();
            }
        });
        logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                App.getApi().setToken(App.iam.getId(), token).enqueue(new Callback<ResponseResult>() {
                                    @Override
                                    public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                                        Log.d("onResponse", "Передал!");
                                        Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseResult> call, Throwable t) {
                                        Log.e("onFailure", t.getMessage());
                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("init", "onStart");

        Toast.makeText(this, "onStart", Toast.LENGTH_LONG);

    }

    private boolean canAccessContacts() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //if (mLocListener != null) mLocManager.removeUpdates(mLocListener);
        super.onDestroy();
    }



    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        } else {
            return false;
        }
    }

    private void openMap(){
        MapActivity.openActivity(this);
    }

    private void openDescribe(){
        DescribeActivity.openActivity(this);
    }

	private void CheckMyId()
    {
        Log.d("init", "CheckMyId");
        UserClass im = new Select().from(UserClass.class).where(UserClass_Table.type.is(1)).querySingle();
        if(im==null) {
            App.getApi().getNewId().enqueue(new Callback<UserClass>() {
                @Override
                public void onResponse(Call<UserClass> call, Response<UserClass> response) {
                    try {
                        App.iam.setId(response.body().getId());
						App.iam.setName(response.body().getName());
						App.iam.setType(response.body().getType());
						App.iam.save();
						tvMyId.setText(App.iam.getStrName());
						//Log.d(response.body());
                    } catch (Exception ex) {
                        Log.d("CheckMyId CATCH", ex.getMessage());
                        //Toast.makeText(MainActivity.this, "An error " + ex, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserClass> call, Throwable t) {
                    Log.d("CheckMyId Failure", t.getMessage());
                    //Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                }
            });
        }
		else {
            App.iam.setId(im.getId());
            App.iam.setName(im.getName());
            App.iam.setType(im.getType());
            tvMyId.setText(App.iam.getStrName());
        }
        final List<LocationClass> locations = new ArrayList<>();
        App.getApi().getLocations(1).enqueue(new Callback<List<LocationClass>>() {
            @Override
            public void onResponse(Call<List<LocationClass>> call, Response<List<LocationClass>> response) {
                try {
                    locations.addAll(response.body());
                    Log.d("getLocations body", ""+locations.size());
                } catch (Exception ex) {
                    Log.d("getLocations catch", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<LocationClass>> call, Throwable t) {
                Log.d("getLocations onFailure", t.getMessage());
            }
        });
    }
	
    private void runTimePermissions(){
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("Интернет");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "Вы должны прописать разрешить следующее: " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= 23)
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23)
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Некоторые разрешения отсутствуют!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



}
