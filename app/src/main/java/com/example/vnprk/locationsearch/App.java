package com.example.vnprk.locationsearch;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by VNPrk on 16.09.2018.
 */

public class App extends Application {

    public static final UserClass iam = new UserClass();
    private static LocationApi locationApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(AppDataBase.class)
                        .databaseName("AppDatabase")
                        .build())
                .build());

        Gson gson = new GsonBuilder() .setLenient() .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://sert-pknk-ru.1gb.ru/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        locationApi = retrofit.create(LocationApi.class); //Создаем объект, при помощи которого будем выполнять запросы
        //CheckMyId();
    }

    /*private void CheckMyId()
    {
        UserClass iam = new Select().from(UserClass.class).where(UserClass_Table.type.is(1)).querySingle();
        //UserClass ids = null;
        if(iam==null) {
            getApi().getNewId().enqueue(new Callback<UserClass>() {
                @Override
                public void onResponse(Call<UserClass> call, Response<UserClass> response) {
                    if (response.isSuccessful()) {
                        im.setId(response.body().getId());
                        im.setName("");
                        im.setType(1);
                        im.save();
                        Log.d("Retrofit CheckMyID", "ID="+response.body().getId());

                    }
                        else {
                        Log.d("Retrofit CheckMyID", "CATCH "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<UserClass> call, Throwable t) {
                    Log.d("Retrofit CheckMyID", "onFailure "+t.getMessage());
                    //Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    public static LocationApi getApi() {
        return locationApi;
    }
}
