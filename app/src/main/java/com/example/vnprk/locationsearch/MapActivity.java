package com.example.vnprk.locationsearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by VNPrk on 20.09.2018.
 */

public class MapActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {

    TextView tvGpsLocation = null;
    TextView tvMyId = null;
    Button btnLocation = null;
    SupportMapFragment mapFragment;
    Spinner spnIdUser;
    Spinner spnSelector;
    private GoogleMap mMap = null;

    public final String TAG = this.getClass().getSimpleName();

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    public static final int LOADER_LOCATION = 1;
    public static final int LOADER_USERS = 2;
    private int nowSelector = 0;
    private int nowIdUser = 0;
    int[] selectorsArray;
    private boolean viewMarkers = false;

    final private LatLng startLoc = new LatLng(55.0415, 82.9346);
    private LocationManager mLocManager = null;
    Location nowLocation = null;
    private Loader<ArrayList<LocationClass>> mLoader;
    private LocationTools locationTools;
    private DataBase db;
    private List<UserClass> users;

    public static void openActivity(Context context){
        Intent intent = new Intent(context, MapActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        /*if (!(context instanceof Activity))
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);*/
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("init", "onCreate");
        setContentView(R.layout.activity_map);
        selectorsArray = getResources().getIntArray(R.array.selectors_id_array);
        users = new ArrayList<>();
        initViews();
        setClickListener();
        //mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //locationTools = new LocationTools(this);
        //RelocationUtilities.scheduleChargingReminder(this);
        db = new DataBase();

    }

    private void initViews() {
        tvGpsLocation = (TextView) findViewById(R.id.tv_gps_location);
        tvMyId = (TextView) findViewById(R.id.tv_my_id);
        btnLocation = (Button) findViewById(R.id.btn_location);
        spnIdUser = (Spinner) findViewById(R.id.spn_users);
        spnSelector = (Spinner) findViewById(R.id.spn_selectors);
        FragmentManager myFragmentManager = getSupportFragmentManager();
        mapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        //setSpinners();
        //initiateMaps();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создаем новый CursorLoader с нужными параметрами
        /*Loader<ArrayList<LocationClass>> mLoader = null;
        // условие можно убрать, если вы используете только один загрузчик
        if (id == LOADER_ID) {
            mLoader = new LocationLoader(this, args);
            Log.d(TAG, "onCreateLoader");
        }*/
        switch (id) {
            case LOADER_USERS:
                return new UserLoader(this, args);
            case LOADER_LOCATION:
                return new LocationLoader(this, args);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_USERS) {
            if (data != null) {
                //List<UserClass> airports = UserClass_Table.listFromCursor(data);
                users = DataBase.getAcceptedUsers();
                /* while (data.moveToNext()) {
                    int idUser = data.getInt(data.getColumnIndex(DataBase.USER_COLUMN_ID));
                    String nameUser = data.getString(data.getColumnIndex(DataBase.USER_COLUMN_NAME));
                    int typeUser = data.getInt(data.getColumnIndex(DataBase.USER_COLUMN_TYPE));
                    users.add(new UserClass(idUser, nameUser, typeUser));
                }*/
                setSpinners();
                //do something here
            }
        }
        if (id == LOADER_LOCATION) {
            if (data != null) {
                List<LocationClass> locations = new ArrayList<>();
                if(data.moveToFirst())
                {

                    if(nowSelector == 1){
                        locations = db.getLastLocation(nowIdUser);
                    }
                    else {
                        locations = db.getLocationForDay(nowIdUser);
                    }

                }

                if(locations!=null && locations.size()>0) {
                    for (LocationClass location : locations) {
                        if (location != null)
                            setMarker(location);
                    }
                }
                else {
                    Toast.makeText(MapActivity.this, "Нет данных", Toast.LENGTH_SHORT).show();
                }

            }
        }
        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // Вызовется, когда загрузчик закончит свою работу. Вызывается в основном потоке
    // Может вызываться несколько раз при изменении данных
    // Также вызывается при поворотах
    /*@Override
    public void onLoadFinished(Loader<ArrayList<LocationClass>> loader, ArrayList<LocationClass> data) {
        Log.d(TAG, "onLoadFinished");
        mLoader.reset();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
        if(viewMarkers && data.size()>0)
        {
            mMap.clear();
            for (int i = 0; i < data.size(); i++)
                setMarker(data.get(i));
        }
        viewMarkers = false;
    }*/

    // Вызовется при уничтожении активности
    /*@Override
    public void onLoaderReset(Loader<ArrayList<LocationClass>> loader) {
        Log.d(TAG, "onLoaderReset");
    }*/

    public void testClick(View view) {
        nowLocation = locationTools.getNowLocation(getApplicationContext());
        if(nowLocation!=null) {
            int id = 1;
            setLocationData(id, nowLocation);
        }
    }
/*
    private final LocationListener mLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            nowLocation=location;
            tvGpsLocation.setText(nowLocation.getLatitude()+" - " +nowLocation.getLongitude()+" Изменено");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };*/

    private void initMap() {

    }

    private void setClickListener() {
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowLocation = locationTools.getNowLocation(getApplicationContext());
                if(nowLocation!=null) {
                    tvGpsLocation.setText(nowLocation.getLatitude() + " - " + nowLocation.getLongitude() + " Изменено");
                    LocationClass myLocation = new LocationClass(nowLocation);
                    mMap.clear();
                    setMarker(myLocation);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Enable the my-location layer in the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Disable my-location button
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLoc, 11F));
        getSupportLoaderManager().initLoader(LOADER_USERS, Bundle.EMPTY, this);
    }

    private void setMarker(LocationClass location) {
        LatLng target = new LatLng(location.getLocationLatitude(), location.getLocationLongitude());
        mMap.addMarker(new MarkerOptions().position(target).title(location.getStringDateTime()));
        if (target == null) return;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 15F));
    }

    //SELECT
    private void onChangeSelector(int idUser, int selectCondition){
        mMap.clear();
        viewMarkers = true;
        Bundle bundle = new Bundle();
        bundle.putInt(LocationLoader.KEY_USER_ID, idUser);
        nowSelector = selectCondition;
        nowIdUser = idUser;
        getSupportLoaderManager().initLoader(LOADER_LOCATION, bundle, this);
        //getLocationData(selectCondition, idUser);
    }

    private void setLocationData(int id, Location location) {
        if (location != null) {
            LocationClass locationClass = new LocationClass(App.iam.getId(), location.getLatitude(), location.getLongitude());
                App.getApi().setLocation(locationClass.getIdUser(), location.getLatitude(), location.getLongitude(),locationClass.getDateTimeMils()).
                        enqueue(new Callback<ResponseResult>() {
                    @Override
                    public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                        if (response.isSuccessful()) {
                            /*im.setId(response.body().getId());
                            im.setName("");
                            im.setType(1);
                            im.save();*/
                            Log.d("setLocationData", "ID="+response.body());

                        }
                        else {
                            Log.d("setLocationData", "CATCH "+response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseResult> call, Throwable t) {
                        Log.d("setLocationData", "onFailure "+t.getMessage());
                        //Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                    }
                });

            /*
            Bundle bundle = new Bundle();
            bundle.putInt(LocationLoader.KEY_TYPE_OPERATION, DataBase.TYPE_OPERATION_INSERT);
            bundle.putInt(LocationLoader.KEY_SELECT_CONDITION, 0);
            bundle.putInt(LocationLoader.KEY_USER_ID, id);
            bundle.putDouble(LocationLoader.KEY_LATITUDE, location.getLatitude());
            bundle.putDouble(LocationLoader.KEY_LONGITUDE, location.getLongitude());

            mLoader = getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);*/
        }
    }
   /* private void getLocationData(int selectCondition, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(LocationLoader.KEY_TYPE_OPERATION, DataBase.TYPE_OPERATION_SELECT);
        bundle.putInt(LocationLoader.KEY_SELECT_CONDITION, selectCondition);
        bundle.putInt(LocationLoader.KEY_USER_ID, id);
        // Инициализируем загрузчик с идентификатором
        // Если загрузчик не существует, то он будет создан,
        // иначе он будет перезапущен.
        mLoader = getSupportLoaderManager().initLoader(LOADER_LOCATION, bundle, this);
    }*/

    private void setSpinners() {
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.users_array, android.R.layout.simple_spinner_dropdown_item);
        //List<UserClass> users = db.getUsersList();
        ArrayAdapter<UserClass> adapter =
                new ArrayAdapter<UserClass>(this,  android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spnIdUser.setAdapter(adapter);
        spnIdUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.v("onItemSelected", view.toString());
                UserClass user = (UserClass)spnIdUser.getSelectedItem();
                Log.d("onItemSelected", ""+user.getId());
                int selector = selectorsArray[spnSelector.getSelectedItemPosition()];
                //int userid = Integer.parseInt(parent.getItemAtPosition(position).toString());
                onChangeSelector(user.getId(), selector);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.v("onNothingSelected", parent.toString());
            }
        });

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.selectors_array, android.R.layout.simple_spinner_dropdown_item);
        spnSelector.setAdapter(adapter1);
        spnSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selector = selectorsArray[position];
                UserClass user = (UserClass)spnIdUser.getSelectedItem();
                onChangeSelector(user.getId(), selector);
                Log.v("onItemSelected", view.toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
