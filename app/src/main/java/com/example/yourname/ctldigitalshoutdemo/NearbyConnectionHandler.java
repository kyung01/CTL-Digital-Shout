package com.example.yourname.ctldigitalshoutdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class NearbyConnectionHandler {
	String TAG = "NearbyConnectionHandler";
	String USER_NICKNAME = "USER_NICKNAME_DEFAULT";
	String SERVICE_ID = "SERVICE_ID_DEFAULT";

	boolean isAdvertising = false;
	boolean isDiscovering = false;

	NearbyConnectionHandler(String userNickName, String serviceId){
		USER_NICKNAME = userNickName;
		SERVICE_ID = serviceId;
	}




	//The serviceId value must uniquely identify your app. As a best practice, use the package name of your app (for example, com.google.example.myapp



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

	public void startAdvertising(AppCompatActivity activity) {
		Log.d(TAG, "startAdvertising: Called");
		isAdvertising = true;
		Nearby.getConnectionsClient(activity).startAdvertising(
				USER_NICKNAME,
				SERVICE_ID,
				mConnectionLifecycleCallback,new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
		)
				.addOnSuccessListener(
						new OnSuccessListener<Void>() {
							@Override
							public void onSuccess(Void unusedResult) {
								// We're advertising!
								Log.d(TAG, "onSuccess: Advertisement Success " + unusedResult);
							}
						})
				.addOnFailureListener(
						new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								// We were unable to start advertising.
								Log.d(TAG, "onFailure: Advertisement Failure " + e);
							}
						});
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
	public void startDiscovery(AppCompatActivity activity) {
		Log.d(TAG, "startDiscovery: ");
		isDiscovering = true;
		Nearby.getConnectionsClient(activity).startDiscovery(
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

}
