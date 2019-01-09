package com.example.michelle.driverbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class insurance_track_driver extends AppCompatActivity {

    private static final String TAG = "Insuarance track driver";
    public Button get_Driver_button;
    DatabaseReference myRef;
    DatabaseReference accidentRef;
    DatabaseReference agentStatusRef;
    FirebaseDatabase database;
    TextView fbNic;
    String driver_nic;
    String agent_nic;


    public void setInsuranceDriverbutton(){
        get_Driver_button=(Button) findViewById(R.id.getDDriverbutton);
        get_Driver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_nic=fbNic.getText().toString().trim();

                if(driver_nic.length()==10){
                    accidentRef = database.getReference().child("Accident").child(driver_nic);

                    accidentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AccidentStatus checkrequest = dataSnapshot.getValue(AccidentStatus.class);
                            if(checkrequest != null ){
                                if(checkrequest.getStatus()!=0) {
                                    //User has been in an accident and given permission to track

                                    accidentRef.setValue(new AccidentStatus(0));
                                    agentStatusRef.child(driver_nic).setValue(new AgentStatus(1,agent_nic));

                                    myRef=database.getReference().child("Users").child(driver_nic);

                                    //Firebaseuser accident_driver=
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            Firebaseuser accident_driver = dataSnapshot.getValue(Firebaseuser.class);

                                            String latitude = String.valueOf(accident_driver.getLat());
                                            String longitude = String.valueOf(accident_driver.getLon());
                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            try{
                                                if (mapIntent.resolveActivity(insurance_track_driver.this.getPackageManager()) != null) {
                                                    startActivity(mapIntent);
                                                }
                                            }catch (NullPointerException e){
                                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                                Toast.makeText(insurance_track_driver.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value
                                            Log.w(TAG, "Failed to read value.", error.toException());
                                        }
                                    });





                                }
                                else{
                                    Toast.makeText(insurance_track_driver.this, "User has Not requested to be tracked",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(insurance_track_driver.this, "Invalid NIC",
                                        Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            //Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                }







                else{
                    Toast.makeText(insurance_track_driver.this, "NIC must contain 10 charachters",
                            Toast.LENGTH_LONG).show();

                }

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_track_driver);
        setInsuranceDriverbutton();
        fbNic=findViewById(R.id.driver_id);

        SharedPreferences preferences = getSharedPreferences("insuranceDetails",MODE_PRIVATE);
        agent_nic=preferences.getString("Nic","N/A");
        database = FirebaseDatabase.getInstance();
        agentStatusRef = database.getReference("Tracking Agent");

        //accidentRef = database.getReference("Accident");
        //agentStatusRef = database.getReference().child("Tracking Agent").child(fbNic);
      //  init();

        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
