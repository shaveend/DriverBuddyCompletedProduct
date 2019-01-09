package com.example.michelle.driverbuddy;

import android.Manifest;
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

public class insurance_profile extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView name,agentId,email;

    public Button but2,but3;
    public TextView edit;

    String fbNic,fbEmail;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mlocationRequest;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    DatabaseReference myRef ;

    public void init()
    /*{
        edit = (TextView) findViewById(R.id.textView33);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a =new Intent(insurance_profile.this,insurance_edit_profile.class);
                startActivity(a);

            }
        });*/
    {
        but3 = (Button)findViewById(R.id.respondaccidentbttn);
        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c=new Intent(insurance_profile.this,insurance_track_driver.class);
                startActivity(c);
            }
        });




    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_profile);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        name=findViewById(R.id.insuranceProfileName);
        agentId=findViewById(R.id.insuranceProfileAgentId);
        email=findViewById(R.id.insuranceProfileEmail);

        SharedPreferences preferences = getSharedPreferences("insuranceDetails",MODE_PRIVATE);
        name.setText(preferences.getString("Name","N/A"));
        email.setText(preferences.getString("Email","N/A"));
        agentId.setText(preferences.getString("AgentId","N/A"));

        init();

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout_1);
        actionBarDrawerToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fbNic=preferences.getString("Nic","N/A");
        fbEmail=preferences.getString("Email","N/A");

        mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(10000);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        fetchlocation();

        NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.acc_logout)
                {
                    final Intent c=new Intent(insurance_profile.this,Logging.class);
                    startActivity(c);
                }
                else if(id==R.id.acc_edit)
                {
                    Intent e=new Intent(insurance_profile.this,insurance_edit_profile.class);
                    startActivity(e);
                }
                else if(id==R.id.acc_histroy)
                {
                    Intent e=new Intent(insurance_profile.this,Insurance_accident_report.class);
                    startActivity(e);
                }



                return true;
            }
        });

        if(preferences.getBoolean("Once",false))
        {
            sendNetworkRequestForReportCount(preferences.getString("AgentId", "Null"));
        }
        else
        {
            TextView reportCount=findViewById(R.id.insuranceMenuReportCount);
            reportCount.setText(String.valueOf(preferences.getInt("ReportCount",0)));
        }
    }

    public void sendNetworkRequestForReportCount(String agentId)
    {
        Toast.makeText(insurance_profile.this,"Loading....",Toast.LENGTH_SHORT).show();
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();


        Api api=retrofit.create(Api.class);
        Call<ArrayList<AccidentReport>> call=api.getCurrentlyIssuedReports(agentId);
        call.enqueue(new Callback<ArrayList<AccidentReport>>() {
            @Override
            public void onResponse(Call<ArrayList<AccidentReport>> call, Response<ArrayList<AccidentReport>> response) {
                TextView reportCount=findViewById(R.id.insuranceMenuReportCount);
                reportCount.setText(String.valueOf(response.body().size()));
                SharedPreferences preferences=getSharedPreferences("insuranceDetails",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("Once",false);
                editor.putInt("ReportCount",response.body().size());
                editor.commit();
            }

            @Override
            public void onFailure(Call<ArrayList<AccidentReport>> call, Throwable t) {
                Toast.makeText(insurance_profile.this,"Something Went Wrong"+t,Toast.LENGTH_SHORT).show();
            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
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
        Firebaseuser insuranceuser=new Firebaseuser(fbEmail,lat,longt);
        myRef.child(fbNic).setValue(insuranceuser);

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
                            ActivityCompat.requestPermissions(insurance_profile.this,
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
