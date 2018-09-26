/*
September 24, 2018
https://code.tutsplus.com/tutorials/google-play-services-using-the-nearby-connections-api--cms-24534

 */
package com.example.yourname.ctldigitalshoutdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.*;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
	String TAG = "CTLDebug";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: Created");
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
				!= PackageManager.PERMISSION_GRANTED) {
			Log.d(TAG, "onCreate: permission is not granted");
			// Permission is not granted
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_CONTACTS)) {
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
			} else {
				// No explanation needed; request the permission
				Log.d(TAG, "onCreate: No explanation needed");
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_CONTACTS},
						0); //it is supposed to be MY_PERMISSIONS_REQUEST_READ_CONTACTS not 0

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		} else {
			// Permission has already been granted
			Log.d(TAG, "onCreate: Permission already granted");
		}
		Log.d(TAG, "onCreate: Permission check is completed");

		setContentView(R.layout.activity_main);
		FusedLocationProviderClient client =
				LocationServices.getFusedLocationProviderClient(this);

	}
	/*
	from https://developer.android.com/training/permissions/requesting#java
	When the user responds to your app's permission request, the system invokes your app's onRequestPermissionsResult() method, passing it the user response. Your app has to override that method to find out whether the permission was granted. The callback is passed the same request code you passed to requestPermissions(). For example, if an app requests READ_CONTACTS access it might have the following callback method:
	 */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            // I dont know what MY_PERMISSIONS_REQUEST_READ_CONTACTS is supposed to be.
            default:
            case 0: { //IT is supposed to be MY_PERMISSIONS_REQUEST_READ_CONTACTS not 0 but since I don't know what MY_PERMISSIONS_REQUEST_READ_CONTACTS is, I am just leaving 0 here
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    String SERVICE_ID = "service_id_here";
    private String getUserNickname(){
    	return "user_nickname_here";
	}

	private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
			new ConnectionLifecycleCallback() {


				@Override
				public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
					Log.d(TAG, "onConnectionInitiated: ");
				}

				@Override
				public void onConnectionResult(String endpointId, ConnectionResolution result) {
					Log.d(TAG, "onConnectionResult: ");
				}

				@Override
				public void onDisconnected(String endpointId) {
					// We've been disconnected from this endpoint. No more data can be
					// sent or received.
					Log.d(TAG, "onDisconnected: ");
				}
			};

	private void startAdvertising() {
		Log.d(TAG, "startAdvertising: Called");
		Nearby.getConnectionsClient(this).startAdvertising(
				getUserNickname(),
				SERVICE_ID,
				mConnectionLifecycleCallback,new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
		);
	}

	public void onClickBttnAdvertise(View view) {
		startAdvertising();
	}

	public void onClickBttnDiscover(View view) {
	}

	public void onClickBttnTransmit(View view) {
	}
}
