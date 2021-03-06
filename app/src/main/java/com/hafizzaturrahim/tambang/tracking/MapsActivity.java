package com.hafizzaturrahim.tambang.tracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hafizzaturrahim.tambang.Config;
import com.hafizzaturrahim.tambang.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mMapView;
    ArrayList<LatLng> point = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Tracking");

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapView.onResume();

        Intent intent = getIntent();
        String id_tracking = intent.getStringExtra("id_tracking");
        getPolyLine(id_tracking);

    }

    //meminta data polyline ke database
    private void getPolyLine(String id_tracking) {
        String URL = Config.base_url + "/selectPolyLine.php?id_tracking=" + id_tracking;
        //Showing the progress dialog
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mengambil data...");
        loading.show();

        Log.v("URL ", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //Disimissing the progress dialog
                        Log.v("result polyline", result);
                        parseJSON(result);
                        loading.dismiss();
                        moveCamera();
                        //Showing toast message of the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
//                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MapsActivity.this, "Terjadi kesalahan dalam mengambil data", Toast.LENGTH_LONG).show();
                    }
                });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    //memparsing hasil tracking
    private void parseJSON(String result) {
        if (!result.contains("gagal")) {
//            Log.v("hasil a", "berhasil");
            try {
                JSONObject data = new JSONObject(result);
                JSONArray dataAr = data.getJSONArray("data");

                for (int i = 0; i < dataAr.length(); i++) {
                    JSONObject polyObj = dataAr.getJSONObject(i);

                    //read from json result
                    float lat = Float.parseFloat(polyObj.getString("lat"));
                    float lng = Float.parseFloat(polyObj.getString("lng"));

                    //add point if point still in the same track
                    point.add(new LatLng(lat, lng));
                }
//                Log.v("jumlah track", String.valueOf(jumlahTracking));
                createPolyLine();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //membuat polyline untuk tiap tracking
    private void createPolyLine() {
        PolylineOptions polylineOptions = new PolylineOptions()
                .clickable(true)
                .geodesic(true)
                .color(Color.BLUE);

        for (int i = 0; i < point.size(); i++) {
            polylineOptions.add(point.get(i));
        }
        Polyline polyline = mMap.addPolyline(polylineOptions);
    }


    private void moveCamera() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(point.get(0)).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

}
