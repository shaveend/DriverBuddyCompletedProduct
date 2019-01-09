package com.example.michelle.driverbuddy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FineHistoryOfficer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_history_officer);
        //add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences("policeDetails",MODE_PRIVATE);
        String policeId=preferences.getString("PoliceId","N/A");
        //Toast.makeText(FineHistoryOfficer.this, policeId, Toast.LENGTH_LONG).show();
        sendNetworkRequest(policeId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendNetworkRequest(String policeId)
    {
        Toast.makeText(FineHistoryOfficer.this,"Loading....",Toast.LENGTH_SHORT).show();
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl("https://driverbuddy.herokuapp.com/")
                //.baseUrl("http://10.0.2.2:3000/")
                //.baseUrl("http://192.168.42.107:3000/")
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit=builder.build();

        Api getTickets=retrofit.create(Api.class);
        SharedPreferences preferences = getSharedPreferences("policeDetails",MODE_PRIVATE);
        String token=preferences.getString("Token","Null");
        Call<ArrayList<FineTicket>> call=getTickets.getFineTicketPolice(token,policeId);
        call.enqueue(new Callback<ArrayList<FineTicket>>() {
            @Override
            public void onResponse(Call<ArrayList<FineTicket>> call, Response<ArrayList<FineTicket>> response) {
                createAdapterView(response.body());
                //Toast.makeText(FineHistoryOfficer.this, String.valueOf(response.body().size()), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<ArrayList<FineTicket>> call, Throwable t) {
                Toast.makeText(FineHistoryOfficer.this, "Somthing went wrong"+t, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createAdapterView(ArrayList<FineTicket> ticketList)
    {
        ArrayList<ViewFineTicket>fineTickets=new ArrayList<ViewFineTicket>();
        ListView fineListView=(ListView) findViewById(R.id.listViewOfficerFIne);

        for(int i=0;i<ticketList.size();i++)
        {
            String offense=ticketList.get(i).getFine()[0].getName();
            String amount=String.valueOf(ticketList.get(i).getAmount());
            String driver=ticketList.get(i).getDriver()[0].getFirstName()+" "+ticketList.get(i).getDriver()[0].getLastName();
            String timestamp=ticketList.get(i).getTimeStamp().toString();

            fineTickets.add(new ViewFineTicket(offense,amount,driver,timestamp));
        }

        //Toast.makeText(FineHistoryOfficer.this, String.valueOf(fineTickets.size()), Toast.LENGTH_LONG).show();
        FineListAdapterOfficer adapter= new FineListAdapterOfficer(this,R.layout.adapter_view_policefine_layout,fineTickets);
        fineListView.setAdapter(adapter);

    }
}
