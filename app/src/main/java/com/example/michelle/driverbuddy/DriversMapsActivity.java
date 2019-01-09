package com.example.michelle.driverbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriversMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "DriversMapActivity";
    private GoogleMap mMap;
    DatabaseReference agentRef;
    DatabaseReference myRef;
    String fbNic;
    Firebaseuser ins_agent;
    LatLng ins_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SharedPreferences preferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
        fbNic = preferences.getString("Nic", "N/A");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        agentRef = database.getReference().child("Tracking Agent").child(fbNic);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // no access to gps location
            return;
        }
        mMap.setMyLocationEnabled(true);

        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AgentStatus agent= dataSnapshot.getValue(AgentStatus.class);
                String agentNic=agent.getnic();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myRef=database.getReference().child("Users").child(agentNic);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //change location of marker in map
                        mMap.clear();
                        ins_agent=dataSnapshot.getValue(Firebaseuser.class);
                        ins_location=new LatLng(ins_agent.getLat(),ins_agent.getLon());
                        mMap.addMarker(new MarkerOptions().position(ins_location).title("Insurance Agent"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ins_location));



                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });






            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Add a marker in Sydney and move the camera

       // LatLng sydney = new LatLng(-34, 151);

    }
}
