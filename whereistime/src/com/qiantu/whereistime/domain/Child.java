package com.qiantu.whereistime.domain;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="child")
public class Child {
	@Id
	private int id;
	@Column(column="name")
	private String name;
	
	@Foreign(column="parentId", foreign="id")
	private Parent parent;
	
	
	
	public Child() {
		super();
	}
	public Child(int id, String name, Parent parent) {
		super();
		this.id = id;
		this.name = name;
		this.parent = parent;
	}
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
	public Parent getParent() {
		return parent;
	}
	public void setParent(Parent parent) {
		this.parent = parent;
	}
}
