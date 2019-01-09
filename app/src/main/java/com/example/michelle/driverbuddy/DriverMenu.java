package com.example.michelle.driverbuddy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView name, email, license;
    String fbNic, fbEmail;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mlocationRequest;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "DriverMenu";
    private double myLat;
    private double myLon;

    DatabaseReference myRef;
    DatabaseReference distressref;


    public Button payfine_bttn;
    public Button report_accident_button;
    public Button distress_button;

    public void setDistress_bttn() {
        distress_button = (Button) findViewById(R.id.Distressbutton);
        distress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(DriverMenu.this)
                        .setTitle("Confirm Distress")
                        .setMessage("Are You in Distress?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                ;

                                if (ActivityCompat.checkSelfPermission(DriverMenu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverMenu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    return;
                                }
                                mFusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(DriverMenu.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {

                                                Log.d(TAG, "onSuccess: Location null");


                                                if (location != null) {
                                                    myLat=location.getLatitude();
                                                    myLon=location.getLongitude();
                                                    distressref.child(fbNic).setValue(new DistressReport(fbNic,myLat,myLon,1));
                                                    Log.d(TAG, "onSuccess: Firebase Distress updated" + fbNic +" "+myLat+" "+myLon);
                                                }
                                            }
                                        });



                                new AlertDialog.Builder(DriverMenu.this)
                                        .setTitle("Distress Recorded")
                                        .setMessage("An Officer will respond to you shortly")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {



                                            }
                                        })
                                        .create()
                                        .show();




                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create()
                        .show();
            }
        });

    }

    public void setPayfine_bttn()
    {
        payfine_bttn=(Button)findViewById(R.id.payfine_bttn);
        payfine_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextActivity= new Intent(DriverMenu.this,DriverFineDetails.class);
                startActivity(nextActivity);
            }
        });
    }
    public void setReportaccident_button(){
        report_accident_button=(Button)findViewById(R.id.respondaccidentbttn) ;
        report_accident_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(DriverMenu.this)
                        .setTitle("Confirm Accident")
                        .setMessage("Have You Encountered an Accident?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent nextActivity= new Intent(DriverMenu.this,DriverAccidentReport.class);
                                startActivity(nextActivity);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                             }
                        })
                        .create()
                        .show();


            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_menu);
        setPayfine_bttn();
        setDistress_bttn();
        setReportaccident_button();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        distressref=database.getReference("Distress");




        name=findViewById(R.id.driverProfileName);
        email=findViewById(R.id.driverProfileEmail);
        license=findViewById(R.id.driverProfileLicense);

        SharedPreferences preferences = getSharedPreferences("driverDetails",MODE_PRIVATE);
        name.setText(preferences.getString("Name","N/A"));
        email.setText(preferences.getString("Email","N/A"));
        license.setText(preferences.getString("License","N/A"));

        fbNic=preferences.getString("Nic","N/A");
        fbEmail=preferences.getString("Email","N/A");

        mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(10000);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        fetchlocation();

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(preferences.getBoolean("Once",false)) {
            sendNetworkRequestGetTickets(preferences.getString("Nic", "Null"));
        }
        else
        {
            TextView conduct=findViewById(R.id.driverMenuConductTextVIew);
            String conductString=preferences.getString("Conduct","N/A");
            if(conductString.equals("GOOD"))
            {
                conduct.setText(conductString);
                conduct.setBackgroundColor(Color.GREEN);
            }
            else if(conductString.equals("Average"))
            {
                conduct.setText(conductString);
                conduct.setBackgroundColor(Color.YELLOW);
            }
            else
            {
                conduct.setText(conductString);
                conduct.setBackgroundColor(Color.RED);
            }


        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_histroy:
                Intent newActivity= new Intent(DriverMenu.this,FineHistory.class);
                startActivity(newActivity);
                break;

            case R.id.nav_setting:
                Intent driverEditActivity= new Intent(DriverMenu.this,DriverProfileEditActivity.class);
                startActivity(driverEditActivity);
                break;

            case R.id.nav_logout:
                Intent logOutActivity=new Intent(DriverMenu.this,Logging.class);
                startActivity(logOutActivity);
                finish();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendNetworkRequestGetTickets(String nic)
    {
        Toast.makeText(DriverMenu.this,"Loading....",Toast.LENGTH_SHORT).show();
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        Api api=retrofit.create(Api.class);
        Call<ArrayList<FineTicket>> call=api.getCurrentMonthlyTickets(nic);
        call.enqueue(new Callback<ArrayList<FineTicket>>() {
            @Override
            public void onResponse(Call<ArrayList<FineTicket>> call, Response<ArrayList<FineTicket>> response) {
                int length=response.body().size();
                final String conductTextView;
                TextView conduct=findViewById(R.id.driverMenuConductTextVIew);
                if(length>=0 && length<=3)
                {
                    conduct.setBackgroundColor(Color.GREEN);
                    conduct.setText("Good");
                    conductTextView="Good";
                }
                else if(length>3 && length<=10)
                {
                    conduct.setBackgroundColor(Color.YELLOW);
                    conduct.setText("Average");
                    conductTextView="Average";
                }
                else
                {
                    conduct.setBackgroundColor(Color.RED);
                    conduct.setText("Bad");
                    conductTextView="Bad";
                }
                SharedPreferences preferences = getSharedPreferences("driverDetails",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("Once",false);
                editor.putString("Conduct",conductTextView);
                editor.commit();


            }


            @Override
            public void onFailure(Call<ArrayList<FineTicket>> call, Throwable t) {
                Toast.makeText(DriverMenu.this,"Something Went Wrong"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fetchpermission();

        }
        else {
            // Permission has already been granted
            mFusedLocationClient.requestLocationUpdates(mlocationRequest,new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    double flat=(locationResult.getLastLocation().getLatitude());
                    double flong=(locationResult.getLastLocation().getLongitude());
                    updatefirebasedatabase(flat,flong);

                }
            },getMainLooper());
        }
    }

    private void updatefirebasedatabase(double lat, double longt) {
        Firebaseuser driveruser=new Firebaseuser(fbEmail,lat,longt);
        Log.d(TAG, "updatefirebasedatabase: " +driveruser.getF_email()+"  "+driveruser.getLat()+"  "+driveruser.getLon() +" "+fbNic);
       /* myRef.child(fbNic).child("email").setValue(driveruser.getF_email());
        myRef.child(fbNic).child("lon").setValue(driveruser.getLon());
        myRef.child(fbNic).child("lat").setValue(driveruser.getLat());*/
        myRef.child(fbNic).setValue(driveruser);

    }

    private void fetchpermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Required Location Permission")
                    .setMessage("You have to give this permission to access this feature")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(DriverMenu.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }
                    })
                    .create()
                    .show();
        } else {
            // No explanation needed; request the permission

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                fetchlocation();
            }else{
                fetchpermission();
            }
        }
    }



}
