package com.qiantu.whereistime.util;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DbUpdateListener;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	public static FinalDb db;
	
	/**
	 * 初始化FinalDB，必须在程序开始运行的时候执行
	 * @param context
	 * @return
	 */
	public static FinalDb init(Context context) {
		if(db == null) {
			db = FinalDb.create(context, "whereistime", true, 1, new MyDbUpdateListener());
		}
		return db;
	}
	
	/**
	 * 数据库版本更新的时候执行，结合AFinalDB使用
	 */
	private static class MyDbUpdateListener implements DbUpdateListener {
		//版本1：创建表的语句：CREATE TABLE IF NOT EXISTS person ( "id" INTEGER PRIMARY KEY AUTOINCREMENT,"password","name" )
		
		private void version2(SQLiteDatabase db) {
			//版本2：增加一个字段age
			String sql = "ALTER TABLE person add age default 55;";
			db.execSQL(sql);
		}
		
		private void version3(SQLiteDatabase db) {
			//版本3：增加字段love
			String sql = "ALTER TABLE person add age default 55;";
			db.execSQL(sql);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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




















