package com.example.yourname.ctldigitalshoutdemo;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewHandler {
	class Item{
		Item(int id, String content){
			this.id = id;
			this.content = content;
		}

		public int id;
		public String content;
	}

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<Item> items = new ArrayList<Item>();

	public void init(AppCompatActivity activity, String[] myDataset){
		mRecyclerView = (RecyclerView) activity.findViewById(R.id.recyclerView);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);

		// use a linear layout manager
		mLayoutManager = new LinearLayoutManager(activity);
		mRecyclerView.setLayoutManager(mLayoutManager);

		// specify an adapter (see also next example)
		mAdapter = new MyAdapter(myDataset);
		mRecyclerView.setAdapter(mAdapter);

	}

	public void display(int id, String content){
		for(int  i = 0; i < items.size();i++){
			if(id == items.get(i).id) {
				//the item already exist in the list
				items.get(i).content = content;
				return;
			}
		}
		//Item is not a duplicate, you can add.
		items.add(new Item(id,content));
		List<String> tempList = new ArrayList<String>();
		for(int i = 0 ; i < items.size();i++){
			tempList.add(items.get(i).content);
		}

		mAdapter = new MyAdapter(tempList.toArray(new String[0]));
		mRecyclerView.setAdapter(mAdapter);
	}
	public void remove(int id){
		for(int  i = 0; i < items.size();i++){
			if(id == items.get(i).id) {
				items.remove(i);
				return;
			}
		}
	}


}
