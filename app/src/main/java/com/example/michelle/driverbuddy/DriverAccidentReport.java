package com.example.michelle.driverbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverAccidentReport extends AppCompatActivity {

    private static final String TAG = "DriverAccidentReport";
    public Button track_i_agent;
    String fbNic;
    DatabaseReference myRef ;
    DatabaseReference agentRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_accident_report);

        setTrack_i_agentbtton();;

        SharedPreferences preferences = getSharedPreferences("driverDetails",MODE_PRIVATE);
        fbNic=preferences.getString("Nic","N/A");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Accident");
        agentRef = database.getReference().child("Tracking Agent").child(fbNic);
        update_a_database();

    }

    public void setTrack_i_agentbtton()
    {
        track_i_agent=(Button)findViewById(R.id.getDDriverbutton);
        track_i_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_agent_status();

            }
        });
    }

    public void update_a_database(){
        AccidentStatus accident = new AccidentStatus(1);
        myRef.child(fbNic).setValue(accident);
    }

    public void get_agent_status(){

        // Read from the database
        agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AgentStatus agent= dataSnapshot.getValue(AgentStatus.class);
                if(agent != null ){
                    if(agent.getStatus()!=0) {
                        //go to next activity
                        Log.d(TAG, "onDataChange: " + agent.getStatus());
                        Intent nextActivity= new Intent(DriverAccidentReport.this,DriversMapsActivity.class);
                        startActivity(nextActivity);
                    }
                    else{
                        Toast.makeText(DriverAccidentReport.this, "An agent is yet to be allocated",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(DriverAccidentReport.this, "An agent is yet to be allocated",
                            Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
