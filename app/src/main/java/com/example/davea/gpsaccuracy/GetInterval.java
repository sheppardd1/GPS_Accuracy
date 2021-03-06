package com.example.davea.gpsaccuracy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class GetInterval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_interval);



        final EditText interval_input = findViewById(R.id.interval_input);

        Button done = findViewById(R.id.done);

        TextView TV = findViewById(R.id.instructions);

        TV.setText("Enter the update interval in seconds");

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove location listener so that we don't have duplicated if going back to MainActivity after having been there before
                if(MainActivity.locationListener != null) {
                    MainActivity.locationManager.removeUpdates(MainActivity.locationListener);
                }

                //note: using an unsigned EditText, so don't have to worry about negative numbers
                if(interval_input.getText().toString().trim().length() > 0) {   //ensure interval_input is not empty
                    MainActivity.interval = Integer.valueOf(interval_input.getText().toString()); //set interval to value specified in interval_input
                    MainActivity.interval *= 1000;  //convert seconds into milliseconds
                    MainActivity.setInterval = true;    //ensures that this activity only runs once
                    //go back to main activity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else{
                    Toast.makeText(GetInterval.this, "Must Enter a Value", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
