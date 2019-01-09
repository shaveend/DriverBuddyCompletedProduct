package com.example.michelle.driverbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class insurance_edit_profile extends AppCompatActivity {

   public Button but4;
   TextView firstName,lastName,email,mobile;

   public void init()
   {
       but4=(Button)findViewById(R.id.edit_done);
       but4.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent d=new Intent(insurance_edit_profile.this,insurance_profile.class);
           }
       });
   }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_edit_profile);
        init();
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName=findViewById(R.id.edit_fname);
        lastName=findViewById(R.id.edit_lname);
        email=findViewById(R.id.edit_email);
        mobile=findViewById(R.id.edit_mobile);

        SharedPreferences preferences = getSharedPreferences("insuranceDetails",MODE_PRIVATE);
        String name[]=preferences.getString("Name","N/A").split(" ");
        firstName.setText(name[0]);
        lastName.setText(name[1]);
        email.setText(preferences.getString("Email","N/A"));
        mobile.setText(String.valueOf(preferences.getInt("Mobile",0)));

        but4=findViewById(R.id.edit_done);
        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean next=true;
                String emailText=email.getText().toString();

                if(firstName.getText().length()==0)
                {
                    firstName.setError("First Name Should Not Be Empty");
                    next=false;
                }
                if(lastName.getText().length()==0)
                {
                    lastName.setError("Last Name Should Not Be Empty");
                    next=false;
                }
                if(emailText.length()==0||!emailText.contains("@"))
                {
                    if(emailText.length()==0)
                        email.setError("Email Should Not Be Empty");
                    else
                        email.setError("Email Should Contain @");
                    next=false;
                }
                if(mobile.getText().length()==0|| !TextUtils.isDigitsOnly(mobile.getText()))
                {
                    if(mobile.getText().length()==0)
                        mobile.setError("Mobile Should Not Be Empty");
                    else
                        mobile.setError("Mobile Should Only Contain Digits");
                    next=false;
                }
                if(next) {
                    SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
                    String name[] = preferences.getString("Name", "N/A").split(" ");
                    Insurance insurance = new Insurance(
                            preferences.getString("Nic", "N/A"),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            Integer.parseInt(mobile.getText().toString()),
                            email.getText().toString(),
                            preferences.getString("AgentId", "N/A")

                    );

                    sendNetworkRequestForEdit(insurance);
                }
            }
       });
    }

    public void sendNetworkRequestForEdit(Insurance insurance)
    {
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();

        Api editInsuranceProfile=retrofit.create(Api.class);
        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        String token=preferences.getString("Token","Null");
        Call<Insurance> call=editInsuranceProfile.editInsuranceProfile(token,insurance);
        call.enqueue(new Callback<Insurance>() {
            @Override
            public void onResponse(Call<Insurance> call, Response<Insurance> response) {
                Toast.makeText(insurance_edit_profile.this,"Sucessfully Changed User Data",Toast.LENGTH_SHORT).show();
                save();
            }

            @Override
            public void onFailure(Call<Insurance> call, Throwable t) {
                Toast.makeText(insurance_edit_profile.this,"Unsucessfull,Cannot Change User Data",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void save()
    {
        SharedPreferences preferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name", firstName.getText().toString()+" "+lastName.getText().toString());
        editor.putString("Email", email.getText().toString());
        editor.putInt("Mobile", Integer.parseInt(mobile.getText().toString()));
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class insurance_popwindow {
    }
}
