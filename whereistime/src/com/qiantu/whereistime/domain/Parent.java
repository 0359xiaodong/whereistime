package com.qiantu.whereistime.domain;

import java.util.List;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Finder;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name="parent")
public class Parent {
	@Id
	private int id;
	@Column(column="name")
	private String name;
	
	@Finder(targetColumn="parentId", valueColumn="id")
	private List<Child> childLoader;
	
	
	
	public Parent(int id, String name, List<Child> childLoader) {
		super();
		this.id = id;
		this.name = name;
		this.childLoader = childLoader;
	}
	public Parent() {
		super();
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
	public List<Child> getChildLoader() {
		return childLoader;
	}
	public void setChildLoader(List<Child> childLoader) {
		this.childLoader = childLoader;
	}
}
