package com.example.davea.gpsaccuracy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    TextView TV1;
    TextView TV2;
    Button startStop;
    public Location currentLocation;
    LocationListener locationListener;

    final int UPDATE_INTERVAL = 5000;

    public boolean on = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on = !on;

                if(!on) TV1.setText("Paused\n");
                else TV1.setText("Running\n");
            }
        });

        locationDetails();


    }

    public void setup(){

        TV1 = findViewById(R.id.TV1);
        TV1.setText("Running");
        TV2 = findViewById(R.id.TV2);
        startStop = findViewById(R.id.startStop);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    }

    public void locationDetails(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                if(on) accuracy();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS not available", Toast.LENGTH_LONG);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {    //if at least Marshmallow, need to ask user's permission to get GPS data
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Need GPS permissions for app to function", Toast.LENGTH_LONG);
                }
                else locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
            }
            else locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
        }
        else locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);

    }

    public void accuracy(){
        TV2.setText("Accuracy:\n" + currentLocation.getAccuracy());
    }

    //may not need:
    public void getCurrentLocation() {

    }



}
