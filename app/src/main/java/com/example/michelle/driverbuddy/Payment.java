package com.example.michelle.driverbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Payment extends AppCompatActivity {

    private static final String TAG = "fine";
    private final static int PAYHERE_REQUEST = 11010; //a unique key
    //TextView message;
    public static String id_1,email,dname,offence;
    public static double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String fName = getIntent().getExtras().getString("fname");
        String lName = getIntent().getExtras().getString("lname");
        dname = getIntent().getExtras().getString("Name"); //full name
        email = getIntent().getExtras().getString("email");
        String mobile = String.valueOf(getIntent().getExtras().getInt("mobile"));
        amount = Double.parseDouble(String.valueOf(getIntent().getExtras().getInt("amount")));
        DecimalFormat df = new DecimalFormat("#.00");
        double amount_to_double=Double.valueOf(df.format(amount));
        offence = getIntent().getExtras().getString("offence");

         id_1 = getIntent().getExtras().getString("id");
        //Toast.makeText(Payment.this,String.valueOf(amount),Toast.LENGTH_LONG).show();


        InitRequest req = new InitRequest();
        req.setMerchantId("1211879"); //  Merchant ID
        req.setMerchantSecret("DBuddy"); // Merchant secret
        req.setAmount(amount_to_double); // Amount which the customer should pay
        req.setCurrency("LKR"); // Currency
        req.setOrderId("fine123"); // Unique ID for payment transaction
        req.setItemsDescription(offence);  // Item title or Order/Invoice number fine name/fine name
        req.setCustom1("Custom message 1");
        req.setCustom2("Custom message 2");
        req.getCustomer().setFirstName(fName);
        req.getCustomer().setLastName(lName);
        req.getCustomer().setEmail(email);
        req.getCustomer().setPhone(mobile);
        req.getCustomer().getAddress().setAddress("xxxxxxxxxx");
        req.getCustomer().getAddress().setCity("xxxxxxxxxx");
        req.getCustomer().getAddress().setCountry("Sri Lanka");
        req.getCustomer().getDeliveryAddress().setAddress("xxxxxxxxxx");
        req.getCustomer().getDeliveryAddress().setCity("xxxxxxxxxx");
        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
        req.getItems().add(new Item(null, "", 1));

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST);

        //onActivityResult( 11010, 12345, intent);

    }

    //fetch payment status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO process response
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            String msg;
            if (response.isSuccess()) {
                msg = "Activity result:" + response.getData().toString();
                Log.d(TAG, msg);
                sendNetworkRequestToUpdatePayment(id_1); //when payment is done update status to paid
                sendNetworkRequesttoSendMail(email,dname,offence,amount);

            } else {
                msg = "Result:" + response.toString();
                Log.d(TAG, msg);
            }
            //message.setText(msg);
        }
    }


    public void sendNetworkRequestToUpdatePayment(String id_1){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        Api payment_status = retrofit.create(Api.class);

        Call<FineTicket> call = payment_status.updatepaidstatus(id_1);
        call.enqueue(new Callback<FineTicket>() {
            @Override
            public void onResponse(Call<FineTicket> call, Response<FineTicket> response) {
                Toast.makeText(Payment.this,"Successfully Updated",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<FineTicket> call, Throwable t) {
                Toast.makeText(Payment.this,String.valueOf(t),Toast.LENGTH_LONG).show();

            }
        });

    }

    public void sendNetworkRequesttoSendMail(String email,String dname, String offence, Double amount){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());

        Log.d(TAG, "sendNetworkRequesttoSendMail: Network requestto send email");

        Retrofit retrofit = builder.build();

        Api send_email = retrofit.create(Api.class);
        Call<Email> call = send_email.sendmail(email,dname,offence,amount);
        call.enqueue(new Callback<Email>() {
            @Override
            public void onResponse(Call<Email> call, Response<Email> response) {
                if(response.body().getAccepted()!=null){
                    Toast.makeText(Payment.this,"Email send successfully",Toast.LENGTH_LONG).show();
                }
                else if(response.body().getAccepted() !=null){
                    Toast.makeText(Payment.this,"Error occurred while sending email",Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<Email> call, Throwable t) {
                Toast.makeText(Payment.this,"Something went wrong"+t, Toast.LENGTH_LONG).show();

            }
        });

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
