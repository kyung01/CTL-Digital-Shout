package com.example.yourname.ctldigitalshoutdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

interface MyAdapterListener{
	void onClick(int id, String content);
}

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
	interface MyViewHolderListener{
		void onClick(MyViewHolder holder);
		void onClickConnection(MyViewHolder holder);
		void onClickSend(MyViewHolder holder);

	}

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public TextView mTextView;
		public Button bttnConnect,bttnSend;
		public List<MyViewHolderListener> listeners = new ArrayList<>();


		public int id;
		public String endpoint;


		public MyViewHolder(View v) {
			super(v);
			this.mTextView = v.findViewById(R.id.textView);
			this.bttnConnect = (Button)v.findViewById(R.id.bttnConnection);
			this.bttnSend = (Button)v.findViewById(R.id.bttnSend);
		}
		public void init(int id, String endpoint) {
			this.id= id;
			this.endpoint = endpoint;
			mTextView.setText(endpoint);
			final MyViewHolder thisHolder = this;

			mTextView.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for(MyViewHolderListener l : listeners){
								l.onClick(thisHolder);
							}
						}
					}
			);
			bttnConnect.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for(MyViewHolderListener l : listeners){
								l.onClickConnection(thisHolder);
							}
						}
					}
			);
			bttnSend.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for(MyViewHolderListener l : listeners){
								l.onClickSend(thisHolder);
							}
						}
					}
			);
		}

	}
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
	public List<MyAdapterListener> listeners = new ArrayList<MyAdapterListener>();



	// Provide a suitable constructor (depends on the kind of dataset)
	public MyAdapter(List<Data> myDataset) {
		mDataset = myDataset;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
													 int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.my_text_view, parent, false);
		//...
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		holder.init(mDataset.get(position).id,mDataset.get(position).content);


	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataset.size();
	}


}