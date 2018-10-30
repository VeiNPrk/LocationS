package com.example.vnprk.locationsearch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by VNPrk on 16.09.2018.
 */

public interface LocationApi {
        @GET("/getnewid.php")
        Call<UserClass> getNewId();

        @GET("/getusers.php")
        Call<List<UserClass>> getUsers(@Query("id") int myId);

        @GET("/getrequests.php")
        Call<List<UserClass>> getRequests(@Query("id") int myId);

        @GET("/getlastlocation.php")
        Call<List<LocationClass>> getLocations(@Query("id") int idUser);

        @FormUrlEncoded
        @POST("/setlocation.php")
        Call<ResponseResult> setLocation(@Field("user") int idUser, @Field("latitude") double latitude,
                                         @Field("longitude") double longitude, @Field("date") long dateTime);

        @FormUrlEncoded
        @POST("/setdescribe.php")
        Call<List<UserClass>> setDescribe(@Field("id_main") int idUser, @Field("id_depend") int idDepend);

        @FormUrlEncoded
        @POST("/updatedescribe.php")
        Call<List<UserClass>> updateDescribe(@Field("id_main") int idUser, @Field("id_depend") int idDepend,
                                         @Field("status") int status);

        @FormUrlEncoded
        @POST("/settoken.php")
        Call<ResponseResult> setToken(@Field("id_user") int idUser, @Field("token") String token);
}
