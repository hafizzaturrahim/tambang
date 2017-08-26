package com.hafizzaturrahim.tambang;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class KmlFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private GoogleMap googleMap;
    MapView mMapView;

    LocationManager lm;
    int counter = 0;

    LatLng currentLocation;

    ProgressDialog loading;

    public KmlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kml, container, false);

        //initialize map
        mMapView = (MapView) v.findViewById(R.id.mapViewKml);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        loading = new ProgressDialog(getActivity());
        loading.setMessage("Mencari lokasi...");
        loading.show();

        //check if latlng session is empty
        SessionManager sessionManager = new SessionManager(getActivity());
        Log.v("kmlfragment", "lat " +String.valueOf(sessionManager.getLatitude()));
//        if (sessionManager.getLatitude() != 0){
//            currentLocation = new LatLng(sessionManager.getLatitude(), sessionManager.getLongitude());
//            counter++;
//            initializeMap();
//        }

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
        loadKml();
        loading.dismiss();
    }

    private void loadKml(){
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mMapView.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.v("latitude", String.valueOf(location.getLatitude()));
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //mengetahui lokasi saat ini
        if (counter == 0) {
            counter++;

            initializeMap();
            Toast.makeText(getActivity(), "Lokasi didapatkan", Toast.LENGTH_SHORT).show();
        }
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
