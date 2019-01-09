package com.example.michelle.driverbuddy;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FineListAdapter extends ArrayAdapter<ViewFineTicket>{

    public static final String TAG ="FineListAdapter";
    private Context mContext;
    int mResource;

    public FineListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ViewFineTicket> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String offense=getItem(position).getOffense();
        String amount=getItem(position).getAmount();
        String officer=getItem(position).getOfficer();
        String timestamp=getItem(position).getTimestamp();
        boolean paid=getItem(position).isPaid();

        ViewFineTicket viewFineTicket=new ViewFineTicket(offense,amount,officer,timestamp,paid);

        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(mResource,parent,false);

        TextView tvOfficer = (TextView)convertView.findViewById(R.id.textViewOfficer);
        TextView tvAmount =(TextView)convertView.findViewById(R.id.textViewAmount);
        TextView tvOffense =(TextView)convertView.findViewById(R.id.textViewOffense);
        TextView tvTimestamp =(TextView)convertView.findViewById(R.id.textViewTimestamp);

        tvOfficer.setText(officer);
        tvAmount.setText(String.valueOf(amount));
        tvOffense.setText(offense);
        tvTimestamp.setText(timestamp);

        if(paid==true)
        {
            tvOffense.setBackgroundColor(Color.parseColor("#8de018"));
        }
        else if(paid==false)
        {
            tvOffense.setBackgroundColor(Color.parseColor("#b20101"));
        }

        return convertView;
    }


}
