package com.example.yourname.ctldigitalshoutdemo.UI;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class UIEventRaiser {
	public List<UIEventRaiserListener> listeners = new ArrayList<>();
	Button bttnAdvertise;
	Button bttnDiscover;
	Button bttnShout;
	boolean isAdvertising = false;
	boolean isDiscovering = false;

	public void init(Button bttnAdvertise,Button bttnDiscover, Button bttnShout){
		this.bttnAdvertise	= bttnAdvertise;
		this.bttnDiscover	= bttnDiscover;
		this.bttnShout		= bttnShout;
		bttnAdvertise.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBttnAdvertise(v);
			}
		});
		bttnDiscover.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBttnDiscover(v);
			}
		});
		bttnShout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBttnShout(v);
			}
		});
	}


	void onClickBttnAdvertise(View view){
		isAdvertising = !isAdvertising;
		if(isAdvertising){
			for(UIEventRaiserListener l : listeners)
				l.onAdvertisementStart();
		}else{
			for(UIEventRaiserListener l : listeners)
				l.onAdvertisementStop();
		}
	}
	void onClickBttnDiscover(View view){
		isDiscovering = !isDiscovering;
		if(isDiscovering){
			for(UIEventRaiserListener l : listeners)
				l.onDiscoverStart();
		}else{
			for(UIEventRaiserListener l : listeners)
				l.onDiscoverStop();
		}
	}
	void onClickBttnShout(View view){

		for(UIEventRaiserListener l : listeners)
			l.onShout();

	}


}
