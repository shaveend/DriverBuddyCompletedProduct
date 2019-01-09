package com.example.michelle.driverbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RespondDistress extends AppCompatActivity {


    private static final String TAG = "RespondDistress";
    public Button button_track_distress_driver;
    FirebaseDatabase database;
    DatabaseReference myref;
    private FusedLocationProviderClient mFusedLocationClient;
    double policeLat;
    double policeLon;
    ArrayList<DistressReport> detailarr=new ArrayList<>();
    ArrayList<String> namearr=new ArrayList<>();
    ListView listView;

    float distance;
    Location userloc=new Location("Point A");

    Location driverloc=new Location("Point B");

    ArrayAdapter arrayAdapter;




    public void track_distress_button() {
        button_track_distress_driver = (Button) findViewById(R.id.button_track_distress_driver);

        button_track_distress_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(RespondDistress.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RespondDistress.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(RespondDistress.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {

                                    policeLat=location.getLatitude();
                                    policeLon=location.getLongitude();

                                }
                            }
                        });
                setPoliceView();







            }



        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_distress);
        listView=(ListView) findViewById(R.id.listView);
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        track_distress_button();
        database=FirebaseDatabase.getInstance();
        myref=database.getReference("Distress");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPoliceView(){

        userloc.setLatitude(policeLat);
        userloc.setLongitude(policeLon);




        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> items=dataSnapshot.getChildren().iterator();
                detailarr.clear();
                namearr.clear();

                DistressReport distresseduser;

                while (items.hasNext()){
                    DataSnapshot item=items.next();
                    distresseduser=item.getValue(DistressReport.class);

                    if(distresseduser != null){
                        if(distresseduser.getStatus()==1){

                            driverloc.setLatitude(distresseduser.getMyLat());
                            driverloc.setLongitude(distresseduser.getMyLon());

                            distance=userloc.distanceTo(driverloc);

                            if(distance!=999999){
                                detailarr.add(distresseduser);
                                namearr.add(distresseduser.getDriversNic());

                            }
                        }
                    }




                }
                ArrayAdapter arrayAdapter=new ArrayAdapter(RespondDistress.this,android.R.layout.simple_list_item_1,namearr);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        DistressReport goingtoMap= detailarr.get(position);

                        String latitude = String.valueOf(goingtoMap.getMyLat());
                        String longitude = String.valueOf(goingtoMap.getMyLon());
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try{
                            if (mapIntent.resolveActivity(RespondDistress.this.getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        }catch (NullPointerException e){
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                            Toast.makeText(RespondDistress.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }


                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
