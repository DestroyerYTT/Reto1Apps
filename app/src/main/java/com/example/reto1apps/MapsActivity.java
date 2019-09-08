package com.example.reto1apps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.Transliterator;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, LocationListener, GoogleMap.OnMarkerClickListener {

    /**
     * Google map
     */
    private GoogleMap mMap;

    /**
     * Marker of the current location of the user
     */
    private Marker myLocation;

    /**
     * List of Markers put on the map
     */
    private ArrayList<Marker> markersOnMap;

    /**
     * Dialog to show and write the name of the markers
     */
    private AlertDialog makerName;

    /**
     * Object to get the direction of the current position
     */
    private Geocoder coder;

    /**
     * Button to allow to put more markers
     */
    private Button plusBtn;

    /**
     * Text view to show the nearest location to current location
     */
    private TextView locationTextView;

    /**
     * Boolean attribute to put more markers
     */
    private boolean state;

    /**
     *  Variable to save the call back of permission
     */
    private int CALL_BACK_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Inicial state
        state = false;

        makerName = new AlertDialog.Builder(MapsActivity.this).create();

        locationTextView = findViewById(R.id.location_tv);

        plusBtn = findViewById(R.id.plus_btn);

        //Setting the listener to plusBtn
        plusBtn.setOnClickListener(new View.OnClickListener() {

            /**
             * Listener of the plusBtn when click on it
             * @param view
             */
            @Override
            public void onClick(View view) {
                state = true;
                Toast.makeText(getApplicationContext(), "-- Click on the map to a put a marker --", Toast.LENGTH_LONG).show();
                plusBtn.setVisibility(View.GONE);

            }
        });
    }


    /**
     * Method for manipulates the map once available
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Inicialiation of the Google Map
        mMap = googleMap;

        coder = new Geocoder(getApplicationContext());

        // Setting the listener when there is a click on a marker
        mMap.setOnMarkerClickListener(this);

        //Inicialize a instace of array
        if (markersOnMap == null) {
            markersOnMap = new ArrayList<>();
        }

        //Inicial position
        LatLng pos = new LatLng(3.341441,-76.529544);

        // Inicialization of current location
        myLocation = mMap.addMarker(new MarkerOptions().position(pos).title("Current Location"));

        // Setting the icon to the current location
        myLocation.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.position1));

        // Animating the camera to focus on the current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));

        // Code to ask permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CALL_BACK_ID);
            }

        }

        // System service to get the current location
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        //Setting the listener when there is a click on the map
        mMap.setOnMapClickListener(this);
    }

    /**
     *  Method to handle when there is a click on the map
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if(state){
            MarkerOptions marker = new MarkerOptions().position(latLng);
            Marker temp = mMap.addMarker(marker);
            markersOnMap.add(temp);
            state = false;
            putTitle(temp);
            plusBtn.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation.setPosition(pos);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, mMap.getCameraPosition().zoom));
        updateNearestPlace();
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

    public void putTitle(final Marker marker){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Please, give a name to the marker");
        alert.setTitle("Maker inicialization");

        final EditText inputTitle = new EditText(this);

        alert.setView(inputTitle);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String message = inputTitle.getText().toString();
                marker.setTitle(message);
            }
        });

        alert.show();
    }


    /**
     * Method from internet to calculate the distance
     * @param startP Start position
     * @param endP   End position
     * @return The distance between the start position and the end position
     */
    public double calculationByDistance(LatLng startP, LatLng endP) {
        double R = 6371000;
        double deltaLat = endP.latitude - startP.latitude;
        double deltaLong = endP.longitude - startP.longitude;
        double deltaLatRad = Math.toRadians(deltaLat);
        double deltaLongRad = Math.toRadians(deltaLong);
        double a = Math.pow(deltaLatRad/2, 2) + Math.cos(startP.longitude) * Math.cos(endP.longitude)* Math.pow(deltaLongRad/2, 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = R * c;
        return distance;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String location = "Current location";
        double dist = calculationByDistance(marker.getPosition(), myLocation.getPosition());
        DecimalFormat newFormat = new DecimalFormat("######.##");
        String distance = newFormat.format(dist);
        if(!distance.startsWith("0")){
            marker.setSnippet("Distance to current position: " + distance +" meters");
        }else{
            try{
                //Current position
                location = coder.getFromLocation(myLocation.getPosition().latitude, myLocation.getPosition().longitude, 1).get(0).getAddressLine(0);
            }catch (Exception e){
                e.printStackTrace();
            }
            marker.setSnippet(location);
        }
        return false;
    }

    public void updateNearestPlace(){
        String text = "The nearest marker to the current position is: ";
        HashMap<Double, String> places = new HashMap<>();
        double distances[] = new double[markersOnMap.size()];

        int counter = 0;
        for(Marker marker : markersOnMap){
            double distance = calculationByDistance(marker.getPosition(), myLocation.getPosition());
            distances[counter] = distance;
            counter++;
            places.put(new Double(distance), marker.getTitle());
        }
        Arrays.sort(distances);
        if(counter > 0){
            if(places.get(new Double(distances[0])) != null){

                if(distances[0] < 10){
                  text = "You are in: " +  places.get(new Double(distances[0]));
                }else{
                    text = text + places.get(new Double(distances[0]));
                }
            }
        }
        locationTextView.setText(text);
        locationTextView.setVisibility(View.VISIBLE);
    }


    }

