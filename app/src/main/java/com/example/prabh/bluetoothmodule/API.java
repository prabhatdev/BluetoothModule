package com.example.prabh.bluetoothmodule;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {
    String BASEURL = "http://192.168.43.167/wisoptServer/";
    @FormUrlEncoded
    @POST("checkid.php/")
     Call<String> insertdata(
            @Field("name") String name,
            @Field("registernumber") String number);

}
