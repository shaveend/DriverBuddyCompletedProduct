package com.example.michelle.driverbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingUpProfileActivity extends AppCompatActivity {

    private static final String TAG = "SettingUpProfileActivity" ;
    public Button btn_done;
    TextView userId;
    EditText password;
    String type="Driver";
    EditText confirmpassword;
    private FirebaseAuth mAuth;
    String email;



    public void SignOut()
    {
        btn_done=(Button) findViewById(R.id.settingUpDone_btn);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfile();
            }
        });



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up_profile);
        SignOut();
        userId=(TextView) findViewById(R.id.settingUpProfileUserId);
        userId.setText(getIntent().getStringExtra("USER ID"));
        email=getIntent().getStringExtra("USER EMAIL");
        mAuth = FirebaseAuth.getInstance();





    }
    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }*/ //check whether user already logged in in firebase

    public void createProfile()
    {
        password=(EditText) findViewById(R.id.settingUpProfilePassword);
        confirmpassword=(EditText) findViewById(R.id.settingUpProfileConfirmPassword);
        String passW=password.getText().toString();
        String confirmP=confirmpassword.getText().toString();


        if(!passW.equals(confirmP))
        {
            password.setError("Password Doesn't Match");
            confirmpassword.setError(("Password Doesn't Match "));
        }
        else
        {
            password = (EditText) findViewById(R.id.settingUpProfilePassword);


            User user = new User(

                    userId.getText().toString(),
                    password.getText().toString(),
                    type

            );

            sendNetworkRequest(user);

        }

    }

    public void sendNetworkRequest(User user)
    {

        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();

        Api userRegistration=retrofit.create(Api.class);
        Call<User> call=userRegistration.createProfile(user);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(SettingUpProfileActivity.this,"Yeah User nic"+response.body().getUserId(),Toast.LENGTH_SHORT).show();
                create_firebase_account(email,password.getText().toString());
                Intent done= new Intent(SettingUpProfileActivity.this, Logging.class);
                startActivity(done);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SettingUpProfileActivity.this,"Something Went Wrong"+t,Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void create_firebase_account(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        }

                        // ...
                    }
                });
    }


}
