package com.example.reto1apps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Marker myLocation;
    private Polygon icesiArea;
    private Button plusBtn;
    private boolean state;
    private int CALL_BACK_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        state = false;
        plusBtn = findViewById(R.id.plus_btn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = true;
               // plusBtn.setBackgroundResource(R.drawable.button_style2);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        icesiArea = mMap.addPolygon(new PolygonOptions().add(
                new LatLng(3.343017, -76.530918),
                new LatLng(3.343350, -76.527615),
                new LatLng(3.338680, -76.527208),
                new LatLng(3.338500, -76.531390)
        ));
        LatLng icesi = new LatLng(3.341441,-76.529544);

        myLocation = mMap.addMarker(new MarkerOptions().position(icesi).title("Icesi"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(icesi, 15));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CALL_BACK_ID);
            }

        }

        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(state){
                   MarkerOptions marker = new MarkerOptions().position(latLng);
                    mMap.addMarker(marker);
               //     getCurrentLocation();
                    state = false;
                }
            }
        });

    }

    private void getCurrentLocation(){
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task task = fusedLocationProviderClient.getLastLocation();
        Location location = null;
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Location loc = (Location)o;
                LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
                myLocation.setPosition(location);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
                Toast.makeText(MapsActivity.this,loc.getLatitude()+" "+loc.getLongitude(),Toast.LENGTH_SHORT).show();
                System.out.println( loc.getLatitude() + " - " + loc.getLongitude());
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
    myLocation.setPosition(pos);
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
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
