package com.qiantu.whereistime.domain;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class AppInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@DatabaseField(generatedId=true)
	private int id;
	/*app名称*/
	@DatabaseField
	private String name;
	/*使用时长*/
	@DatabaseField
	private double useTime;
	/*程序包名*/
	@DatabaseField
	private String pkgName;
	/**
	 * 这个记录是属于哪一天的
	 */
	@DatabaseField(foreign=true, foreignAutoRefresh=true)
	private Day day;
	
	
	
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
	public double getUseTime() {
		return useTime;
	}
	public void setUseTime(double useTime) {
		this.useTime = useTime;
	}
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	public Day getDay() {
		return day;
	}
	public void setDay(Day day) {
		this.day = day;
	}
	
}
