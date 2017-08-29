package com.hafizzaturrahim.tambang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.hafizzaturrahim.tambang.beranda.MapFragment;
import com.hafizzaturrahim.tambang.kml.KmlFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog pDialog;
    long lastPress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoggedIn();
        setContentView(R.layout.activity_main);
        pDialog = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

//        TextView nav_nama = (TextView) hView.findViewById(R.id.nav_nama);
//
//        nav_nama.setText("Halo, " + sessionManager.getUsername());

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_beranda));
    }

    private void isLoggedIn(){
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPress > 5000){
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        }else{
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        selectMenu(id);
        return true;
    }

    private void selectMenu(int id) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_beranda) {
            fragment = new MapFragment();
            title = getString(R.string.nav_home);
        } else if (id == R.id.nav_geotag) {
            fragment = new MapFragment();
            title =  getString(R.string.nav_geotag);
        }else if (id == R.id.nav_tracking) {
            fragment = new MapFragment();
            title =  getString(R.string.nav_tracking);
        }else if (id == R.id.nav_kml) {
            fragment = new KmlFragment();
            title =  getString(R.string.nav_kml);
        }else if (id == R.id.nav_logout) {
            fragment = new MapFragment();
            title =  getString(R.string.nav_logout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}
