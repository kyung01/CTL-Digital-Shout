/*
September 24, 2018
https://code.tutsplus.com/tutorials/google-play-services-using-the-nearby-connections-api--cms-24534

 */
package com.example.yourname.ctldigitalshoutdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yourname.ctldigitalshoutdemo.UI.FeedbackTextView;
import com.example.yourname.ctldigitalshoutdemo.UI.RecycleView.RecyclerViewHandler;
import com.example.yourname.ctldigitalshoutdemo.UI.RecycleView.RecyclerViewListener;
import com.example.yourname.ctldigitalshoutdemo.UI.UIDisplaySettings;
import com.example.yourname.ctldigitalshoutdemo.UI.UIEventRaiser;
import com.example.yourname.ctldigitalshoutdemo.UI.UIEventRaiserListener;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;

public class MainActivity extends AppCompatActivity implements  NearbyConnectionListener, RecyclerViewListener, UIEventRaiserListener{
	final String TAG = "CTLDebug";
	final String USER_NICKNAME = "USER_NICKNAME";

	//UI
	UIEventRaiser		mUIEventRaiser = new UIEventRaiser();
	UIDisplaySettings	mUIDisplaySettings = new UIDisplaySettings();
	ConnectionOrganizer mConnectionOrganizer = new ConnectionOrganizer();
	RecyclerViewHandler mRecyclerViewHandler = new RecyclerViewHandler();
	FeedbackTextView	feedback = new FeedbackTextView();

	NearbyConnectionHandler nearbyConnectionHandler = new NearbyConnectionHandler(this,"UserNickName","ServiceID");

	int hprStringToInt(String s){
		int num = 0;
		for(int i = 0 ; i < s.length();i++){
			num += (int)s.charAt(i) * Math.pow(10,i);
		}
		return num;
	}

	String hprGetInput(){
		EditText mEdit   = (EditText)findViewById(R.id.textEdit);
		return mEdit.getText().toString();
	}
	void hprCleanInpit(){
		EditText mEdit   = (EditText)findViewById(R.id.textEdit);
		mEdit.setText("");

	}

	void requestPermissions(){

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
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: Created");
		setContentView(R.layout.activity_main);
		//Link
		nearbyConnectionHandler.listeners.add(this);
		mRecyclerViewHandler.	listeners.add(this);
		mUIEventRaiser.			listeners.add(this);
		//Init
		requestPermissions();
		mUIEventRaiser		.init((Button)findViewById(R.id.bttnAdvertise),(Button)findViewById(R.id.bttnDiscover),(Button)findViewById(R.id.bttnShout));
		mUIDisplaySettings	.init((Button)findViewById(R.id.bttnAdvertise),(Button)findViewById(R.id.bttnDiscover));
		mRecyclerViewHandler.init(this);
		feedback			.init((TextView)findViewById(R.id.kTextView));
		//Settings
		feedback.clean();
		feedback.display("Feedback Test Message\n");
		((TextView)findViewById(R.id.kTextView) ) .setMovementMethod(new ScrollingMovementMethod());
		//Thread for updating
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						//60 frames per second
						Thread.sleep(17);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								update();
							}
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};

		t.start();
	}

	void update(){
		float timeElapsed = 0.017f;
		//60 frames per second based.
		//The actual time elapsed will be more complex. But accuracy is not required as we are handling only for the sake of animation
		mRecyclerViewHandler.update(timeElapsed);

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

	boolean onEndpointFound(String endpoint){
		Log.d(TAG, "onEndpointFound: " + endpoint);
		boolean isNewConnection = false;
		if(mConnectionOrganizer.add(endpoint)){
			//new connection
			mRecyclerViewHandler.add(hprStringToInt(endpoint),endpoint);
			isNewConnection = true;
		}
		mConnectionOrganizer.setFound(endpoint,true);
		mRecyclerViewHandler.setFound(hprStringToInt(endpoint) , true);
		//old connection
		return isNewConnection;
	}

	boolean onEndpointLost(String endpoint){
		Log.d(TAG, "onEndpointLost: " + endpoint);
		mConnectionOrganizer.setFound(endpoint,false);
		mRecyclerViewHandler.setFound(hprStringToInt(endpoint) , false);
		return false;
	}

	boolean onAcceptConnectionTo(String endpoint){
		if(mConnectionOrganizer.isConnected(endpoint)){
			//already connected. there is no need to do anything here
			return false;
		}
		nearbyConnectionHandler.acceptRequest(this, endpoint);
		return true;
	}




	//NearbyConnectHandlerListener
	@Override
	public void onDcvEndPointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
		onEndpointFound(endpointId);
	}

	@Override
	public void onDcvEndPointLost(String endpoint) {
		onEndpointLost(endpoint);
	}

	@Override
	public void onConnectionRequested(String endpoint, ConnectionInfo info) {
		onEndpointFound(endpoint);
		onAcceptConnectionTo(endpoint);
	}
	void onConnectionRequest_Success(String	 endpoint){
		mRecyclerViewHandler.setConnected(hprStringToInt(endpoint),true);
		mConnectionOrganizer.setConnected(endpoint,true);

	}
	void onConnectionRequest_Rejected(String	 endpoint){
		mRecyclerViewHandler.setConnected(hprStringToInt(endpoint),false);
		mConnectionOrganizer.setConnected(endpoint,false);

	}
	void onConnectionRequest_Error(String	 endpoint, ConnectionResolution result){
		Log.d(TAG, "onConnectionRequest_Error: " + result);

	}


	@Override
	public void onConnectionRequestedResult(String endpointId, ConnectionResolution result) {
        switch (result.getStatus().getStatusCode()) {
            case ConnectionsStatusCodes.STATUS_OK:
                // We're connected! Can now start sending and receiving data.
				onConnectionRequest_Success(endpointId);
                break;
            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                // The connection was rejected by one or both sides.
				onConnectionRequest_Rejected(endpointId);
                break;
            case ConnectionsStatusCodes.STATUS_ERROR:
                // The connection broke before it was able to be accepted.
				onConnectionRequest_Error(endpointId,result);
                break;
        }
	}

	@Override
	public void onDisconnected(String endpointId) {
        //mConnectionOrganizer.setConnected(endpointId, false);
		Log.d(TAG, "onDisconnected: Called");
		mConnectionOrganizer.setConnected(endpointId, false);
        mRecyclerViewHandler.setConnected(hprStringToInt(endpointId), false);
	}

	@Override
	public void onPayoadMessageReceived(String endpoint, String content) {
		feedback.display("["+ endpoint + "] sent [" + content+"]\n");
	}

	//RecylerViewListener
	@Override
	public void onRcyViewClick(int id, String endpoint) {
		feedback.display("sent to  ["+endpoint+"]: [" +hprGetInput()+"]\n");
		Log.d(TAG, "onRcyViewClick: " + endpoint);
	}

	@Override
	public void onRcyClickConnection(int id, String endpoint, boolean requestedState) {
		Log.d(TAG, "onRcyClickConnection: " + endpoint);
		if(requestedState){
			//requested to connect
			Log.d(TAG, "onRcyClickConnection: Requesting to connect");
			nearbyConnectionHandler.requestConnection(this,USER_NICKNAME,endpoint);
		}
		else{
			//requested to disconnect
			Log.d(TAG, "onRcyClickConnection: Requesting to disconnect");
			nearbyConnectionHandler.requestDisconnect(this,endpoint);
			onDisconnected(endpoint);
		}
	}

	@Override
	public void onRcyClickSend(int id, String endpoint) {
    	nearbyConnectionHandler.sendPayload(this,endpoint,hprGetInput());
		feedback.display("sent to  ["+endpoint+"]: [" +hprGetInput()+"]\n");

	}

	//UIEventListener
	@Override
	public void onAdvertisementStart() {
		nearbyConnectionHandler.startAdvertising(this,USER_NICKNAME);
		mUIDisplaySettings.setAdvertise(false);
	}

	@Override
	public void onAdvertisementStop() {
		nearbyConnectionHandler.stopAdvertising(this);
		mUIDisplaySettings.setAdvertise(true);
	}

	@Override
	public void onDiscoverStart() {
		nearbyConnectionHandler.startDiscovery(this);
		mUIDisplaySettings.setDiscover(false);
	}

	@Override
	public void onDiscoverStop() {
		nearbyConnectionHandler.stopDiscovery(this);
		mUIDisplaySettings.setDiscover(true);

	}

	@Override
	public void onShout() {
		for(String endpoint : mConnectionOrganizer.connections.keySet()){
			nearbyConnectionHandler.sendPayload(this,endpoint, hprGetInput() );
			feedback.display("sent to  ["+endpoint+"]: [" +hprGetInput()+"]\n");
		}
		hprCleanInpit();
	}
}
