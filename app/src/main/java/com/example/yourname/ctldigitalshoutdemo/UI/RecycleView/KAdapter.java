package com.example.yourname.ctldigitalshoutdemo.UI.RecycleView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yourname.ctldigitalshoutdemo.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

interface KAdapterListener{
	void onClick(int id, String endpoint);
	void onClickConnect(int id, String endpoint);
	void onClickSend(int id, String endpoint);
}

public class KAdapter extends RecyclerView.Adapter<KViewHolder> implements  KViewHolderListener {




	public static class Data{
		Data(String content, boolean isConneted,boolean isFound){
			this.content = content;
			this.isConnected = isConneted;
			this.isFound = isFound;
		}
		public String content;
		public  boolean isConnected,isFound;
	}


	final String TAG = "MyAdapter";
	private Map<Integer, Data> mDataset;
	public List<KAdapterListener> listeners = new ArrayList<KAdapterListener>();
	public Map<Integer, KViewHolder> holders = new Hashtable<>();



	// Provide a suitable constructor (depends on the kind of dataset)
	public KAdapter( Map<Integer,Data> myDataset) {

		mDataset = myDataset;
	}

	public void update(float timeElapsed){
		for(KViewHolder kvh: holders.values()){
			kvh.update(timeElapsed);
		}
	}

	// Create new views (invoked by the layout manager)
	@Override
	public KViewHolder onCreateViewHolder(ViewGroup parent,
										  int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.my_text_view, parent, false);
		//...
		KViewHolder vh = new KViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(KViewHolder holder, final int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		int count = 0;
		for (Map.Entry<Integer,Data> entry : mDataset.entrySet()) {
			if(count++ == position){
				//Found the correct data
				holder.init(entry.getKey(),entry.getValue().content,entry.getValue().isConnected,entry.getValue().isFound);
				break;
			}
		}
		holder.listeners.add(this);
		holders.put(holder.id, holder);
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onClick(KViewHolder holder) {
		for(KAdapterListener l : listeners)
			l.onClick(holder.id,holder.endpoint);

	}

	@Override
	public void onClickConnect(KViewHolder holder) {
		for(KAdapterListener l : listeners)
			l.onClickConnect(holder.id,holder.endpoint);

	}

	@Override
	public void onClickSend(KViewHolder holder) {
		for(KAdapterListener l : listeners)
			l.onClickSend(holder.id,holder.endpoint);

	}
}