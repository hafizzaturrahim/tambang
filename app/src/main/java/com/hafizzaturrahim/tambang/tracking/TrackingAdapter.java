package com.hafizzaturrahim.tambang.tracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hafizzaturrahim.tambang.R;

import java.util.ArrayList;

/**
 * Created by Hafizh on 31/08/2017.
 */

public class TrackingAdapter extends ArrayAdapter<Tracking> {

    private Context context;
    private ArrayList<Tracking> trackings = new ArrayList<>();

    public TrackingAdapter(Context context, ArrayList<Tracking> trackings) {
        super(context, R.layout.item_tracking,trackings);
        this.context = context;
        this.trackings = trackings;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Load Custom Layout untuk list
        View v = inflater.inflate(R.layout.item_tracking, null, true);

        //Declarasi komponen
        TextView title = (TextView) v.findViewById(R.id.txt_TitleTracking);

        //Set ItemSpinner Value
        title.setText(trackings.get(position).getNama());

        return v;
    }
}
