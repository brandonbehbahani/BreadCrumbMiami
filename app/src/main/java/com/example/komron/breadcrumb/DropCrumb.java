
package com.example.komron.breadcrumb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;


public class DropCrumb extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private EditText editTextUsername;
    private EditText editTextCrumbTitle;
    private EditText editTextCrumbContent;
    private double latitude, longitude;
    private String sector;
    private RadioButton radioButtonGreen;
    private RadioButton radioButtonBlue;
    private RadioButton radioButtonPurple;
    Date currentDate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_crumb);
        editTextCrumbContent = (EditText) findViewById(R.id.editTextCrumbContent);
        editTextUsername = (EditText) findViewById(R.id.editTextUserName);
        editTextCrumbTitle = (EditText) findViewById(R.id.editTextCrumbTitle);
        radioButtonBlue = (RadioButton) findViewById(R.id.radioButtonBlue);
        radioButtonGreen = (RadioButton) findViewById(R.id.radioButtonGreen);
        radioButtonPurple = (RadioButton) findViewById(R.id.radioButtonPurple);
        context = getApplicationContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            latitude = mLastLocation.getLatitude();
                            longitude = mLastLocation.getLongitude();

                            sendCrumbToDatabase();

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            Toast.makeText(context, "No Location Detected", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onClickPostCrumb(View view) {
        if(editTextUsername.getText().toString().equals("") || editTextCrumbContent.getText().toString().equals("")|| editTextCrumbTitle.getText().toString().equals("")){
            Toast.makeText(context, "There is some information missing", Toast.LENGTH_SHORT).show();
        } else {
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                getLastLocation();
            }
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
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Toast.makeText(this, "Displaying permission rationale to provide additional context.", Toast.LENGTH_LONG).show();
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    };

        } else {
            Log.i(TAG, "Requesting permission");

            startLocationPermissionRequest();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
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

    public void sendCrumbToDatabase(){
        double trueLatitude, trueLongitude;
        String crumbContent = editTextCrumbContent.getText().toString();
        String userName = editTextUsername.getText().toString();
        String crumbTitle = editTextCrumbTitle.getText().toString();
        String color = "blue";

        Toast.makeText(context, "Your post is being processed", Toast.LENGTH_SHORT).show();

        if ((latitude > 25 && latitude < 26) && (longitude < -80 && longitude > -81)){
            currentDate = Calendar.getInstance().getTime();
            trueLatitude = latitude;
            trueLongitude = longitude;
            longitude = longitude * -1;
            if (radioButtonPurple.isChecked()){
                color = "purple";
            }
            if (radioButtonBlue.isChecked()){
                color = "blue";
            }
            if (radioButtonGreen.isChecked()){
                color = "green";
            }
            sector = "a" + (String.valueOf(latitude).substring(3,5)) + "b" + (String.valueOf(longitude).substring(3,5) );
            BackgroundWorker b = new BackgroundWorker(this);
            b.execute(sector , String.valueOf(trueLatitude), String.valueOf(trueLongitude), userName, crumbTitle, crumbContent, color);
            b.onPostExecute("test");
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
