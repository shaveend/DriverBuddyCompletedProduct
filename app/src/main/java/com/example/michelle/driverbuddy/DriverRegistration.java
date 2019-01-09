package com.example.michelle.driverbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverRegistration extends AppCompatActivity {

    public Button but1;
    public Button createAccountButton;


    public void init()
    {
            but1 = (Button) findViewById(R.id.userRegistrationButton);

            final EditText firstName = (EditText) findViewById(R.id.registrationFirstName);
            final EditText lastName = (EditText) findViewById(R.id.registrationLastName);
            final EditText email = (EditText) findViewById(R.id.driverProfileEditLicense);
            final EditText nic = (EditText) findViewById(R.id.driverprofileEditMobile);
            final EditText license = (EditText) findViewById(R.id.registrationLicense);
            final EditText mobile = (EditText) findViewById(R.id.registrationMobile);


            but1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   String emailText=email.getText().toString();
                   String nicText=nic.getText().toString().toLowerCase();
                   boolean next=true;

                   if(firstName.getText().length()==0)
                   {
                       firstName.setError("First Name Should Not Be Empty");
                       next=false;
                   }
                   if (lastName.getText().length()==0)
                   {
                       lastName.setError("Last Name Should Not Be Empty");
                       next=false;
                   }
                   if(!emailText.contains("@"))
                   {
                       email.setError("Email Should Contain @");
                       next=false;
                   }
                   if(nicText.length()!=10)
                   {
                       nic.setError("NIC Is Invalid");
                       next=false;
                   }
                   if (!nicText.contains("v"))
                   {
                       nic.setError("NIC Should Contain V");
                       next=false;
                   }
                   if(mobile.getText().length()!=10)
                   {
                       mobile.setError("Mobile Number Is Invalid");
                       next=false;
                   }
                   if (!TextUtils.isDigitsOnly(mobile.getText()))
                   {
                       mobile.setError("Mobile Should Only Cotain Digits");
                       next=false;
                   }
                   if(license.getText().length()==0)
                   {
                       license.setError("License Should Not Be Empty");
                       next=false;

                   }
                   if(next)
                   {

                       final EditText firstName = (EditText) findViewById(R.id.registrationFirstName);
                       final EditText lastName = (EditText) findViewById(R.id.registrationLastName);
                       final EditText email = (EditText) findViewById(R.id.driverProfileEditLicense);
                       final EditText nic = (EditText) findViewById(R.id.driverprofileEditMobile);
                       final EditText license = (EditText) findViewById(R.id.registrationLicense);
                       final EditText mobile = (EditText) findViewById(R.id.registrationMobile);

                       Driver driver =new Driver(

                               firstName.getText().toString(),
                               lastName.getText().toString(),
                               email.getText().toString(),
                               nic.getText().toString(),
                               Integer.parseInt(license.getText().toString()),
                               Integer.parseInt(mobile.getText().toString())
                       );
                       sendNetworkRequest(driver);
                   }


               }
           });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        init();
    }

    public void sendNetworkRequest(Driver driver)
    {
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();

        Api driverRegistration=retrofit.create(Api.class);
        Call<Driver> call=driverRegistration.createAccount(driver);


        call.enqueue(new Callback<Driver>() {
            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {

                if(response.body().getNic()!=null) {
                    EditText userId = (EditText) findViewById(R.id.driverprofileEditMobile);
                    EditText userEmail= (EditText) findViewById(R.id.driverProfileEditLicense);
                    Toast.makeText(DriverRegistration.this, "Yeah Driver nic" + response.body().getNic(), Toast.LENGTH_SHORT).show();
                    Intent settingUpProfile = new Intent(DriverRegistration.this, SettingUpProfileActivity.class);
                    settingUpProfile.putExtra("USER ID", userId.getText().toString());
                    settingUpProfile.putExtra("USER EMAIL", userEmail.getText().toString());
                    startActivity(settingUpProfile);
                }
                else
                {
                    final EditText nic = (EditText) findViewById(R.id.driverprofileEditMobile);
                    nic.setError("User Already Exists");
                }
            }

            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                Toast.makeText(DriverRegistration.this,"Something Went Wrong"+t,Toast.LENGTH_SHORT).show();
                Intent logging= new Intent(DriverRegistration.this,Logging.class);
                startActivity(logging);
            }
        });
    }



}


