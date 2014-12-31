package com.qiantu.whereistime.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 一天
 * @author LinZhiquan
 *
 */
@DatabaseTable
public class Day implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@DatabaseField(generatedId=true)
	private int id;
	/**
	 * 日期,格式为2014-03-27
	 */
	@DatabaseField
	private String date;
	/**
	 * 这天的所有记录，在数据库中实际上是不存在这个字段的
	 * 删除day的同时也不会删除对应的appInfos
	 */
	@ForeignCollectionField
	private ForeignCollection<AppInfo> appInfos;
	
	public Day() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public ForeignCollection<AppInfo> getAppInfos() {
		return appInfos;
	}
	public void setAppInfos(ForeignCollection<AppInfo> appInfos) {
		this.appInfos = appInfos;
	}
	
}
