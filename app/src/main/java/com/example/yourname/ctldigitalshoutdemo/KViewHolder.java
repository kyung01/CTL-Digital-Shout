package com.example.yourname.ctldigitalshoutdemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

interface KViewHolderListener {
	void onClick(KViewHolder holder);
	void onClickConnect(KViewHolder holder);
	void onClickSend(KViewHolder holder);

}

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class KViewHolder extends RecyclerView.ViewHolder {
	// each data item is just a string in this case
	public TextView mTextView;
	public Button bttnConnect,bttnSend;
	public List<KViewHolderListener> listeners = new ArrayList<>();


	public int id;
	public String endpoint;


	public KViewHolder(View v) {
		super(v);
		this.mTextView = v.findViewById(R.id.textView);
		this.bttnConnect = (Button)v.findViewById(R.id.bttnConnection);
		this.bttnSend = (Button)v.findViewById(R.id.bttnSend);
	}
	public void init(int id, String endpoint) {
		this.id = id;
		this.endpoint = endpoint;
		mTextView.setText(endpoint);
		final KViewHolder thisHolder = this;

		mTextView.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for(KViewHolderListener l : listeners){
							l.onClick(thisHolder);
						}
					}
				}
		);
		bttnConnect.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for(KViewHolderListener l : listeners){
							l.onClickConnect(thisHolder);
						}
					}
				}
		);
		bttnSend.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for(KViewHolderListener l : listeners){
							l.onClickSend(thisHolder);
						}
					}
				}
		);
	}

}