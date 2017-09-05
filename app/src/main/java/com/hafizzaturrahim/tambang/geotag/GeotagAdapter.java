package com.hafizzaturrahim.tambang.geotag;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hafizzaturrahim.tambang.Config;
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
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Load Custom Layout untuk list
        View v = inflater.inflate(R.layout.item_tracking, null, true);

        //Declarasi komponen
        TextView title = (TextView) v.findViewById(R.id.txt_TitleTracking);
        TextView date = (TextView) v.findViewById(R.id.txt_date);
        date.setVisibility(View.GONE);

        Button deleteBtn = (Button) v.findViewById(R.id.btn_delete);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Menghapus Tracking");
                alert.setMessage("Apakah anda akan menghapus tracking (tracking yang dihapus tidak dapat dibatalkan)?");
                alert.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                deleteGeotag(geotags.get(position).getId_marker());
                                notifyDataSetChanged();
                                removeFromList(position);
                                dialog.dismiss();
                            }
                        });

                alert.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();
                            }
                        });

                alert.show();


            }
        });

        //Set ItemSpinner Value
        title.setText(geotags.get(position).getNama());

        return v;
    }

    private void deleteGeotag(String id){
        String URL = Config.base_url + "/deleteGeotag.php?id=" +id;
        //Showing the progress dialog
        final ProgressDialog loading = new ProgressDialog(context);
        loading.setMessage("Menghapus data...");
        loading.show();
        Log.v("URL ", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //Disimissing the progress dialog
                        Log.v("result tracking", result);
                        loading.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(context, "Terjadi kesalahan dalam mengambil data", Toast.LENGTH_LONG).show();
                    }
                });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void removeFromList(int position){
        geotags.remove(geotags.get(position));
    }
}
