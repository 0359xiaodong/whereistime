package com.qiantu.whereistime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.j256.ormlite.dao.Dao;
import com.qiantu.whereistime.domain.AppInfo;
import com.qiantu.whereistime.domain.Day;
import com.qiantu.whereistime.util.DBHelper;
import com.qiantu.whereistime.util.GlobleVar;
import com.qiantu.whereistime.util.RunAlways;

public class BackService extends OrmLiteBaseService<DBHelper> {
	private Dao<AppInfo, Integer> appInfoDao;
	private Dao<Day, Integer> dayDao;
	private Map<String, String> appInfos;//所有已经安装的程序的信息<包名，app名>
	private boolean flag_colseLogTimeRunnable = true;
	
	/* 一系列的receiver */
	private MyReceiver stopSelfReceiver;//用于关闭自身
	private MyReceiver stopThreadReceiver;//用于锁屏的时候关闭线程
	private MyReceiver startThreadReceiver;//用于当屏幕从锁屏状态到打开状态是，启动线程
	
	private RunAlways runAlways;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		this.registerReceivers();
		
		//开启线程，在后台记录各个程序的使用时间
		new Thread(new LogTimeRunnable()).start();
		
		//只要在service中new一个对象，然后start方法
		runAlways = new RunAlways(this);
		runAlways.start();
	}
	
	/**
	 * 注册一系列的广播接收器
	 */
	public void registerReceivers() {
		String action = null;
		IntentFilter filter = null;
		
		//注册广播，用于关闭自身
		action = GlobleVar.SYSTEM_EXIT;
		filter = new IntentFilter();
		filter.addAction(action);
		stopSelfReceiver = new MyReceiver(action);
		this.registerReceiver(stopSelfReceiver, filter);
		
		//注册广播，锁屏的时候关闭线程
		action = Intent.ACTION_SCREEN_OFF;
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		stopThreadReceiver = new MyReceiver(action);
		this.registerReceiver(stopThreadReceiver, filter);
		
		//注册广播，当屏幕从锁屏状态到打开状态是，启动线程
		action = Intent.ACTION_SCREEN_ON;
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		startThreadReceiver = new MyReceiver(action);
		this.registerReceiver(startThreadReceiver, filter);
	}
	
	/**
	 * 注销一系列的广播接收
	 */
	public void unRegisterReceivers() {
		this.unregisterReceiver(stopSelfReceiver);
		this.unregisterReceiver(stopThreadReceiver);
		this.unregisterReceiver(startThreadReceiver);
	}

	@Override
	public void onDestroy() {
		flag_colseLogTimeRunnable = false;//关闭线程
		//最后在onDestory中close就行
		runAlways.close();
		this.unRegisterReceivers();//注销广播接收
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	class LogTimeRunnable implements Runnable {

		@Override
		public void run() {
			appInfoDao = getHelper().getAppInfoDao();
			dayDao = getHelper().getDayDao();
			appInfos = getInstalledAppInfos();
			
			while(flag_colseLogTimeRunnable) {
				try {
					//3秒插一次数据库
					Thread.sleep(3000);
					if(!flag_colseLogTimeRunnable) return;//防止在这3秒内已经关闭了service
					
					//获取当前运行的Activity
					ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
					ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
					String pkgName = cn.getPackageName();
					
					String pkgName_self = getPackageName();
					//如果是程序本身，则忽略
					if(pkgName.equals(pkgName_self)) {
						continue;
					}
					
					String top_app_name = appInfos.get(pkgName);
					
					//更新数据库
					try {
						//获取现在的日期
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
						String dateNow = format.format(new Date());
						List<Day> list_day = dayDao.queryForEq("date", dateNow);
						
						Day d = null;
						//新的一天
						if(list_day.size() == 0) {
							d = new Day();
							d.setDate(dateNow);
							dayDao.create(d);
						} else {
							d = list_day.get(0);
						}
						
//						List<AppInfo> list_appInfo = appInfoDao.queryForEq("name", top_app_name);
						//查询 同一天是否打开过同一个应用
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", top_app_name);
						map.put("day_id", d.getId());
						List<AppInfo> list_appInfo = appInfoDao.queryForFieldValues(map);
						
						//第一次运行的应用
						if(list_appInfo.size() == 0) {
							AppInfo appInfo = new AppInfo();
							appInfo.setName(top_app_name);
							appInfo.setUseTime(3);//3s
							appInfo.setPkgName(pkgName);
							appInfo.setDay(d);
							appInfoDao.create(appInfo);
							
						//已经运行过的应用，数据库中已经有记录
						} else if(list_appInfo.size() == 1) {
							AppInfo appInfo = list_appInfo.get(0);
							appInfo.setUseTime(appInfo.getUseTime() + 3);
							appInfoDao.update(appInfo);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					//发送广播更新UI
//					Intent intent = new Intent();
//					intent.setAction(GlobleVar.UPDATE_UI);
//					sendBroadcast(intent);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 获取所有已经安装的app的名字，比如微信，QQ
	 * @return Map<pkgName, appName>
	 */
	public Map<String, String> getInstalledAppInfos() {
		Map<String, String> appNames = new HashMap<String, String>();
		
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);
		for(ApplicationInfo appInfo : applicationInfos) {
			String pkgName = appInfo.packageName;
			String appName = pm.getApplicationLabel(appInfo).toString();
			appNames.put(pkgName, appName);
		}
		return appNames;
	}

	/**
	 * 这个service用到的所有广播
	 * @author LinZhiquan
	 */
	class MyReceiver extends BroadcastReceiver {
		private String action;
		public MyReceiver(String action) {
			super();
			this.action = action;
		}
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(action.equals(GlobleVar.SYSTEM_EXIT)) {
				//注册广播，用于关闭自身
				stopSelf();
				
			} else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
				//注册广播，锁屏的时候关闭线程
				flag_colseLogTimeRunnable = false;
				
			} else if(action.equals(Intent.ACTION_SCREEN_ON)) {
				//注册广播，当屏幕从锁屏状态到打开状态是，启动线程
				flag_colseLogTimeRunnable = true;
				new Thread(new LogTimeRunnable()).start();
				
			}
		}
	}
}















