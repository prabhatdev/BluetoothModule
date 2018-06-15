package com.example.prabh.bluetoothmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPage extends AppCompatActivity {

    public EditText nameText, registernumberText, useridText;
    public CardView register;
    public RelativeLayout relativelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        nameText = (EditText) findViewById(R.id.name);
        registernumberText = (EditText) findViewById(R.id.registernumber);
        useridText = (EditText) findViewById(R.id.userId);
        register = (CardView) findViewById(R.id.register);
        relativelayout = (RelativeLayout) findViewById(R.id.relativelayout);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccount();
            }
        });

    }

    void registerAccount() {

        String n = nameText.getText().toString();
        String r = registernumberText.getText().toString();
        String u = useridText.getText().toString();
        if (!n.isEmpty() && !r.isEmpty() && !u.isEmpty()) {
            Call<String> call = APIservice.getService().register(n, u, r);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String message = response.body().toString();
                    if (message.equals("true")) {
                        snackbarMessage("Id Successfully created");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(RegisterPage.this, LoginPage.class);
                                startActivity(i);
                            }
                        }, 2500);
                    } else if (message.equals("false")) {
                        snackbarMessage("The UserId already exists!");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    snackbarMessage("Some unknown error occurred!");
                }
            });
        } else {
            snackbarMessage("Please enter the Sign up details");
        }
    }

    void snackbarMessage(String m) {
        Snackbar snackbar = Snackbar.make(relativelayout, m, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
