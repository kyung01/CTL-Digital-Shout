package com.example.yourname.ctldigitalshoutdemo.UI.RecycleView;


public interface RecyclerViewListener{
	void onRcyViewClick(int id, String content);
	void onRcyClickConnection(int id, String content, boolean requestedState);
	void onRcyClickSend(int id, String content);


}