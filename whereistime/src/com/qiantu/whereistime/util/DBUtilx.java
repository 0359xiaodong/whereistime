package com.qiantu.whereistime.util;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;

public class DBUtilx {
	public static DbUtils db;
	
	public static DbUtils getInstance(Context context) {
		if(db != null) {
			return db;
		}
		synchronized (DBUtilx.class) {
			if(db == null) {
				db = DbUtils.create(context, "mydb", 1, new MyDbUpdateListener());
			}
			return db;
		}
	}
	
	/**
	 * 数据库版本更新的时候执行，结合AFinalDB使用
	 */
	private static class MyDbUpdateListener implements DbUpgradeListener {
		//版本1：创建表的语句：CREATE TABLE IF NOT EXISTS person ( "id" INTEGER PRIMARY KEY AUTOINCREMENT,"password","name" )
		
		private void version2(DbUtils db) {
			//版本2：增加一个字段age
//			String sql = "ALTER TABLE person add age default 55;";
//			db.execSQL(sql);
		}
		
		private void version3(DbUtils db) {
			//版本3：增加字段love
//			String sql = "ALTER TABLE person add age default 55;";
//			db.execSQL(sql);
		}
		
		@Override
		public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
			int num = newVersion - oldVersion;
			if(num > 0) {
				//执行最高版本，下面版本递减执行
				version3(db);
			}
			if(num > 1) {
				version2(db);
			}
		}
	}
}




















