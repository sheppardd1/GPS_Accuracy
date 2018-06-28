package com.example.davea.gpsaccuracy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //UI:
    TextView TV1;   //top textview
    TextView TV2;   //bottom textview
    Button startStop;   //start/stop button for pausing/resuming data collection

    //Location:
    public Location currentLocation;
    LocationManager locationManager;
    LocationListener locationListener;

    //Time:
    //create simple date format to show just 12hr time
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa");

    //Constants:
    //final int UPDATE_INTERVAL = 1000;   //when on, update location data every UPDATE_INTERVAL milliseconds

    //Variables:
    static int interval = 1000; //default update interval is 1000 seconds
    int numDataPoints = 0;  //number of times location has updated
    public boolean on = true;   //true if location is actively being reequested
    static boolean setInterval = false; //true if update interval has been set
    boolean setFirstSignalTime = false; //true if firstSignalTime has been set for the session
    String startTime;   //time that Start button is pressed to start program
    long firstSignalTime = 0;   //time that first GPS signal is received



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
                if(!on) {
                    TV1.setText("Stopped\n");
                    if(TV2.getText() != "") {
                        TV2.setText(TV2.getText() + "\nEnd Time: " + dateFormat.format(System.currentTimeMillis()) + "\n\nUpdate Interval: " + (interval/1000) + " second\nReadings taken: " + numDataPoints);
                    }
                    else{
                        TV2.setText("");
                    }
                    setFirstSignalTime = false;
                    numDataPoints = 0;
                    locationManager.removeUpdates(locationListener);
                }
                else{
                    TV1.setText("Running\n");
                    TV2.setText("");
                    locationDetails();
                }
            }
        });

        locationDetails();   //only get data when not paused

    }

    public void setup(){

        if(setInterval == false) {
            startActivity(new Intent(getApplicationContext(), GetInterval.class));
        }

        TV1 = findViewById(R.id.TV1);
        TV1.setText("Running\n");
        TV2 = findViewById(R.id.TV2);
        startStop = findViewById(R.id.startStop);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    public void locationDetails(){
        if(setInterval) {   //ensures that interval has been set
            final int UPDATE_INTERVAL = interval;   //set UPDATE_INTERVAL to user-specified value
            startTime = dateFormat.format(System.currentTimeMillis());
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //when location changes, display accuracy of that reading
                    currentLocation = location;
                    accuracy();
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

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS not available", Toast.LENGTH_LONG);
            }

            //if at least Marshmallow, need to ask user's permission to get GPS data
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //if permission is not yet granted, ask for it
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //if permission still not granted, tell user app will not work without it
                        Toast.makeText(this, "Need GPS permissions for app to function", Toast.LENGTH_LONG);
                    }
                    //once permission is granted, set up location listener
                    //updating every UPDATE_INTERVAL milliseconds, regardless of distance change
                    else
                        locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
                } else
                    locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
            } else {
                assert locationManager != null;
                locationManager.requestLocationUpdates("gps", UPDATE_INTERVAL, 0, locationListener);
            }
        }

    }

    public void accuracy(){
        //convert epoch time to calendar data
        if(!setFirstSignalTime){
            firstSignalTime = System.currentTimeMillis();
            setFirstSignalTime = true;
        }
        //print accuracy value on screen along with coordinates and start time
        TV2.setText("Accuracy: " + currentLocation.getAccuracy() + "\n\nLatitude: " + currentLocation.getLatitude()
                + "\nLongitude: " + currentLocation.getLongitude()+ "\n\n" + "Start Time: " + startTime
                + "\nFirst GPS Signal: " + dateFormat.format(firstSignalTime));

        ++numDataPoints;
    }




}
