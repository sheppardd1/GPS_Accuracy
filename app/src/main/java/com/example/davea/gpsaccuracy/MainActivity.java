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
    TextView TV1;   //top textview
    TextView TV2;   //bottom textview
    Button startStop;   //start/stop button for pausing/resuming data collection
    public Location currentLocation;
    LocationListener locationListener;

    final int UPDATE_INTERVAL = 1000;   //when on, update location data every UPDATE_INTERVAL milliseconds

    public boolean on = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();    //set up basic

        startStop.setOnClickListener(new View.OnClickListener() {   //start-stop button
            @Override
            public void onClick(View v) {
                on = !on;
                //show status to user
                if(!on) TV1.setText("Paused\n");
                else TV1.setText("Running\n");
            }
        });

        if(on) locationDetails();   //only get data when not paused


    }

    public void setup(){

        TV1 = findViewById(R.id.TV1);
        TV1.setText("Running\n");
        TV2 = findViewById(R.id.TV2);
        startStop = findViewById(R.id.startStop);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    }

    public void locationDetails(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //when location changes, display accuracy of that reading
                currentLocation = location;
                if(on) accuracy();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //not used right now
            }

            @Override
            public void onProviderEnabled(String provider) {
                //not used right now
            }

            @Override
            public void onProviderDisabled(String provider) {
                TV1.setText("GPS permissions have been denied.\nNeed GPS permissions for app to function.");
            }
        };

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS not available", Toast.LENGTH_LONG);
        }

        //if at least Marshmallow, need to ask user's permission to get GPS data
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if permission is not yet granted, ask for it
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //if permission still not granted, tell user app will not work without it
                    Toast.makeText(this, "Need GPS permissions for app to function", Toast.LENGTH_LONG);
                }
                //once permission is granted, set up location listener
                //updating every UPDATE_INTERVAL milliseconds, regardless of distance change
                else locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
            }
            else locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
        }
        else locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);

    }

    public void accuracy(){
        //print accuracy value on screen
        TV2.setText("Accuracy:\n" + currentLocation.getAccuracy());
    }




}
