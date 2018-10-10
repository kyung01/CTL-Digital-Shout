package com.example.yourname.ctldigitalshoutdemo.UI;

import android.widget.Button;

public class UIDisplaySettings {
	Button bttnAdvertise;
	Button bttnDiscover;
	public  void init(Button bttnAdvertise, Button bttnDiscover){
		this.bttnAdvertise = bttnAdvertise;
		this.bttnDiscover= bttnDiscover;
	}
	public void setAdvertise(boolean b){
		bttnAdvertise.setText((b)?"Advertise":"Stop");
	}
	public void setDiscover(boolean b){
		bttnDiscover.setText((b)? "Discover":"Stop");
	}


}
