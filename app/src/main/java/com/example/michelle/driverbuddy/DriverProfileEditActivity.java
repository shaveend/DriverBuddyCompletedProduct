package com.example.michelle.driverbuddy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverProfileEditActivity extends AppCompatActivity {

    TextView name,license,email,mobile;
    Button driverProfileEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_edit);

        name = findViewById(R.id.driverProfileEditName);
        email = findViewById(R.id.driverProfileEditEmail);
        license = findViewById(R.id.driverProfileEditLicense);
        mobile = findViewById(R.id.driverprofileEditMobile);

        SharedPreferences preferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
        name.setText(preferences.getString("Name", "N/A"));
        email.setText(preferences.getString("Email", "N/A"));
        license.setText(preferences.getString("License", "N/A"));
        mobile.setText(String.valueOf(preferences.getInt("Mobile", 0)));

        driverProfileEditButton = (Button) findViewById(R.id.driverProfileEditButton);
        driverProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean next=true;

                if(name.getText().length()==0)
                {
                    name.setError("Name Should Not Be Empty");
                    next=false;
                }
                if(email.getText().length()==0 || !email.getText().toString().contains("@"))
                {
                    if(email.getText().length()==0 )
                        email.setError("Email Should Not Be Empty");
                    else
                        email.setError("Email Should Contain @");

                    next=false;
                }
                if(license.getText().length()==0)
                {
                    license.setError("License Should Not Be Empty");
                    next=false;
                }
                if(mobile.getText().length()!=10||!TextUtils.isDigitsOnly(mobile.getText()))
                {
                    if(mobile.getText().length()!=10)
                        mobile.setError("Invalid Mobile");
                    else
                        mobile.setError("Mobile Should Only Contain Digits");
                    next=false;
                }
                if(next) {
                    SharedPreferences preferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
                    String[] nameArray = name.getText().toString().split(" ");
                    String nic = preferences.getString("Nic", "N/A");


                    Driver driver = new Driver(

                            nameArray[0],
                            nameArray[1],
                            email.getText().toString(),
                            nic,
                            Integer.parseInt(license.getText().toString()),
                            Integer.parseInt(mobile.getText().toString())
                    );

                    sendNetworkRequestForEdit(driver);
                }
            }
        });
    }

    public void sendNetworkRequestForEdit(final Driver driver)
    {
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();

        Api editDriverProfile=retrofit.create(Api.class);
        SharedPreferences preferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
        String token=preferences.getString("Token","Null");
        Call<Driver> call=editDriverProfile.editDriverProfile(token,driver);
        call.enqueue(new Callback<Driver>() {

            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {
               Toast.makeText(DriverProfileEditActivity.this,"Sucessfully Changed User Data",Toast.LENGTH_SHORT).show();
                //Toast.makeText(DriverProfileEditActivity.this,response.body().getObjectId(),Toast.LENGTH_SHORT).show();
               save();

            }

            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                Toast.makeText(DriverProfileEditActivity.this,"Unsucessfull,Cannot Change User Data",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save()
    {
        SharedPreferences preferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Name", name.getText().toString());
        editor.putString("License", license.getText().toString());
        editor.putString("Email", email.getText().toString());
        editor.putInt("Mobile", Integer.parseInt(mobile.getText().toString()));
        editor.commit();

    }


}
