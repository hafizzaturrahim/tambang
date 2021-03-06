package com.hafizzaturrahim.tambang.beranda;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.kml.KmlLayer;
import com.hafizzaturrahim.tambang.Config;
import com.hafizzaturrahim.tambang.geotag.DetailGeotagActivity;
import com.hafizzaturrahim.tambang.geotag.Geotag;
import com.hafizzaturrahim.tambang.geotag.GeotagActivity;
import com.hafizzaturrahim.tambang.R;
import com.hafizzaturrahim.tambang.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap googleMap;
    MapView mMapView;

    LocationManager lm;
    int counter = 0, hitung = 0;

    LatLng currentLocation;

    private static final String TAG = "MapFragment";
    private Button btnStart, btnStop;

    ProgressDialog loading;
    ArrayList<LatLng> point = new ArrayList<>();

    ArrayList<LatLng> trackPoints = new ArrayList<>();
    Polyline trackLine;
    Marker trackMarker;
    PolylineOptions polyOptions;
    boolean isTracking = false;

    ArrayList<Geotag> geotagPoint = new ArrayList<>();
    String[] geoMarkerPosition;
    SessionManager sessionManager;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getActivity());
        //inisiasi ;ayout
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        loading = new ProgressDialog(getActivity());
        loading.setMessage("Mencari lokasi...");
        loading.show();

        btnStart = (Button) v.findViewById(R.id.btn_startTrack);
        btnStop = (Button) v.findViewById(R.id.btn_stopTrack);


//        btnStart.hide();
//        btnStop.hide();
        showFab(false);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Memulai Tracking");
                alert.setMessage("Apakah anda akan memulai tracking?");

                alert.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showFab(false);

                                startTracking();
                                dialog.dismiss();
                            }
                        });
                alert.setNegativeButton("Tidak",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();
//                                animate();

                            }
                        });

                alert.show();


            }
        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Menghentikan Tracking");
                alert.setMessage("Apakah anda akan menghentikan tracking?");
                alert.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                stopTracking();
                                Toast.makeText(getActivity(), "Tracking berhenti", Toast.LENGTH_SHORT).show();
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

        //initialize location manager
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //check permission for location manager
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getActivity(), "Not Enough Permission", Toast.LENGTH_SHORT).show();
            //give permission
            if (Build.VERSION.SDK_INT >= 23) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            return v;
        } else {
//            Toast.makeText(getActivity(), "Got Permission", Toast.LENGTH_SHORT).show();
        }
        //set location manager using gps or network
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);



        return v;
    }

    private void animate() {
        FastOutSlowInInterpolator fastOutSlowInInterpolator = new FastOutSlowInInterpolator();
        btnStart.clearAnimation();
        if (btnStart.getVisibility() == View.GONE) {
            btnStart.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.GONE);
        }
        Animation anim = android.view.animation.AnimationUtils.loadAnimation(
                btnStart.getContext(), R.anim.design_fab_in);
        anim.setDuration(1500);
        anim.setInterpolator(fastOutSlowInInterpolator);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnStart.startAnimation(anim);
    }

    //inisiasi map
    private void initializeMap() {
        loadKml();
        getPolyLine();
        getGeotag();
//        createPolyLine();
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Titik awal anda"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
//        btnStart.show();

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for(int i=0;i < geoMarkerPosition.length;i++){
                    if (marker.getId().equals(geoMarkerPosition[i])){
//                        String tes = geotagPoint.get(i).getNama();
//                        Toast.makeText(getActivity(), tes, Toast.LENGTH_SHORT).show();
                        newActivity(i);
                        break;
                    }
                }

            }
        });
        showFab(true);
    }

    //membuka aktivity baru untuk geotag
    private void newActivity(int position){
        Intent intent = new Intent(getActivity(), DetailGeotagActivity.class);
        Geotag geotag = geotagPoint.get(position);
        Log.v("geo lat lg", String.valueOf(geotag.getLat()));
        Log.v("geo lng lg", String.valueOf(geotag.getLng()));
        LatLng pos = new LatLng(geotag.getLat(),geotag.getLng());
        Bundle args = new Bundle();
        args.putParcelable("position", pos);

        intent.putExtra("geo",geotag);
        intent.putExtra("bundle",args);
        getActivity().startActivity(intent);
    }

    //memunculkan/menyembunyikan tombol tracking
    private void showFab(boolean isShow) {
        if (isShow) {
            btnStart.setVisibility(View.VISIBLE);
        } else {
            btnStart.setVisibility(View.GONE);
        }

        if (btnStart.isShown()) {
            btnStop.setVisibility(View.GONE);
        } else {
            btnStop.setVisibility(View.VISIBLE);
        }
    }

    //meload kml
    private void loadKml() {
        KmlLayer layerArea;
        KmlLayer layerLine;
        try {
            layerArea = new KmlLayer(googleMap, R.raw.area, getActivity());
            layerLine = new KmlLayer(googleMap, R.raw.line, getActivity());
            layerArea.addLayerToMap();
            layerLine.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createPolygon() {
        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                .add(new LatLng(12.780712, 77.770956),
                        new LatLng(12.912006, 77.229738),
                        new LatLng(12.572030, 77.999756))
                .strokeColor(0xFF3F51B5)
                .fillColor(0xFFFF4081)
                .strokeWidth(5)
        );


        polygon.setClickable(true);

        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            public void onPolygonClick(Polygon polygon) {
                Toast.makeText(getActivity(), "Click polygon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        mMapView.onResume();
    }

    //method yang diturunkan ketika posisi berubah
    @Override
    public void onLocationChanged(Location location) {
//        Log.v("latitude", String.valueOf(location.getLatitude()));
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation = position;
        //mengetahui lokasi saat ini menggunakan counter
        if (counter == 0) {
            counter++;
            sessionManager.setLatitude((float) location.getLatitude());
            sessionManager.setLongitude((float) location.getLongitude());
            initializeMap();
            Toast.makeText(getActivity(), "Lokasi didapatkan", Toast.LENGTH_SHORT).show();
        }

        //mengetahui apakah tracking aktif
        if (isTracking) {
            polyOptions.add(position);
            trackPoints.add(position);
            hitung++;
            Log.v("latitude ke " + hitung, String.valueOf(position.latitude + " dan " + position.longitude));
//            Toast.makeText(getActivity(), "Lokasi " +hitung+" ,lat " +position.latitude, Toast.LENGTH_SHORT).show();
            trackLine.setPoints(trackPoints);

            trackMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            trackMarker.setPosition(position);
//            redrawLine();

        }

    }

    //meminta data polyline untuk tracking dari database untuk ditampilkan
    private void getPolyLine() {
        String URL = Config.base_url + "/selectPolyLine.php?id_user=" + sessionManager.getIdLogin();
        //Showing the progress dialog

        loading.setMessage("Mengambil data...");
        Log.v("URL ", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //Disimissing the progress dialog
                        Log.v("result polyline", result);
                        parseJSONTracking(result);
                        loading.dismiss();
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
                        Toast.makeText(getActivity(), "Terjadi kesalahan dalam mengambil data", Toast.LENGTH_LONG).show();
                    }
                });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    //memparsing hasil polyline untuk tracking
    private void parseJSONTracking(String result) {
        if (!result.contains("gagal")) {
//            Log.v("hasil a", "berhasil");
            try {
                JSONObject data = new JSONObject(result);
                JSONArray dataAr = data.getJSONArray("data");

                int jumlahTracking = 0;
                String id = null;
                for (int i = 0; i < dataAr.length(); i++) {
                    JSONObject polyObj = dataAr.getJSONObject(i);

                    //read from json result
                    float lat = Float.parseFloat(polyObj.getString("lat"));
                    float lng = Float.parseFloat(polyObj.getString("lng"));

//                    Log.v("hasil b", String.valueOf(lat));
                    if (id == null) {
//                        Log.v("hasil c", "kosong");
                        jumlahTracking++;
                        id = polyObj.getString("id_tracking");
                        //initiate the result for the first time
                    } else {
//                        Log.v("hasil ", "isi");
                        String thisId = polyObj.getString("id_tracking");
                        if (id.equals(thisId)) {
//                            Log.v("hasil d", "sama");

                        } else {
                            createPolyLine();
//                            Log.v("hasil d", "tidak sama");
                            id = thisId;
                            jumlahTracking++;
                        }
                    }
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
        Integer[] warna = new Integer[]{
                Color.GREEN,
                Color.BLUE,
                Color.RED,
                Color.YELLOW
        };
        Random random = new Random();

        PolylineOptions polylineOptions = new PolylineOptions()
                .clickable(true)
                .geodesic(true)
                .color(warna[random.nextInt(4)]);

        for (int i = 0; i < point.size(); i++) {
            polylineOptions.add(point.get(i));
        }
        Polyline polyline1 = googleMap.addPolyline(polylineOptions);

        //clearing point to add new tracking
        point.clear();
    }

    //mengambil data geotag dari database
    private void getGeotag() {
        String URL = Config.base_url + "/selectGeotag.php?id_user=" + sessionManager.getIdLogin();
        //Showing the progress dialog

        Log.v("URL geotag", URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //Disimissing the progress dialog
                        Log.v("result geotag", result);
                        parseJSONGeotag(result);
                        loading.dismiss();
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
                        Toast.makeText(getActivity(), "Terjadi kesalahan dalam mengambil data", Toast.LENGTH_LONG).show();
                    }
                });

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    //memparsing hasil geotag dari database
    private void parseJSONGeotag(String result) {
        if (!result.contains("gagal")) {
//            Log.v("hasil a", "berhasil");
            String title = null;
            try {
                JSONObject data = new JSONObject(result);
                JSONArray dataAr = data.getJSONArray("data");

                geoMarkerPosition = new String[dataAr.length()];
                for (int i = 0; i < dataAr.length(); i++) {
                    JSONObject geoObj = dataAr.getJSONObject(i);

                    //read from json result
                    Double lat = geoObj.getDouble("lat");
                    Double lng = geoObj.getDouble("lng");
                    title = geoObj.getString("nama");
                    String id_marker = geoObj.getString("id_marker");
                    String image = geoObj.getString("url");

                    Geotag geotag = new Geotag();
                    geotag.setId_marker(id_marker);
                    geotag.setLat(lat);
                    geotag.setLng(lng);
                    geotag.setNama(title);
                    geotag.setImage(image);

                    LatLng position = new LatLng(lat, lng);
                    geotagPoint.add(geotag);
                    addMarkerGeotag(title, position,i);
                }
//                Log.v("jumlah track", String.valueOf(jumlahTracking));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //menambah marker ke map untuk geotag
    private void addMarkerGeotag(String title, LatLng position, final int i) {
        int height = 85;
        int width = 55;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap icon = Bitmap.createScaledBitmap(b, width, height, false);

        MarkerOptions options = new MarkerOptions().position(position)
                .title(title)
                .snippet("geotag")
                .icon(BitmapDescriptorFactory.fromBitmap(icon));


        Marker geoMarker = googleMap.addMarker(options);
        geoMarkerPosition[i] = geoMarker.getId();

        Log.d(TAG, "Marker geotag added.............................");
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));
    }

    //memulai tracking
    private void startTracking() {
        Toast.makeText(getActivity(), "Memulai tracking", Toast.LENGTH_SHORT).show();
        isTracking = true;

        addMarker("Lokasi anda");
        polyOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        polyOptions.add(currentLocation);
        trackLine = googleMap.addPolyline(polyOptions); //add Polyline

    }

    //menambah marker baru untuk tracking
    private void addMarker(String title) {
        MarkerOptions options = new MarkerOptions();

        options.position(currentLocation);
        trackMarker = googleMap.addMarker(options);
//        long atTime = mCurrentLocation.getTime();
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
//        String title = mLastUpdateTime.concat(", " + requiredArea).concat(", " + city).concat(", " + country);
        trackMarker.setTitle(title);

        Log.d(TAG, "Marker added.............................");
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));
    }

    //menghentikan tracking
    private void stopTracking() {
        Polyline polyline = googleMap.addPolyline(polyOptions);

//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .color(0xff000000)
//                .add(
//                        new LatLng(-7.967548, 110.9304117),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309)));

        isTracking = false;
        giveTitleTrackingDialog();
        showFab(true);

    }

    //memunculkan dialog untuk memberi nama tracking
    private void giveTitleTrackingDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("Tracking Baru")
                .content("Berikan nama untuk tracking yang baru dibuat")
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText("Submit")
                .negativeText("Cancel")
                .input(
                        null,
                        null,
                        false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String titleTracking = input.toString();
                                sendPolyLine(titleTracking);
                            }
                        })
                .show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //mengubah hasil tracking menjadi json
    private String createJSONArray() {
        String jsonResult = new Gson().toJson(trackPoints);

        Log.v(TAG, "createJSONArray: " + jsonResult);
        return jsonResult;
    }

    //mengirim hasil tracking berupa json ke database
    private void sendPolyLine(final String titleTracking) {
        String URL = Config.base_url + "/sendPolyLine.php";
        //Showing the progress dialog
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mengirim data...");
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        //Disimissing the progress dialog
                        Log.v("result", result);
                        loading.dismiss();
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user", "1");
                params.put("polyline", createJSONArray());
                params.put("nama", titleTracking);
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
