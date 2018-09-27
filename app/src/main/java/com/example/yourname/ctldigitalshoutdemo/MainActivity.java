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
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
	String TAG = "CTLDebug";

	void requestPermission(String permission){

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: Created");

		String[] permissionsRequested ={Manifest.permission.ACCESS_COARSE_LOCATION};

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
						permissionsRequested,
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
		Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            // I dont know what MY_PERMISSIONS_REQUEST_READ_CONTACTS is supposed to be.
            default:
            case 0: { //IT is supposed to be MY_PERMISSIONS_REQUEST_READ_CONTACTS not 0 but since I don't know what MY_PERMISSIONS_REQUEST_READ_CONTACTS is, I am just leaving 0 here
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
					Log.d(TAG, "onRequestPermissionsResult: permission was granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
					Log.d(TAG, "onRequestPermissionsResult: permission is denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    //The serviceId value must uniquely identify your app. As a best practice, use the package name of your app (for example, com.google.example.myapp
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
	private final EndpointDiscoveryCallback mEndpointDiscoveryCallback =
			new EndpointDiscoveryCallback() {
				@Override
				public void onEndpointFound(
						String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
					// An endpoint was found!
					Log.d(TAG, "onEndpointFound: " + endpointId + ", " + discoveredEndpointInfo);
				}

				@Override
				public void onEndpointLost(String endpointId) {
					// A previously discovered endpoint has gone away.
					Log.d(TAG, "onEndpointLost: " + endpointId);
				}
			};
	private void startDiscovery() {
		Log.d(TAG, "startDiscovery: ");
		Nearby.getConnectionsClient(this).startDiscovery(
				SERVICE_ID,
				mEndpointDiscoveryCallback,
				new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
		).addOnSuccessListener(
				new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void unusedResult) {
						// We're discovering!
						Log.d(TAG, "onSuccess: startDiscovery is discovering " + unusedResult);
					}
				})
				.addOnFailureListener(
						new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								// We were unable to start discovering.
								Log.d(TAG, "onFailure: startDiscovery unable to start discover " + e);
							}
						}
				);
	}
	public void onClickBttnAdvertise(View view) {
		Log.d(TAG, "onClickBttnAdvertise: ");
		startAdvertising();
	}

	public void onClickBttnDiscover(View view) {
		Log.d(TAG, "onClickBttnDiscover: ");
		startDiscovery();
	}

	public void onClickBttnTransmit(View view) {
	}
}
