package com.qiantu.whereistime.util;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qiantu.whereistime.domain.AppInfo;
import com.qiantu.whereistime.domain.Day;

public class DBHelper extends OrmLiteSqliteOpenHelper {
	private final static int DB_VERSION = 1;  
	private static final String DB_NAME = "whereistime";  
	
	/*一张表对应一个dao*/
	private Dao<AppInfo, Integer> appInfoDao;
	private Dao<Day, Integer> dayDao;
	
	/**构造函数，貌似是必须有的
	 * @param context
	 */
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(arg1, AppInfo.class);
			TableUtils.createTable(arg1, Day.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		try {
			TableUtils.dropTable(arg1, AppInfo.class, true);
			TableUtils.dropTable(arg1, Day.class, true);
			this.onCreate(arg0, arg1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Dao<AppInfo, Integer> getAppInfoDao() {
		if(appInfoDao == null) {
			try {
				appInfoDao = getDao(AppInfo.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return appInfoDao;
	}
	
	public Dao<Day, Integer> getDayDao() {
		if(dayDao == null) {
			try {
				dayDao = getDao(Day.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dayDao;
	}

}
