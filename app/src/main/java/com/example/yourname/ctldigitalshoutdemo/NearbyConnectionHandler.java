package com.example.yourname.ctldigitalshoutdemo;

import java.util.*;

import android.Manifest;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;
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
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
interface NearbyConnectionListener{
	void onEndpointAdded(String endpoint);
	void onEndpointRemoved(String endpoint);
	void onPayoadMessageReceived(String endpoint, String content);
}

public class NearbyConnectionHandler {
	public static AppCompatActivity context;

	String TAG = "NearbyConnectionHandler";
	String USER_NICKNAME = "USER_NICKNAME_DEFAULT";
	String SERVICE_ID = "SERVICE_ID_DEFAULT";

	boolean isAdvertising = false;
	boolean isDiscovering = false;
	List<String> endpoints = new ArrayList<String>();
	List<NearbyConnectionListener> listeners = new ArrayList<NearbyConnectionListener>();

	NearbyConnectionHandler(AppCompatActivity activity, String userNickName, String serviceId){
		USER_NICKNAME = userNickName;
		SERVICE_ID = serviceId;
	}

	void addEndpoint(String addedEndpoint){
		for(int i = 0; i < endpoints.size();i++){
			if(endpoints.get(i) == addedEndpoint){
				return;
			}
		}
		endpoints.add(addedEndpoint);
		for(NearbyConnectionListener lst: listeners){
			lst.onEndpointAdded(addedEndpoint);
		}
	}
	void removeEndpoint(String removedEndpoint){
		for(int i = 0; i < endpoints.size();i++){
			if(endpoints.get(i) == removedEndpoint){
				endpoints.remove(i);
				for(NearbyConnectionListener lst: listeners){
					lst.onEndpointAdded(removedEndpoint);
				}
				return;
			}
		}
	}




	//The serviceId value must uniquely identify your app. As a best practice, use the package name of your app (for example, com.google.example.myapp



	private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
			new ConnectionLifecycleCallback() {
				@Override
				public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
					// Automatically accept the connection on both sides.
					Log.d(TAG, "onConnectionInitiated: Accepting the connection");
					Nearby.getConnectionsClient(context).acceptConnection(endpointId, mPayloadCallback);
				}

				@Override
				public void onConnectionResult(String endpointId, ConnectionResolution result) {
					Log.d(TAG, "onConnectionResult: ");
					switch (result.getStatus().getStatusCode()) {
						case ConnectionsStatusCodes.STATUS_OK:
							// We're connected! Can now start sending and receiving data.
							Log.d(TAG, "onConnectionResult: STATUS_OK");
							break;
						case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
							// The connection was rejected by one or both sides.
							Log.d(TAG, "onConnectionResult: REJECTED");
							break;
						case ConnectionsStatusCodes.STATUS_ERROR:
							// The connection broke before it was able to be accepted.
							Log.d(TAG, "onConnectionResult: ERROR");
							break;
					}

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
					addEndpoint(endpointId);
					Nearby.getConnectionsClient(context).requestConnection(
							USER_NICKNAME,
							endpointId,
							mConnectionLifecycleCallback)
							.addOnSuccessListener(
									new OnSuccessListener<Void>() {
										@Override
										public void onSuccess(Void unusedResult) {
											// We successfully requested a connection. Now both sides
											// must accept before the connection is established.

											Log.d(TAG, "onSuccess: Successfully requested a connection");

										}
									})
							.addOnFailureListener(
									new OnFailureListener() {
										@Override
										public void onFailure(@NonNull Exception e) {
											// Nearby Connections failed to request the connection.
											Log.d(TAG, "onFailure: Failed to request the connection");
										}
									});

				}

				@Override
				public void onEndpointLost(String endpointId) {
					// A previously discovered endpoint has gone away.
					Log.d(TAG, "onEndpointLost: " + endpointId);
					removeEndpoint(endpointId);
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

	public void sendPayload(String endpoint, String content) {
		Log.d(TAG, "sendPayload: Attempting to send pay load [" + content + "] to endpoint: " + endpoint);
		Payload payload = Payload.fromBytes(content.getBytes());
		Nearby.getConnectionsClient(context).sendPayload(endpoint,payload);

	}

	private final SimpleArrayMap<Long, NotificationCompat.Builder> incomingPayloads = new SimpleArrayMap<>();
	private final SimpleArrayMap<Long, NotificationCompat.Builder> outgoingPayloads = new SimpleArrayMap<>();
	//...


	private NotificationCompat.Builder buildNotification(Payload payload, boolean isIncoming) {
		NotificationCompat.Builder notification = new NotificationCompat.Builder(context,"channelID")
				.setContentTitle(isIncoming ? "Receiving..." : "Sending...");
		int size = payload.asBytes().length;
		boolean indeterminate = false;
		if (size == -1) {
			// This is a stream payload, so we don't know the size ahead of time.
			size = 100;
			indeterminate = true;
		}
		notification.setProgress(size, 0, indeterminate);
		return notification;
	}

	//...
	PayloadCallback mPayloadCallback = new PayloadCallback() {

		//Payload
		@Override
		public void onPayloadReceived(String endpointId, Payload payload) {
			Log.d(TAG, "onPayloadReceived: Payload is here!");
			if (payload.getType() == Payload.Type.BYTES) {
				// No need to track progress for bytes.
				String message = new String(payload.asBytes());
				Log.d(TAG, "onPayloadReceived: received: " + message );
				for(NearbyConnectionListener l : listeners)
					l.onPayoadMessageReceived(endpointId,message);
				return;
			}

			// Build and start showing the notification.
			NotificationCompat.Builder notification = buildNotification(payload, true /*isIncoming*/);
			((NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE)).notify((int) payload.getId(), notification.build());

			// Add it to the tracking list so we can update it.
			incomingPayloads.put(payload.getId(), notification);
		}

		@Override
		public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
			Log.d(TAG, "onPayloadTransferUpdate: Called");
			long payloadId = update.getPayloadId();
			NotificationCompat.Builder notification;
			if (incomingPayloads.containsKey(payloadId)) {
				notification = incomingPayloads.get(payloadId);
				if (update.getStatus() != PayloadTransferUpdate.Status.IN_PROGRESS) {
					// This is the last update, so we no longer need to keep track of this notification.
					incomingPayloads.remove(payloadId);
				}
			} else if (outgoingPayloads.containsKey(payloadId)) {
				notification = outgoingPayloads.get(payloadId);
				if (update.getStatus() != PayloadTransferUpdate.Status.IN_PROGRESS) {
					// This is the last update, so we no longer need to keep track of this notification.
					outgoingPayloads.remove(payloadId);
				}
			}

			switch(update.getStatus()) {
				case PayloadTransferUpdate.Status.IN_PROGRESS:
					break;
				case PayloadTransferUpdate.Status.SUCCESS:
					break;
				case PayloadTransferUpdate.Status.CANCELED:
					break;
				case PayloadTransferUpdate.Status.FAILURE:
					break;
			}
			try{

				//((NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE)).notify((int) payloadId,
				//		((NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE)).build());

			}
			catch(Exception e){
				Log.d(TAG, "onPayloadTransferUpdate: ERROR. Notification was not initialized");
			}
		}
	};


}
