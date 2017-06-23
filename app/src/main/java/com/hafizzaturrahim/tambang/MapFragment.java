package com.hafizzaturrahim.tambang;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import static android.R.id.list;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap googleMap;
    MapView mMapView;

    LocationManager lm;
    int counter = 0,hitung = 0;

    LatLng currentLocation;

    boolean isTracking = false;

    private FloatingActionButton fabCamera, fabStart, fabLocation;

    PolylineOptions polyOptions;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //initialize map
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);


        fabStart = (FloatingActionButton) v.findViewById(R.id.fabStart);
        fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
//                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                alert.setTitle("Memulai Tracking");
//                alert.setMessage("Apakah anda akan memulai tracking?");
//                alert.setNegativeButton("Tidak",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alert.setPositiveButton("Ya",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//
//                                dialog.dismiss();
//                            }
//                        });
//
//                alert.show();

            }
        });

        fabLocation = (FloatingActionButton) v.findViewById(R.id.fabGetLocation);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });

        fabCamera = (FloatingActionButton) v.findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GeotagActivity.class);
                startActivity(intent);
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

    private void initializeMap() {
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Anda di sini"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
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

    private void createPolyline() {
        Polyline line;
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.add(new LatLng(12.780712, 77.770956),
                new LatLng(12.912006, 77.229738),
                new LatLng(12.572030, 77.999756));
        line = googleMap.addPolyline(options);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mMapView.onResume();
        Toast.makeText(getActivity(), "Mohon tunggu", Toast.LENGTH_SHORT).show();

//        createPolygon();
//        createPolyline();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);


    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.v("latitude", String.valueOf(location.getLatitude()));
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        if (counter == 0) {
            currentLocation = position;
            counter++;
            SessionManager sessionManager = new SessionManager(getActivity());
            sessionManager.setLatitude((float) location.getLatitude());
            sessionManager.setLongitude((float) location.getLongitude());
            initializeMap();
            Toast.makeText(getActivity(), "Lokasi didapatkan", Toast.LENGTH_SHORT).show();
        }

        if (isTracking) {
            polyOptions.add(position);
            hitung++;
            Log.v("latitude ke " +hitung, String.valueOf(position.latitude+ " dan " +position.longitude));
        }

    }

    private void startTracking() {
        isTracking = true;
        polyOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

    }

    private void stopTracking() {
        Polyline polyline = googleMap.addPolyline(polyOptions);
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(0xff000000)
                .add(
                        new LatLng(-7.967548,110.9304117),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        Polyline polyline21 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(Color.GREEN)
                .add(
                        new LatLng(-34.747, 145.592),
                        new LatLng(-8.967548,110.9304117),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));

        isTracking = false;
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
}
