package com.example.yourname.ctldigitalshoutdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

interface KAdapterListener{
	void onClick(int id, String endpoint);
	void onClickConnect(int id, String endpoint);
	void onClickSend(int id, String endpoint);
}

public class KAdapter extends RecyclerView.Adapter<KViewHolder> implements  KViewHolderListener {




	public static class Data{
		Data(int id, String content){
			this.id = id;
			this.content = content;
		}
		public int id;
		public String content;
	}


	final String TAG = "MyAdapter";
	private List<Data> mDataset;
	public List<KAdapterListener> listeners = new ArrayList<KAdapterListener>();
	public List<KViewHolder> holders = new ArrayList<>();



	// Provide a suitable constructor (depends on the kind of dataset)
	public KAdapter(List<Data> myDataset) {
		mDataset = myDataset;
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
		holder.init(mDataset.get(position).id,mDataset.get(position).content);
		holder.listeners.add(this);
		holders.add(holder);
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