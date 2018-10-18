package com.example.yourname.ctldigitalshoutdemo;

import java.io.Serializable;

public class PayloadContent implements Serializable{
	enum MESSAGE_TYPE{DEFAULT, ECHO };
	public int id;
	public MESSAGE_TYPE type;
	public String content;
	PayloadContent(MESSAGE_TYPE _type, String _content){
		id = (int)(Math.random()* Integer.MIN_VALUE);
		this.type= _type;
		this.content=_content;
	}


}
