package com.example.prabh.bluetoothmodule;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {
    String BASEURL = "http://13.232.30.77/wisoptServer/";
    @FormUrlEncoded
    @POST("checkid.php/")
     Call<String> insertdata(
            @Field("userId") String name,
            @Field("registernumber") String number);

    @FormUrlEncoded
    @POST("insertemployee.php/")
    Call<String>  register(
            @Field("name") String name,
            @Field("userId") String id,
            @Field("registernumber") String number);

    @FormUrlEncoded
    @POST("insertlog.php/")
    Call<String> insertlog(
            @Field("userId") String n,
            @Field("registernumber") String r,
            @Field("operation") String o,
            @Field("dateTime") String d
    );
}
