package com.example.michelle.driverbuddy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Logging extends AppCompatActivity {

    private static final String TAG = "Logging";
    //private static final int MY_PERMISSIONS_REQUEST_ACCESS_COURSE_LOCATION = 2;
    public Button btn_logIn, btn_signin;
    private FirebaseAuth mAuth;
    private EditText passwordf;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mlocationRequest;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;



    public void driverMenuLogIn() {
        btn_logIn = (Button) findViewById(R.id.btn_logIn);

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(Logging.this,"THis works",Toast.LENGTH_SHORT).show();
                final EditText username = (EditText) findViewById(R.id.editText);
                final EditText password = (EditText) findViewById(R.id.editText2);
                passwordf = (EditText) findViewById(R.id.editText2);
                String userNameText = username.getText().toString().toLowerCase();
                if (userNameText.length() != 10 || !userNameText.contains("v")) {
                    username.setError("Invalid User ID");
                } else {
                    User user = new User(

                            username.getText().toString(),
                            password.getText().toString()
                    );

                    sendNetworkRequestForType(user);
                /*if(username.getText().toString().trim().equals("insurance") && password.getText().toString().equals("abc")) {
                    Intent nextActivity = new Intent(Logging.this, insurance_profile.class);
                    startActivity(nextActivity);
                }
                else if (username.getText().toString().trim().equals("police") && password.getText().toString().equals("abc")){
                    Intent nextActivity= new Intent(Logging.this,PoliceOfficerMenu.class);
                    startActivity(nextActivity);
                }
                else if (username.getText().toString().trim().equals("driver") && password.getText().toString().equals("abc"))
                {
                    Intent nextActivity1 = new Intent(Logging.this, DriverMenu.class);
                    startActivity(nextActivity1);
                }*/

                }

            }
        });
    }


    public void signup() {
        btn_signin = (Button) findViewById(R.id.btn_signUp);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextActivity1 = new Intent(Logging.this, DriverRegistration.class);
                startActivity(nextActivity1);


            }
        });

    }

    public void sendNetworkRequestForType(final User user) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        Api userLogin = retrofit.create(Api.class);
        Call<User> call = userLogin.getProfileType(user);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String type = response.body().getType();
                //Toast.makeText(Logging.this,type,Toast.LENGTH_SHORT).show();
                sendNetworkRequestForAccount(type, user);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //Toast.makeText(Logging.this,"Something Went Wrong"+t,Toast.LENGTH_SHORT).show();
                final EditText username = (EditText) findViewById(R.id.editText);
                username.setError("User Not Found"+t);
            }
        });
    }

    public void sendNetworkRequestForAccount(String type, final User user) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        Api userLogin = retrofit.create(Api.class);

        if (type.equals("Driver")) {
            //Toast.makeText(Logging.this,"hello",Toast.LENGTH_SHORT).show();
            Call<Driver> call = userLogin.loginDriver(user);


            call.enqueue(new Callback<Driver>() {
                @Override
                public void onResponse(Call<Driver> call, Response<Driver> response) {

                    Intent driverMenu = new Intent(Logging.this, DriverMenu.class);
                    String name = response.body().getFirstName() + " " + response.body().getLastName();
                    String license = String.valueOf(response.body().getLicense());
                    String email = response.body().getEmail();
                    int mobile = response.body().getMobile();
                    String nic = response.body().getNic();
                    String token = response.body().getToken();

                    SharedPreferences driverPreferences = getSharedPreferences("driverDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = driverPreferences.edit();
                    editor.putString("Name", name);
                    editor.putString("License", license);
                    editor.putString("Email", email);
                    editor.putInt("Mobile", mobile);
                    editor.putString("Nic", nic);
                    editor.putString("Token", token);
                    editor.putBoolean("Once",true);
                    editor.commit();
                    firebase_log_in(email, passwordf.getText().toString());
                    startActivity(driverMenu);
                    finish();
                }

                @Override
                public void onFailure(Call<Driver> call, Throwable t) {
                    //Toast.makeText(Logging.this,"Something Went Wrong"+t,Toast.LENGTH_SHORT).show();
                    final EditText username = (EditText) findViewById(R.id.editText);
                    username.setError("User Not Found");
                }
            });
        } else if (type.equals("Police")) {
            Call<Police> call = userLogin.loginPolice(user);


            call.enqueue(new Callback<Police>() {
                @Override
                public void onResponse(Call<Police> call, Response<Police> response) {

                    Intent policeMenu = new Intent(Logging.this, PoliceOfficerMenu.class);


                    String name = response.body().getFirstName() + " " + response.body().getLastName();
                    String policeId = String.valueOf(response.body().getPoliceId());
                    String email = response.body().getEmail();
                    int mobile = response.body().getMobile();
                    String nic = response.body().getNic();
                    String token = response.body().getToken();

                    SharedPreferences policePreferences = getSharedPreferences("policeDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = policePreferences.edit();
                    editor.putString("Name", name);
                    editor.putString("PoliceId", policeId);
                    editor.putString("Email", email);
                    editor.putInt("Mobile", mobile);
                    editor.putString("Nic", nic);
                    editor.putString("Token", token);
                    editor.putBoolean("Once",true);
                    editor.commit();
                    firebase_log_in(email, passwordf.getText().toString());
                    startActivity(policeMenu);
                    finish();
                }

                @Override
                public void onFailure(Call<Police> call, Throwable t) {
                    final EditText username = (EditText) findViewById(R.id.editText);
                    username.setError("User Not Found");
                }
            });
        } else if (type.equals("Insurance")) {
            Call<Insurance> call = userLogin.loginInsurance(user);


            call.enqueue(new Callback<Insurance>() {
                @Override
                public void onResponse(Call<Insurance> call, Response<Insurance> response) {

                    Intent insuranceMenu = new Intent(Logging.this, insurance_profile.class);

                    String name = response.body().getFirstName() + " " + response.body().getLastName();
                    String policeId = String.valueOf(response.body().getAgentId());
                    String email = response.body().getEmail();
                    int mobile = response.body().getMobile();
                    String nic = response.body().getNic();
                    String token = response.body().getToken();

                    SharedPreferences insurancePreferences = getSharedPreferences("insuranceDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = insurancePreferences.edit();
                    editor.putString("Name", name);
                    editor.putString("AgentId", policeId);
                    editor.putString("Email", email);
                    editor.putInt("Mobile", mobile);
                    editor.putString("Nic", nic);
                    editor.putString("Token", token);
                    editor.putBoolean("Once",true);
                    editor.commit();
                    firebase_log_in(email, passwordf.getText().toString());
                    startActivity(insuranceMenu);
                    finish();
                }

                @Override
                public void onFailure(Call<Insurance> call, Throwable t) {
                    final EditText username = (EditText) findViewById(R.id.editText);
                    username.setError("User Not Found");

                }
            });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);

        mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(10000);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        fetchlocation();


        mAuth = FirebaseAuth.getInstance();
        driverMenuLogIn();
        signup();


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
                            ActivityCompat.requestPermissions(Logging.this,
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
                }
            },getMainLooper());
        }
    }

    public void firebase_log_in(String emailf,String passwordf){
        mAuth.signInWithEmailAndPassword(emailf, passwordf)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);

                        }

                        // ...
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
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
