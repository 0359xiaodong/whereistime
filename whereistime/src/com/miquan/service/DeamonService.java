package com.miquan.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.miquan.util.x;

/**
 * 守护服务。
 * 将会一直运行于系统的后台，保持app的运行状态，不轻易让系统回收。
 * 
 * 原理：每隔多少秒检查一次当前运行的activity是否是当前应用，
 * 		如果是，则app正在运行，则无需操作；
 * 		如果不是，则app没有在运行，则后台运行app一下，避免被系统回收。
 * 
 * 注意：要加入权限 <uses-permission android:name="android.permission.GET_TASKS"/>
 */
public class DeamonService extends BaseService implements Runnable {
	private final String ACTION = "com.miquan.deamonService.notSelf";
	private final int DELAY = 3000;//每隔多少秒检查一次当前运行的程序
	private final int RUN = 1000;//如果不是当前程序，则运行多少秒
	
	private Context context;
	private ActivityManager am;
	private BroadcastReceiver myReceiver;
	private Intent intent;
	
	private boolean flag = true;

	@Override
	public void onCreate() {
		context = this;
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		intent = new Intent(ACTION);
		
		initBroadcast();
		startThread();
	}
	
	private void initBroadcast() {
		//定义广播接收器：每当收到广播，停止一秒
		myReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				try {
					x.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx("运行1秒中");
					Thread.sleep(RUN);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		// 注册广播接收器，当当前运行的程序不是自身，则让service运行一下
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		context.registerReceiver(myReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		closeThread();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * 开启线程
	 * 隔一段时间就判断一下当前运行的程序是不是自身，如果不是，则发送广播
	 */
	private void startThread() {
		new Thread(this).start();
	}

	/**
	 * 关闭，取消注册接收器以及停止线程
	 */
	public void closeThread() {
		// 记得取消广播注册，和关闭线程
		if(myReceiver != null) {
			context.unregisterReceiver(myReceiver);
		}
		this.flag = false;
	}

	@Override
	public void run() {
		while (flag) {
			try {
				Thread.sleep(DELAY);
				x.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx("正在检查中");

				// 获取当前运行的Activity
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				String pkgName = cn.getPackageName();

				if (!pkgName.equals(context.getPackageName())) {
					x.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx("不是当前app");
					sendBroadcast(intent);
				}
				x.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx("yes");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
















