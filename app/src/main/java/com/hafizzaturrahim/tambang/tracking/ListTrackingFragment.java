package com.hafizzaturrahim.tambang.tracking;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.hafizzaturrahim.tambang.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListTrackingFragment extends Fragment {

    ListView lvTracking;
    ArrayList<Tracking> trackingArrayList = new ArrayList<>();

    public ListTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_tracking, container, false);
        lvTracking = (ListView) v.findViewById(R.id.list_Tracking);
        getTracking();

        lvTracking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                String id_tracking = trackingArrayList.get(position).getId_tracking();

                intent.putExtra("id_tracking",id_tracking);
                getActivity().startActivity(intent);
            }
        });
        return v;
    }

    //meminta data tracking ke database
    private void getTracking() {
        SessionManager sessionManager = new SessionManager(getActivity());
        String URL = Config.base_url + "/selectTracking.php?id_user=" +sessionManager.getIdLogin();
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
                        Log.v("result tracking", result);
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

    //memparsing hasil data tracking
    private void parseJSON(String result) {
        if (!result.contains("gagal")) {
//            Log.v("hasil a", "berhasil");
            try {
                JSONObject data = new JSONObject(result);
                JSONArray dataAr = data.getJSONArray("data");

                for (int i = 0; i < dataAr.length(); i++) {
                    JSONObject jsonObject = dataAr.getJSONObject(i);

                    String id_tracking = jsonObject.getString("id_tracking");
                    String nama = jsonObject.getString("nama");
                    String tanggal = jsonObject.getString("tanggal");

                    Tracking tracking = new Tracking();
                    tracking.setId_tracking(id_tracking);
                    tracking.setNama(nama);
                    tracking.setTanggal(tanggal);

                    trackingArrayList.add(tracking);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //mengeset adapter ke dalam list tracking
    private void setAdapter(){
        TrackingAdapter adapter = new TrackingAdapter(getActivity(),trackingArrayList);
        if(lvTracking.getAdapter() == null){ //Adapter not set yet.
            lvTracking.setAdapter(adapter);
        }
        else{ //Already has an adapter
            lvTracking.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lvTracking.invalidateViews();
            lvTracking.refreshDrawableState();
        }
    }

}
