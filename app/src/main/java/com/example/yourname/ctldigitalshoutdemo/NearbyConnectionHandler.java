package com.example.yourname.ctldigitalshoutdemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.*;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;

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

	void onEchoMessage(PayloadContent content);
}

public class NearbyConnectionHandler {

	String TAG = "NearbyConnectionHandler";
	String SERVICE_ID = "SERVICE_ID_DEFAULT";

	boolean isAdvertising = false;
	boolean isDiscovering = false;
	List<NearbyConnectionListener> listeners = new ArrayList<NearbyConnectionListener>();
	List<Integer> receivedMessageIds = new ArrayList<>();
	static class PayloadContentEcho{
		public float timeLeft;
		public PayloadContent payloadContnet;
		PayloadContentEcho(float timeLeft,  PayloadContent p){this.timeLeft=timeLeft;this.payloadContnet = p;}

	}

	List<PayloadContentEcho> echoedMessages = new ArrayList<>();

	NearbyConnectionHandler(AppCompatActivity activity, String userNickName, String serviceId){
		SERVICE_ID = serviceId;
	}

	boolean isPayloadContentDuplicatedMessage(PayloadContent content){
		if(receivedMessageIds.contains(content.id)){
			return true;
		}
		receivedMessageIds.add(content.id);
		return false;
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
	private final ConnectionLifecycleCallback mConnectionLifecycleCallback2 =
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
				userNickname + Math.random()*1000,
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

	byte[] hprToByteArr(PayloadContent content){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(content);
			out.flush();
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "hprToByteArr: Catch " + e);
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return bytes;
	}
	PayloadContent hprToPayloadContent(byte[] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		PayloadContent content = null;
		try {
			in = new ObjectInputStream(bis);
			content = (PayloadContent)in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return content;
	}



	public void sendPayload(AppCompatActivity activity, String endpoint, PayloadContent payloadContent) {
		Log.d(TAG, "sendPayload: Attempting to send pay load [" + payloadContent + "] to endpoint: " + endpoint);

		if(payloadContent.type == PayloadContent.MESSAGE_TYPE.ECHO){
			echoedMessages.add(new PayloadContentEcho(10.0f,payloadContent));
		}

		Payload payload = Payload.fromBytes(hprToByteArr(payloadContent));
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
				PayloadContent payloadContent = hprToPayloadContent(payload.asBytes());
				Log.d(TAG, "onPayloadReceived: received: " + payloadContent.content );
				if(isPayloadContentDuplicatedMessage(payloadContent)){
					//We received a duplicated message
				}else{
					//not a duplicate
					if(payloadContent.type == PayloadContent.MESSAGE_TYPE.ECHO){
						echoedMessages.add(new PayloadContentEcho(10.0f,payloadContent));
					}
					for(NearbyConnectionListener l : listeners)
						l.onPayoadMessageReceived(endpointId,payloadContent.content);
				}
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
		Nearby.getConnectionsClient(context).requestConnection(userNickname,endpoint, mConnectionLifecycleCallback2);
	}
	public void requestDisconnect(Context context, String endpoint){
		Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpoint);
	}


	public void update(float timeElapsed) {
		for(int i = echoedMessages.size()-1 ; i>=0;i--){
			int numBefore = (int)echoedMessages.get(i).timeLeft;
			float timeLeft = echoedMessages.get(i).timeLeft - timeElapsed;
			int numAfter = (int)timeLeft;
			if(numBefore != numAfter){
				for(NearbyConnectionListener l : listeners){
					l.onEchoMessage(echoedMessages.get(i).payloadContnet);
				}
			}
			if(timeLeft <0){
				echoedMessages.remove(i);
			}else{
				echoedMessages.get(i).timeLeft = timeLeft;
			}

		}

	}
}
