package com.example.michelle.driverbuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FineListAdapterOfficer extends ArrayAdapter<ViewFineTicket> {

    public static final String TAG = "FineListAdapter";
    private Context mContext;
    int mResource;

    public FineListAdapterOfficer(@NonNull Context context, int resource, @NonNull ArrayList<ViewFineTicket> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String offense=getItem(position).getOffense();
        String amount=getItem(position).getAmount();
        String driver=getItem(position).getOfficer();
        String timestamp=getItem(position).getTimestamp();

        ViewFineTicket viewFineTicket=new ViewFineTicket(offense,amount,driver,timestamp);
        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(mResource,parent,false);

        TextView tvDriver = (TextView)convertView.findViewById(R.id.textViewPoliceDriver);
        TextView tvAmount =(TextView)convertView.findViewById(R.id.textViewPoliceAmount);
        TextView tvOffense =(TextView)convertView.findViewById(R.id.textViewPoliceOffense);
        TextView tvTimestamp =(TextView)convertView.findViewById(R.id.textViewPoliceTimestamp);

        tvDriver.setText(driver);
        tvAmount.setText(amount);
        tvOffense.setText(offense);
        tvTimestamp.setText(timestamp);

        return convertView;

    }
}





