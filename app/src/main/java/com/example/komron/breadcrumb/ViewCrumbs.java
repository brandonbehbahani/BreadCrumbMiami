package com.example.komron.breadcrumb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewCrumbs extends FragmentActivity implements OnMapReadyCallback {


    private ArrayList<Crumbs> allCrumbs;
    private GoogleMap mMap;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue requestQueue;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private double mLatitude, mLongitude;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private String sector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_crumbs);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        allCrumbs = new ArrayList<>();
        xyz();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        if (!success) {
            Toast.makeText(this, "Style could not be loaded", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            mLatitude = mLastLocation.getLatitude();
                            mLongitude = mLastLocation.getLongitude();
                            sector = "a" + String.valueOf(mLatitude).substring(3, 5) + "b" + String.valueOf(mLongitude * -1).substring(3, 5);
                            CALL_DATA();
                            LatLng yourLocation = new LatLng(mLatitude, mLongitude);
                           // Toast.makeText(ViewCrumbs.this, "You are in sector: " + sector, Toast.LENGTH_SHORT).show();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 20), 5000, null);
                        } else {
                            Toast.makeText(ViewCrumbs.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void CALL_DATA(){
        String JSON_URL = ""; //Todo: add the get_crumbs php code url here
        JSON_URL += "?id=" + sector;
        Log.v("testingid", JSON_URL);
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null,  new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        PARSE_DATA(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("network_error", error.getNetworkTimeMs() + "");
                    }
                }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void PARSE_DATA(JSONArray array){
        String userName = "";
        String color = "";
        double latitude;
        double longitude;
        String content = "";
        String title = "";
        for(int i = 0; i < array.length(); i++){
            JSONObject json = null;
            try{
                json = array.getJSONObject(i);
                latitude = Double.parseDouble(json.getString("crumbLatitude"));
                longitude = Double.parseDouble(json.getString("crumbLongitude"));
                userName = json.getString("userName");
                title = json.getString("crumbTitle");
                content = json.getString("crumbContent");
                color = json.getString("color");
                allCrumbs.add(new Crumbs(latitude, longitude, userName, title, content, color));

            }
            catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
        populateMap();

    }



    public void populateMap(){ // adding the markers to the map
        Crumbs currentCrumbs;
        LatLng currentLatLng;
        String currentContent;
        float markerColor;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(ViewCrumbs.this));
        for (int i  = 0; i < allCrumbs.size(); i++){
            currentCrumbs = allCrumbs.get(i);


            currentContent = "User: " + currentCrumbs.getUserName() + "\n";
            currentContent += "Latitude: " + currentCrumbs.getLatitude() + "\n";
            currentContent += "Longitude: " + currentCrumbs.getLongitude() + "\n\n";
            currentContent += "info: \n" + currentCrumbs.getContent();
            currentLatLng = new LatLng(currentCrumbs.getLatitude(),currentCrumbs.getLongitude());

            if(currentCrumbs.getColor().equals("blue")){
                mMap.addMarker(
                        new MarkerOptions().position(currentLatLng).snippet(currentContent).title(currentCrumbs.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );

            } else if (currentCrumbs.getColor().equals("green")){
                mMap.addMarker(
                        new MarkerOptions().position(currentLatLng).snippet(currentContent).title(currentCrumbs.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );

            } else {
                mMap.addMarker(
                    new MarkerOptions().position(currentLatLng).snippet(currentContent).title(currentCrumbs.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            );

            }


        }
    }



    public void xyz() {
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Toast.makeText(this, "Displaying permission rationale to provide additional context.", Toast.LENGTH_LONG).show();
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request permission
                    startLocationPermissionRequest();
                }
            };

        } else {
            startLocationPermissionRequest();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                };
            }
        }
    }

}
