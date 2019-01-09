package com.example.michelle.driverbuddy;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PoliceOfficerMenu extends AppCompatActivity {

    private DrawerLayout officer_drawer;
    private ActionBarDrawerToggle ntoggle;
    String fbNic,fbEmail;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mlocationRequest;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    DatabaseReference myRef ;
    public Button button_write_fine, button_check_license, button_distress_respond;
    NavigationView navigationView;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    TextView name,officeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_officer_menu);
        officer_drawer = (DrawerLayout)findViewById(R.id.officer_drawer);
        ntoggle = new ActionBarDrawerToggle(this,officer_drawer,R.string.open, R.string.close);

        officer_drawer.addDrawerListener(ntoggle);
        ntoggle.syncState();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        write_fine_button();
        check_license_button();
        distress_respond_button();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.nav_fine_history:
                        Intent fine = new Intent(PoliceOfficerMenu.this,FineHistoryOfficer.class);
                        startActivity(fine);
                        //item.setChecked(true);
                        officer_drawer.closeDrawers();
                        break;
                    case R.id.nav_settings:
                        Intent settings = new  Intent(PoliceOfficerMenu.this,OfficerSettings.class);
                        startActivity(settings);
                        //item.setChecked(true);
                        officer_drawer.closeDrawers();
                        break;

                    case R.id.nav_log_out:
                        Intent log_out = new  Intent(PoliceOfficerMenu.this,Logging.class);
                        startActivity(log_out);
                        //item.setChecked(true);
                        officer_drawer.closeDrawers();
                        break;


                }

                return false;
            }
        });

        name=findViewById(R.id.policeMenuName);
        officeId=findViewById(R.id.policeMenuOfficerId);

        SharedPreferences preferences = getSharedPreferences("policeDetails",MODE_PRIVATE);
        name.setText(preferences.getString("Name","N/A"));
        officeId.setText(preferences.getString("PoliceId","N/A"));
        if(preferences.getBoolean("Once",false)) {
            sendNetworkIssuedFineCount(preferences.getString("PoliceId", "N/A"));
        }
        else
        {
            TextView fineCount=findViewById(R.id.policeMenuIssuedFine);
            fineCount.setText(String.valueOf(preferences.getInt("FineCount",0)));
        }

        fbNic=preferences.getString("Nic","N/A");
        fbEmail=preferences.getString("Email","N/A");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(10000);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        fetchlocation();
    }

    public void write_fine_button()
    {
        button_write_fine=(Button)findViewById(R.id.button_write_fine);
        button_write_fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextActivity;
                nextActivity = new Intent(PoliceOfficerMenu.this,WriteFine.class);
                startActivity(nextActivity);
            }
        });
    }

    public void check_license_button()
    {
        button_check_license=(Button)findViewById(R.id.button_check_license);
        button_check_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextActivity;
                nextActivity = new Intent(PoliceOfficerMenu.this,CheckLicense.class);
                startActivity(nextActivity);
            }
        });
    }

    public void distress_respond_button()
    {
        button_distress_respond=(Button)findViewById(R.id.button_distress_respond);
        button_distress_respond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextActivity;
                nextActivity = new Intent(PoliceOfficerMenu.this,RespondDistress.class);
                startActivity(nextActivity);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ntoggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Firebaseuser policeuser=new Firebaseuser(fbEmail,lat,longt);
        myRef.child(fbNic).setValue(policeuser);

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
                            ActivityCompat.requestPermissions(PoliceOfficerMenu.this,
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

    public void sendNetworkIssuedFineCount(String policeId)
    {
        Toast.makeText(PoliceOfficerMenu.this,"Loading....",Toast.LENGTH_LONG).show();
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();

        Api api=retrofit.create(Api.class);
        Call<ArrayList<FineTicket>> call=api.getCurrentlyIssuedTickets(policeId);
        call.enqueue(new Callback<ArrayList<FineTicket>>() {
            @Override
            public void onResponse(Call<ArrayList<FineTicket>> call, Response<ArrayList<FineTicket>> response) {
                TextView fineCount=findViewById(R.id.policeMenuIssuedFine);
                fineCount.setText(String.valueOf(response.body().size()));
                SharedPreferences preferences=getSharedPreferences("policeDetails",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("Once",false);
                editor.putInt("FineCount",response.body().size());
                editor.commit();
            }

            @Override
            public void onFailure(Call<ArrayList<FineTicket>> call, Throwable t) {
                Toast.makeText(PoliceOfficerMenu.this,"Something Went Wrong"+t, Toast.LENGTH_SHORT).show();
            }
        });
    }



}
