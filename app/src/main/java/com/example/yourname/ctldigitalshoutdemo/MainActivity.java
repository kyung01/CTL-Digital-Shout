package com.example.yourname.ctldigitalshoutdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.location.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(this);

    }
}
