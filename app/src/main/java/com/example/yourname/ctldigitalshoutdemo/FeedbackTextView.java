package com.example.yourname.ctldigitalshoutdemo;

import android.widget.TextView;

public class FeedbackTextView {
	TextView textView;
	public  void init(TextView textview){
		this.textView = textview;
	}
	public  void display(String text){
		textView.setText(textView.getText() + text);
	}
	public  void clean(){
		textView.setText("");
	}





}
