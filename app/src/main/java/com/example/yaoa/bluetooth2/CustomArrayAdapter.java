package com.example.yaoa.bluetooth2;

import android.content.Context;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by yaoa on 7/23/2015.
 */
public class CustomArrayAdapter extends ArrayAdapter<BTItem> {
    private final Context context;
    private final ArrayList<BTItem> items = new ArrayList<BTItem>();

    public CustomArrayAdapter(Context context){
        super(context, R.layout.bt_device_list_item);
        this.context = context;
    }


    public void add(BTItem item){
        boolean exists = false;
        for(BTItem tmp : items){
            if(item.getAddress().equals( tmp.getAddress() )){
                tmp.setSignal(item.getSignal());
                tmp.calculateAverage();
                if(tmp.getAverage() <= -58 && tmp.getAverage() >= -59){
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 100 milliseconds
                    v.vibrate(500);
                }
                exists = true;
            }
        }
        if(!exists) {
            items.add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public BTItem getItem(int position) {
        return items.get(position);
    }

    public boolean findItem(BTItem input){

        for (BTItem tmp : items){
            if(tmp.getAddress().equals(input.getAddress())){
                return true;
            }
        }
        return false;
    }

    public void editSignal(int position, BTItem input){
        items.get(position).setSignal(input.getSignal());
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listed = inflater.inflate(R.layout.bt_device_list_item, parent, false);
        TextView name = (TextView) listed.findViewById(R.id.devicename);
        name.setText(items.get(position).getName());
        TextView address = (TextView) listed.findViewById(R.id.deviceaddress);
        address.setText(items.get(position).getAddress());
        TextView signal = (TextView) listed.findViewById(R.id.devicestrength);
        signal.setText(String.valueOf(
                items.get(position).getSignal()
        )+"dBm");
        TextView average = (TextView) listed.findViewById(R.id.deviceaveragesignal);
        average.setText(String.valueOf(
                    items.get(position).getAverage()
            ) + "dBm");
        notifyDataSetChanged();
        return listed;
    }
}
