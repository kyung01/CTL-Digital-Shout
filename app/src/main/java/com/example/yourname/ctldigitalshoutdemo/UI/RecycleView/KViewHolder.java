package com.example.yourname.ctldigitalshoutdemo.UI.RecycleView;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yourname.ctldigitalshoutdemo.R;

import java.util.ArrayList;
import java.util.List;

interface KViewHolderListener {
	void onClick(KViewHolder holder);
	void onClickConnect(KViewHolder holder);
	void onClickSend(KViewHolder holder);

}

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class KViewHolder extends RecyclerView.ViewHolder {
	// each data item is just a string in this case
	public View view;
	public TextView mTextView;
	public Button bttnConnect,bttnSend;
	public  int id;
	public  boolean isConnected,isFound;
	public List<KViewHolderListener> listeners = new ArrayList<>();


	public String endpoint;

	//animation
	float rateColorTransitionIsFound = 0;


	public KViewHolder(View v) {
		super(v);
		this.view = v;
		this.mTextView = v.findViewById(R.id.textView);
		this.bttnConnect = (Button)v.findViewById(R.id.bttnConnection);
		this.bttnSend = (Button)v.findViewById(R.id.bttnSend);

	}
	public void init( int id, String endpoint, boolean isConnected,boolean isFound) {
		this.id = id;
		this.endpoint = endpoint;
		setConnected(isConnected);
		setFound(isFound);


		mTextView.setText(endpoint);
		final KViewHolder thisHolder = this;

		mTextView.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for(KViewHolderListener l : listeners){
							l.onClick(thisHolder);
						}
					}
				}
		);
		bttnConnect.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for(KViewHolderListener l : listeners){
							l.onClickConnect(thisHolder);
						}
					}
				}
		);
		bttnSend.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for(KViewHolderListener l : listeners){
							l.onClickSend(thisHolder);
						}
					}
				}
		);
	}
	float hprLerp(float a, float b, float f)
	{
		return a + f * (b - a);
	}
	public void update(float timeElapsed){
		if(!isFound){
			//display not found animation
			float ratio = (float)Math.cos(rateColorTransitionIsFound);
			ratio = (1 + ratio) * 0.5f;
			ratio = 1- ratio*ratio;
			rateColorTransitionIsFound += timeElapsed*10;
			if(isConnected){
				//Connected but not found. Display flashing orange
				view.setBackgroundColor( (Color.rgb(
						(int)hprLerp(255,0,ratio),
						(int)hprLerp(165,0,ratio),
						(int)hprLerp(0,255,ratio)) ) );

			}else{
				//Not connected and not found. Display flashing red
				view.setBackgroundColor( (Color.rgb(
						(int)hprLerp(100,255,ratio),
						(int)hprLerp(0,0,ratio),
						(int)hprLerp(0,0,ratio))) );
			}
		}

	}

	public  void setConnected(boolean value){
		this.isConnected = value;
		view.setBackgroundColor( (isConnected)? Color.rgb(0,0,255):Color.rgb(255,0,0));
	}
	public void setFound(boolean b){
		this.isFound = b;
		rateColorTransitionIsFound = 0;

	}



}