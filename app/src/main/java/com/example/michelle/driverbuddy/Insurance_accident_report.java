package com.example.michelle.driverbuddy;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Insurance_accident_report extends AppCompatActivity {

    CheckBox enterCheck, viewCheck;
    Button search, getLocation, store;
    EditText nic, name, longitude, latitude,insuranceNumber,vehicleNumber,damageDescription;


    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_accident_report);

        enterCheck = (CheckBox)findViewById(R.id.accident_report_enter);
        viewCheck = (CheckBox)findViewById(R.id.accident_report_view);


        getLocation = (Button) findViewById(R.id.insurance_accident_report_getLocation_button);
        store = (Button) findViewById(R.id.insurance_accident_report_store_button);
        search = (Button) findViewById(R.id.insurance_accident_report_search_button);
        nic = (EditText) findViewById(R.id.insurance_accident_report_search_nic);
        insuranceNumber=(EditText) findViewById(R.id.insurance_accident_report_search_insuranceNumber);
        vehicleNumber=(EditText) findViewById(R.id.insurance_accident_report_number);
        damageDescription=(EditText) findViewById(R.id.insurance_accident_report_damage);
        longitude = findViewById(R.id.insurance_accident_report_longitude);
        latitude = findViewById(R.id.insurance_accident_report_latitude);

        enterCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    viewCheck.setEnabled(false);
                }
                if(!b) {
                    viewCheck.setEnabled(true);
                }
            }
        });

        viewCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    enterCheck.setEnabled(false);
                }
                if(!b)
                {
                    enterCheck.setEnabled(true);

                }
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nicText = nic.getText().toString().toLowerCase();
                if (!nicText.contains("v")) {
                    nic.setError("Invalid NIC");
                } else {
                    sendNetworkRequestDriver(nicText);
                }


            }
        });

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(enterCheck.isChecked())
                {
                    enterAccident();
                }
                else if(viewCheck.isChecked())
                {
                    viewAccident();
                }

            }
        });
    }

    public void viewAccident()
    {
        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        String agentId=preferences.getString("AgentId","Null");
        sendNetworkRequestViewReport(nic.getText().toString().trim(),agentId);
    }

    public void enterAccident()
    {

        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        String agentId=preferences.getString("AgentId","Null");
        String nicText = nic.getText().toString().toLowerCase().trim();
        String insuranceNumberText=insuranceNumber.getText().toString().trim();
        String vehicleNumberText=vehicleNumber.getText().toString().trim();
        String damage=damageDescription.getText().toString().trim();
        String place=String.valueOf(longitude.getText()).trim()+" "+String.valueOf(latitude.getText().toString()).trim();
        if (!nicText.contains("v")) {
            nic.setError("Invalid NIC");
        }
        if(insuranceNumberText.length()==0)
        {
            insuranceNumber.setError("Insurance Number Should Not Be Empty");
        }
        if(vehicleNumberText.length()==0)
        {
            vehicleNumber.setError("Vehicle Number Should Not Be Empty");
        }
        if(damage.length()==0)
        {
            damageDescription.setError("Damage Description Should Not Be Empty");
        }
        else
        {
            AccidentReport accidentReport=new AccidentReport(
                    vehicleNumberText,
                    place,
                    damage,
                    insuranceNumberText,
                    nicText,
                    agentId
            );

            sendNetworkRequestEnterReport(accidentReport);
        }
    }

    public void sendNetworkRequestDriver(String nic) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();
        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        String token = preferences.getString("Token", "Null");
        final Api checkDriver = retrofit.create(Api.class);
        Call<Driver> call = checkDriver.getDriver(token, nic);
        call.enqueue(new Callback<Driver>() {
            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {
                name = (EditText) findViewById(R.id.insurance_accident_report_name);
                name.setText(response.body().getFirstName() + " " + response.body().getLastName());
            }

            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                Toast.makeText(Insurance_accident_report.this, "Something Went Wrong " + t, Toast.LENGTH_LONG).show();

            }
        });
    }

    public void sendNetworkRequestEnterReport(AccidentReport accidentReport)
    {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();
        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        String token = preferences.getString("Token", "Null");
        final Api enterAccidentReport = retrofit.create(Api.class);
        Call<AccidentReport> call=enterAccidentReport.enterAccidentReport(token,accidentReport);
        call.enqueue(new Callback<AccidentReport>() {
            @Override
            public void onResponse(Call<AccidentReport> call, Response<AccidentReport> response) {
                Toast.makeText(Insurance_accident_report.this,"Accident Report Sucessfully Entered To The Database", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<AccidentReport> call, Throwable t) {
                Toast.makeText(Insurance_accident_report.this, "Accident Report Insertion Unsucessfull" + t, Toast.LENGTH_LONG).show();
            }
        });

    }

    public void sendNetworkRequestViewReport(String nic,String agentId)
    {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();
        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        String token = preferences.getString("Token", "Null");
        final Api viewAccidentReport = retrofit.create(Api.class);
        Call<AccidentReport> call=viewAccidentReport.viewAccidentReport(token,nic,agentId);
        call.enqueue(new Callback<AccidentReport>() {
            @Override
            public void onResponse(Call<AccidentReport> call, Response<AccidentReport> response) {
                if(response.body().getDriver()!=null) {
                    String[] text = response.body().getPlace().split(" ");
                    insuranceNumber.setText(response.body().getInsuranceNumber());
                    longitude.setText(text[0]);
                    latitude.setText(text[1]);
                    vehicleNumber.setText(response.body().getVehicleNo());
                    damageDescription.setText(response.body().getDescription());
                    SharedPreferences preferences=getSharedPreferences("insuranceDetails",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("Once",true);
                    editor.commit();
                }

            }

            @Override
            public void onFailure(Call<AccidentReport> call, Throwable t) {
                Toast.makeText(Insurance_accident_report.this, "Accident Report Insertion Unsucessfull" + t, Toast.LENGTH_LONG).show();
            }
        });

    }





    public void getCurrentLocation() {
        longitude = findViewById(R.id.insurance_accident_report_longitude);
        latitude = findViewById(R.id.insurance_accident_report_latitude);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude.setText(String.valueOf(location.getLongitude()));
                latitude.setText(String.valueOf(location.getLatitude()));

                longitude.setFocusable(false);
                latitude.setFocusable(false);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
