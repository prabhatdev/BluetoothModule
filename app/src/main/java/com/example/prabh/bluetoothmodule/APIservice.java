package com.example.prabh.bluetoothmodule;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIservice {
    public static API api;
    public static API getService()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(API.BASEURL)
                .build();
        api=retrofit.create(API.class);
        return api;
    }
}
