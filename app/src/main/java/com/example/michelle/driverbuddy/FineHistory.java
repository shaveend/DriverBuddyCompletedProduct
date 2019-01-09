package com.example.michelle.driverbuddy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FineHistory extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_history);
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences("driverDetails",MODE_PRIVATE);
        String nic=preferences.getString("Nic","N/A");
        sendNetworkRequest(nic);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendNetworkRequest(String nic)
    {
        Toast.makeText(FineHistory.this,"Loading....",Toast.LENGTH_SHORT).show();
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();

        Api getTickets=retrofit.create(Api.class);
        SharedPreferences preferences = getSharedPreferences("driverDetails",MODE_PRIVATE);
        String token=preferences.getString("Token","Null");
        Call<ArrayList<FineTicket>> call=getTickets.getFineTicketDriver(token,nic);
        call.enqueue(new Callback<ArrayList<FineTicket>>() {

            @Override
            public void onResponse(Call<ArrayList<FineTicket>> call, Response<ArrayList<FineTicket>> response) {
                createAdapterView(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<FineTicket>> call, Throwable t) {
                Toast.makeText(FineHistory.this, "Somthing went wrong"+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createAdapterView(ArrayList<FineTicket> ticketList)
    {
        ArrayList<ViewFineTicket>fineTickets=new ArrayList<ViewFineTicket>();
        ListView fineListView=(ListView) findViewById(R.id.fineListView);

        for(int i=0;i<ticketList.size();i++)
        {
            String offense=ticketList.get(i).getFine()[0].getName();
            String amount=String.valueOf(ticketList.get(i).getAmount());
            String officer=ticketList.get(i).getPolice()[0].getFirstName().charAt(0)+" "+ticketList.get(i).getPolice()[0].getLastName();
            String timestamp=ticketList.get(i).getTimeStamp().toString();
            boolean paid=ticketList.get(i).isPaid();

            fineTickets.add(new ViewFineTicket(offense,amount,officer,timestamp,paid));
        }

        //fineTickets.add(new ViewFineTicket("Crossing Double Line",1000,"Saman Kulathunga","2018/78/78"));
        //Toast.makeText(FineHistory.this,String.valueOf(fineTickets.size()), Toast.LENGTH_LONG).show();
        FineListAdapter adapter= new FineListAdapter(this,R.layout.adapter_view_layout,fineTickets);
        fineListView.setAdapter(adapter);
    }
}
