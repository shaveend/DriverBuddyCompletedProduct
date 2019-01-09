package com.example.michelle.driverbuddy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckLicense extends AppCompatActivity {

    EditText checkLicense;
    Button checkLicenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_license);
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkLicense=(EditText)findViewById(R.id.checkLicenseEditText);
        checkLicenseButton=(Button)findViewById(R.id.getDDriverbutton);

        checkLicenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String license=checkLicense.getText().toString().trim().toLowerCase();
                if(license.length()==0||license.contains("v"))
                    checkLicense.setError("Invalid License Number");
                else
                    sendNetworkRequest(license);
            }
        });

    }

    public void sendNetworkRequest(final String license)
    {
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();
        SharedPreferences preferences = getSharedPreferences("policeDetails", MODE_PRIVATE);
        String token=preferences.getString("Token","Null");
        final Api checkLicense=retrofit.create(Api.class);
        Call<Driver>call=checkLicense.checkLicense(token,license);
        call.enqueue(new Callback<Driver>() {
            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {
                Toast.makeText(CheckLicense.this, "Driver Is Registered In The System", Toast.LENGTH_LONG).show();


            }

            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                String error=t.getMessage().trim();
                if(error.contains("End of input"))
                {
                    Toast.makeText(CheckLicense.this, "Driver Is Not Registered In The System", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(CheckLicense.this,"Something Went Wrong "+t,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
