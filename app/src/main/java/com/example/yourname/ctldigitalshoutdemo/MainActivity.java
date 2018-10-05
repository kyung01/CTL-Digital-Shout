/*
September 24, 2018
https://code.tutsplus.com/tutorials/google-play-services-using-the-nearby-connections-api--cms-24534

 */
package com.example.yourname.ctldigitalshoutdemo;

import android.Manifest;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements  NearbyConnectionListener, RecyclerViewListener {
	String TAG = "CTLDebug";
	NotificationManager notificationManager;

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	NearbyConnectionHandler nearbyConnectionHandler = new NearbyConnectionHandler(this,"UserNickName","ServiceID");
	RecyclerViewHandler recyclerViewHandler = new RecyclerViewHandler();
	FeedbackTextView feedback = new FeedbackTextView();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nearbyConnectionHandler.listeners.add(this);
		recyclerViewHandler.listeners.add(this);

		NearbyConnectionHandler.context = this;
		feedback.init((TextView)findViewById(R.id.kTextView));
		feedback.clean();
		feedback.display("Feedback Test Message\n");

		Log.d(TAG, "onCreate: Created");
		String[] myDataset = new String[]{"APPLE", "BANANA","CAR","DOG","CUP","HEAD"};
		notificationManager =  (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		requestPermissions();
		recyclerViewHandler.init(this);
		recyclerViewHandler.display(123456,"example_content");
		((TextView)findViewById(R.id.kTextView) ) .setMovementMethod(new ScrollingMovementMethod());

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

	@Override
	public void onEndpointAdded(String endpoint) {
		recyclerViewHandler.display(hprStringToInt(endpoint),endpoint);
	}

	@Override
	public void onEndpointRemoved(String endpoint) {
		recyclerViewHandler.remove(hprStringToInt(endpoint));
	}

	@Override
	public void onPayoadMessageReceived(String endpoint, String content) {
		feedback.display("["+ endpoint + "] sent [" + content+"]\n");
	}

	@Override
	public void onViewClick(int id, String endpoint) {
		nearbyConnectionHandler.sendPayload(endpoint, hprGetInput());
		feedback.display("sent to  ["+endpoint+"]: [" +hprGetInput()+"]\n");
	}

	public void onClickBttnAdvertise(View view) {
		Log.d(TAG, "onClickBttnAdvertise: ");
		nearbyConnectionHandler.startAdvertising(this);
	}

	public void onClickBttnDiscover(View view) {
		Log.d(TAG, "onClickBttnDiscover: ");
		nearbyConnectionHandler.startDiscovery(this);
	}

	public void onClickBttnTransmit(View view) {
		Log.d(TAG, "onClickBttnTransmit: requesting to send a payload to NearbyConnectionHandler");
		//Send message to everyone that's on the list
		nearbyConnectionHandler.sendPayloadToAll(hprGetInput());
		feedback.display("sent to [ALL]: ["+hprGetInput()+"]\n");
	}

	String hprGetInput(){
		EditText mEdit   = (EditText)findViewById(R.id.textEdit);
		return mEdit.getText().toString();
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



	int hprStringToInt(String s){
    	int num = 0;
    	for(int i = 0 ; i < s.length();i++){
    		num += (int)s.charAt(i) * Math.pow(10,i);
		}
		return num;
	}

}
