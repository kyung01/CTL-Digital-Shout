package com.example.yourname.ctldigitalshoutdemo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ConnectionOrganizer {
	static class ConnectionInfo{
		boolean isConnected;
	}
	Map<String,ConnectionInfo> connections = new Hashtable<>();


	public  void add(String endpoint){
		connections.put(endpoint,new ConnectionInfo());

	}
	public void setConnected(String endpoint, boolean state){
		connections.get(endpoint).isConnected = state;
	}



}
