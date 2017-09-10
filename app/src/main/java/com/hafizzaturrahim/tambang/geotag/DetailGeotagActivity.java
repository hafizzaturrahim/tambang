package com.hafizzaturrahim.tambang.geotag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hafizzaturrahim.tambang.Config;
import com.hafizzaturrahim.tambang.R;
import com.squareup.picasso.Picasso;

public class DetailGeotagActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MapView mMapView;
    Geotag geotag;
    ImageView imgGeotag;
    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_geotag);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Geotag");

        imgGeotag = (ImageView) findViewById(R.id.img_geotag);
        TextView txtTitle = (TextView) findViewById(R.id.txt_nama);
        TextView txtCoordinate = (TextView) findViewById(R.id.txt_latlng);

        geotag = getIntent().getParcelableExtra("geo");

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        location = bundle.getParcelable("position");

        geotag.setLat(location.latitude);
        geotag.setLng(location.longitude);

        txtTitle.setText(geotag.getNama());
        txtCoordinate.setText("("+geotag.getLat()+", "+geotag.getLng()+")");
        loadImage(geotag.getImage());

        mMapView = (MapView) findViewById(R.id.mapGeotag);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

    }

    private void loadImage(String picName){
        Log.v("urlPic",Config.base_url+"/"+picName);
        Picasso.with(this)
                .load(Config.base_url+"/"+picName)
                .placeholder(R.drawable.placeholder) // optional
                .into(imgGeotag);
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

        moveCamera();

    }

    private void moveCamera(){

        mMap.addMarker(new MarkerOptions().position(location).title(geotag.getNama()));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
}
