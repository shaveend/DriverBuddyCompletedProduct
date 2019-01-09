package com.example.michelle.driverbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverFineDetails extends AppCompatActivity {

    public Button to_pay_button;
    EditText nic_1,vehicle_no,offence,amount;
    static String f_name,l_name,email_1,offence_1,objectid_1,dname_1;
    static int mobile_1,amount_1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_fine_details);
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        to_pay_button();

        nic_1 = (EditText)findViewById(R.id.driver_nic);
        vehicle_no =(EditText)findViewById(R.id.driver_vehicleno);
        offence =(EditText)findViewById(R.id.driver_offence_1);
        amount = (EditText)findViewById(R.id.driver_amount_fine);

        sendNetworkRequestForGetRecentTicket();

    }

    @Override
    protected void onResume() {
        super.onResume();

        nic_1 = (EditText)findViewById(R.id.driver_nic);
        vehicle_no =(EditText)findViewById(R.id.driver_vehicleno);
        offence =(EditText)findViewById(R.id.driver_offence_1);
        amount = (EditText)findViewById(R.id.driver_amount_fine);

        nic_1.setText(" ");
        vehicle_no.setText(" ");
        offence.setText(" ");
        amount.setText(" ");
    }

    public void sendNetworkRequestForGetRecentTicket() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        Api fine_details = retrofit.create(Api.class);

        SharedPreferences preferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
        String nic=preferences.getString("Nic","Null");
        dname_1 = preferences.getString("Name","Null");
        Call<FineTicket> call = fine_details.viewRecentUnpaidFineTicket(nic);
        call.enqueue(new Callback<FineTicket>() {

            @Override
            public void onResponse(Call<FineTicket> call, Response<FineTicket> response) {
                nic_1.setText(response.body().getDriver()[0].getNic());
                vehicle_no.setText(response.body().getVehicleNumber());
                offence.setText(response.body().getFine()[0].getDescription());
                amount.setText(String.valueOf(response.body().getAmount()));

                f_name = response.body().getDriver()[0].getFirstName();
                l_name = response.body().getDriver()[0].getLastName();
                email_1 = response.body().getDriver()[0].getEmail();
                mobile_1 = response.body().getDriver()[0].getMobile();
                amount_1 = response.body().getAmount();
                offence_1 = response.body().getFine()[0].getName();
                objectid_1= response.body().getObjectId();



            }

            @Override
            public void onFailure(Call<FineTicket> call, Throwable t) {
                Toast.makeText(DriverFineDetails.this,"No un-paid fines",Toast.LENGTH_LONG).show();
                //nic_1.setError(String.valueOf(t));
            }
        });

    }

    public void to_pay_button()
    {
        to_pay_button=(Button)findViewById(R.id.to_pay_button);
        to_pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent(DriverFineDetails.this,Payment.class);
                intent.putExtra("fname",f_name );
                intent.putExtra("lname", l_name);
                intent.putExtra("email", email_1);
                intent.putExtra("mobile", mobile_1);
                intent.putExtra("offence", offence_1);
                intent.putExtra("amount", amount_1);
                intent.putExtra("id", objectid_1);
                intent.putExtra("Name",dname_1);
                startActivity(intent);
                //Toast.makeText(DriverFineDetails.this,f_name,Toast.LENGTH_LONG).show();
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
