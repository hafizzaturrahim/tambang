package com.hafizzaturrahim.tambang.geotag;

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

public class GeotagAdapter extends ArrayAdapter<Geotag> {

    private Context context;
    private ArrayList<Geotag> geotags = new ArrayList<>();

    public GeotagAdapter(Context context, ArrayList<Geotag> geotags) {
        super(context, R.layout.item_tracking,geotags);
        this.context = context;
        this.geotags = geotags;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Load Custom Layout untuk list
        View v = inflater.inflate(R.layout.item_tracking, null, true);

        //Declarasi komponen
        TextView title = (TextView) v.findViewById(R.id.txt_TitleTracking);
        TextView date = (TextView) v.findViewById(R.id.txt_date);
        date.setVisibility(View.GONE);

        //Set ItemSpinner Value
        title.setText(geotags.get(position).getNama());

        return v;
    }
}
