package com.qiantu.whereistime.domain;

import java.io.Serializable;

public class Bean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** id */
	private int id;
	/** 表情中文名 */
	private String name;
	
	/********* 普通方法方法 *********/
	public String toString() {
		return "id=" + id + "name=" + name;
	}
	
	/********* 构造方法 *********/
	public Bean() {
		super();
	}
	public Bean(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/********* get and set *********/
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
















