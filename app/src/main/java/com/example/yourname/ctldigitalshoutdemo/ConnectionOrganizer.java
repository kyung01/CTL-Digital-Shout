package com.example.yourname.ctldigitalshoutdemo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ConnectionOrganizer {
	static class ConnectionInfo{
		boolean isConnected;
		boolean isFound; //connection can be found and lost
		ConnectionInfo(){
			isConnected = false;
			isFound = false;
		}

	}
	Map<String,ConnectionInfo> connections = new Hashtable<>();

	void hprCheck(String endpoint){
		if(!connections.containsKey(endpoint)){
			connections.put(endpoint,new ConnectionInfo() );
		}
	}


	public  boolean add(String endpoint){
		if(connections.containsKey(endpoint)){
			return false;
		}
		connections.put(endpoint,new ConnectionInfo());
		return true;
	}
	public boolean remove(String endpoint){
		if(connections.containsKey(endpoint)){
			connections.remove(endpoint);
			return true;
		}
		return false;
	}

	public boolean isConnected(String endpoint){
		hprCheck(endpoint);
		return connections.get(endpoint).isConnected;
	}

	public boolean isFound(String endpoint){
		hprCheck(endpoint);
		return connections.get(endpoint).isFound;
	}

	public void setConnected(String endpoint, boolean state){
		hprCheck(endpoint);
		connections.get(endpoint).isConnected = state;
	}
	public  void setFound(String endpoint, boolean state){
		hprCheck((endpoint));
		connections.get(endpoint).isFound = state;
	}




}
