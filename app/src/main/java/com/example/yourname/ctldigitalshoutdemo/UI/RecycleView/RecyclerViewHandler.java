package com.example.yourname.ctldigitalshoutdemo.UI.RecycleView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.yourname.ctldigitalshoutdemo.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;



public class RecyclerViewHandler implements KAdapterListener {


	class Item {
		Item(String endpoint) {
			this.content = endpoint;
			this.isConnected = false;
			this.isFound = false;
		}

		public String content;
		public boolean isConnected;
		public boolean isFound;
	}

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private Map<Integer, Item> items = new Hashtable<Integer ,Item>();
	public List<RecyclerViewListener> listeners = new ArrayList<RecyclerViewListener>();

	public void init(AppCompatActivity activity) {
		mRecyclerView = (RecyclerView) activity.findViewById(R.id.recyclerView);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);

		// use a linear layout manager
		mLayoutManager = new LinearLayoutManager(activity);
		mRecyclerView.setLayoutManager(mLayoutManager);

		add(0,"EndpointIDs");

	}
	public void update(float timeElapsed){
		
	}

	public void setConnected(int id, boolean b) {
		items.get(id).isConnected = b;
		syncItems();
	}

	public void setFound(int id, boolean b) {
		items.get(id).isFound = b;
		syncItems();
	}
	public void add(int id, String content) {
		if(!items.containsKey(id)){
			items.put(id, new Item(content));
		}
		else{
			items.get(id).content = content;
		}

		syncItems();
	}

	public void remove(int id) {
		if(items.containsKey((id))){
			items.remove(id);
		}

		syncItems();
	}

	private void syncItems() {
		Map<Integer, KAdapter.Data> tempList = new Hashtable<>();
		for (Map.Entry<Integer,Item> entry : items.entrySet()) {
			int key = entry.getKey();
			Item value = entry.getValue();
			tempList.put(entry.getKey(), new KAdapter.Data(value.content,value.isConnected));
		}

		KAdapter tempAdapter = new KAdapter(tempList);
		tempAdapter.listeners.add(this);
		mAdapter = tempAdapter;
		mRecyclerView.setAdapter(mAdapter);
	}



	//Handling events fired from Adapter
	@Override
	public void onClick(int id, String content) {
		for(RecyclerViewListener l : listeners){
			l.onRcyViewClick(id,content);
		}
	}

	@Override
	public void onClickConnect(int id, String endpoint) {

		for(RecyclerViewListener l : listeners){
			l.onRcyClickConnection(id,endpoint);
		}
	}

	@Override
	public void onClickSend(int id, String endpoint) {

		for(RecyclerViewListener l : listeners){
			l.onRcyClickSend(id,endpoint);
		}
	}



}
