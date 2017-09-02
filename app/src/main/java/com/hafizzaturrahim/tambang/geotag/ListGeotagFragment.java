package com.hafizzaturrahim.tambang.geotag;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hafizzaturrahim.tambang.Config;
import com.hafizzaturrahim.tambang.R;
import com.hafizzaturrahim.tambang.tracking.Tracking;
import com.hafizzaturrahim.tambang.tracking.TrackingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListGeotagFragment extends Fragment {


    ListView listGeotag;
    ArrayList<Geotag> geotagArrayList = new ArrayList<>();

    public ListGeotagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_geotag, container, false);
        listGeotag = (ListView) v.findViewById(R.id.list_Geotag);
        getGeotag();
        return v;
    }

    private void getGeotag() {
        String URL = Config.base_url + "/selectGeotag.php";
        //Showing the progress dialog
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mengambil data...");
        loading.show();
        Log.v("URL ", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //Disimissing the progress dialog
                        Log.v("result geotag", result);
                        parseJSON(result);
                        setAdapter();
                        loading.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Terjadi kesalahan dalam mengambil data", Toast.LENGTH_LONG).show();
                    }
                });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String result) {
        if (!result.contains("gagal")) {
//            Log.v("hasil a", "berhasil");
            try {
                JSONObject data = new JSONObject(result);
                JSONArray dataAr = data.getJSONArray("data");

                for (int i = 0; i < dataAr.length(); i++) {
                    JSONObject jsonObject = dataAr.getJSONObject(i);

                    String nama = jsonObject.getString("nama");
                    Geotag geotag = new Geotag();
                    geotag.setNama(nama);

                    geotagArrayList.add(geotag);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setAdapter(){
        GeotagAdapter adapter = new GeotagAdapter(getActivity(),geotagArrayList);
        listGeotag.setAdapter(adapter);
    }

}
