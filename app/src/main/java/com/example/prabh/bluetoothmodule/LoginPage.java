package com.example.prabh.bluetoothmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPage extends AppCompatActivity {

    public static String userId;
    public static String registernumber;
    public EditText userIdText, registernumberText;
    public CardView submit, register;
    public RelativeLayout relativeLayout;
    private SharedPreferences sharedPreferences;

    public static String getUserId() {
        return userId;
    }

    public static String getRegisternumber() {
        return registernumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_login);
        userIdText =findViewById(R.id.userId);
        registernumberText =findViewById(R.id.regNo);
        relativeLayout =findViewById(R.id.login_page);
        register =findViewById(R.id.register);
        submit =findViewById(R.id.login);
        sharedPreferences=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("firstTime","true").equals("false"))
        {
            userIdText.setText(sharedPreferences.getString("userId","notFound"));
            registernumberText.setText(sharedPreferences.getString("registernumber","not found"));
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(i);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid();
            }
        });
    }

    void isValid() {
        if (!userIdText.getText().toString().isEmpty() && !registernumberText.getText().toString().isEmpty()) {
            userId = userIdText.getText().toString();
            registernumber = registernumberText.getText().toString();
            Call<String> call = APIservice.getService().insertdata(userId, registernumber);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.body().toString().equals("true")) {
                        submitLog();
                        savedetails();
                        Intent i = new Intent(LoginPage.this, DeviceControl.class);
                        startActivity(i);
                        finish();
                    } else if (response.body().toString().equals("false")) {
                        snackbarMessage("Invalid entry! Please contact WisOpt Admin. Access denied");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    snackbarMessage("Some connection error occurred");
                }
            });
        } else {
            snackbarMessage("Please enter the details.");
        }
    }

    void snackbarMessage(String m) {
        Snackbar snackbar = Snackbar.make(relativeLayout, m, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    void savedetails()
    {
        sharedPreferences=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userId",userId);
        editor.putString("registernumber",registernumber);
        editor.putString("firstTime","false");
        editor.apply();
    }
    void submitLog() {
        String dateTime = getDateTime();
        Call<String> call = APIservice.getService().insertlog(userId, registernumber, "Logged In", dateTime);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
