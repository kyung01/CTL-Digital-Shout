package com.example.yourname.ctldigitalshoutdemo;

import java.util.*;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
	//void onEndpointAdded(String endpoint);
	//void onEndpointRemoved(String endpoint);
	void onPayoadMessageReceived(String endpoint, String content);

	void onDcvEndPointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo);
	void onDcvEndPointLost(String endpointId);

	void onConnectionRequested(String endpoint, ConnectionInfo info);
	void onConnectionRequestedResult(String endpointId, ConnectionResolution result);
	void onDisconnected(String endpointId);
}

public class NearbyConnectionHandler {

	String TAG = "NearbyConnectionHandler";
	String SERVICE_ID = "SERVICE_ID_DEFAULT";

	boolean isAdvertising = false;
	boolean isDiscovering = false;
	List<NearbyConnectionListener> listeners = new ArrayList<NearbyConnectionListener>();

	NearbyConnectionHandler(AppCompatActivity activity, String userNickName, String serviceId){
		SERVICE_ID = serviceId;
	}




	//The serviceId value must uniquely identify your app. As a best practice, use the package name of your app (for example, com.google.example.myapp

	private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
			new ConnectionLifecycleCallback() {
				@Override
				public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
					// Automatically accept the connection on both sides.
					Log.d(TAG, "onConnectionInitiated: Accepting the connection " + endpointId + ", " + connectionInfo);

					for(NearbyConnectionListener lst: listeners){
						lst.onConnectionRequested(endpointId, connectionInfo);
					}

				}

				@Override
				public void onConnectionResult(String endpointId, ConnectionResolution result) {
					Log.d(TAG, "onConnectionResult: "+ endpointId+ ", "+ result);

					for(NearbyConnectionListener lst: listeners){
						lst.onConnectionRequestedResult(endpointId, result);
					}
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
					Log.d(TAG, "onDisconnected: " +endpointId);

					for(NearbyConnectionListener lst: listeners){
						lst.onDisconnected(endpointId);
					}
				}
			};




	public void stopAdvertising(AppCompatActivity activity) {
		isAdvertising = false;
		Nearby.getConnectionsClient(activity).stopAdvertising();
	}

	public void startAdvertising(AppCompatActivity activity, String userNickname) {
		Log.d(TAG, "startAdvertising: Called");
		isAdvertising = true;
		Nearby.getConnectionsClient(activity).stopAdvertising();
		Nearby.getConnectionsClient(activity).startAdvertising(
				userNickname,
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
						final String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
					for(NearbyConnectionListener lst: listeners){
						lst.onDcvEndPointFound(endpointId, discoveredEndpointInfo);
					}

				}

				@Override
				public void onEndpointLost(String endpointId) {
					for(NearbyConnectionListener lst: listeners){
						lst.onDcvEndPointLost(endpointId);
					}
				}
			};
	public void startDiscovery(AppCompatActivity activity) {
		Log.d(TAG, "startDiscovery: ");
		isDiscovering = true;
		Nearby.getConnectionsClient(activity).stopDiscovery();
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

	public void stopDiscovery(AppCompatActivity activity){
		Nearby.getConnectionsClient(activity).stopDiscovery();

	}


	public void sendPayload(AppCompatActivity activity, String endpoint, String content) {
		Log.d(TAG, "sendPayload: Attempting to send pay load [" + content + "] to endpoint: " + endpoint);
		Payload payload = Payload.fromBytes(content.getBytes());
		Nearby.getConnectionsClient(activity).sendPayload(endpoint,payload);
	}


	private final SimpleArrayMap<Long, NotificationCompat.Builder> incomingPayloads = new SimpleArrayMap<>();
	private final SimpleArrayMap<Long, NotificationCompat.Builder> outgoingPayloads = new SimpleArrayMap<>();
	//...


	/*

	private NotificationCompat.Builder buildNotification(AppCompatActivity activity, Payload payload, boolean isIncoming) {
		NotificationCompat.Builder notification = new NotificationCompat.Builder(activity,"channelID")
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

	 */

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
			/*

			// Build and start showing the notification.
			//NotificationCompat.Builder notification = buildNotification(payload, true );
			//((NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE)).notify((int) payload.getId(), notification.build());

			// Add it to the tracking list so we can update it.
			//incomingPayloads.put(payload.getId(), notification);
			/*

			 */
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


	public void acceptRequest(Context context, String endpoint) {

		Nearby.getConnectionsClient(context).acceptConnection(endpoint, mPayloadCallback);
	}
	public void requestConnection(Context context, String userNickname, String endpoint){
		Nearby.getConnectionsClient(context).requestConnection(userNickname,endpoint, mConnectionLifecycleCallback);
	}
	public void requestDisconnect(Context context, String endpoint){
		Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpoint);
	}


}
