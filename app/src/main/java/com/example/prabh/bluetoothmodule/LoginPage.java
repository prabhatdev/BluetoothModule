package com.example.prabh.bluetoothmodule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPage extends AppCompatActivity {

    public EditText nameText,registernumberText;
    public CardView submit;
    public RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_login);
        nameText=(EditText)findViewById(R.id.name);
        registernumberText=(EditText)findViewById(R.id.regNo);
        relativeLayout=(RelativeLayout)findViewById(R.id.login_page);
        submit=(CardView)findViewById(R.id.login);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid();
            }
        });
    }

    void isValid()
    {
        String message = "";
        String n=nameText.getText().toString();
        String r=registernumberText.getText().toString();
        Call<String> call=APIservice.getService().insertdata(n,r);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().toString().equals("true"))
                {
                    snackbarMessage("Login Successful");
                    Intent i=new Intent(LoginPage.this,DeviceControl.class);

                    startActivity(i);
                }
                else
                {
                    snackbarMessage("Invalid entry! Please contact Wisopt Admin");
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i=new Intent(LoginPage.this,LoginPage.class);
                            startActivity(i);
                        }
                    },2000);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                snackbarMessage("Some connection error occurred");
            }
        });
    }
    void snackbarMessage(String m)
    {
        Snackbar snackbar=Snackbar.make(relativeLayout,m,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
