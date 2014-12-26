package com.qiantu.whereistime.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppUtil {
	/**
	 * 判断用户是否是第一次打开app
	 * @param act
	 * @return
	 */
	public static boolean isFirstOpenApp(Activity act) {
		//获取当前版本号
		int versionCode = 0;
		try {
			versionCode = act.getPackageManager()
					.getPackageInfo(act.getPackageName(), 0)
					.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		String valName = "isFirstOpenApp_" + versionCode;//后面跟着版本号
		SharedPreferences sp = act.getSharedPreferences("isFirstOpenApp", Activity.MODE_PRIVATE);    
		boolean isFirstOpenApp = sp.getBoolean(valName, true);    
		if(isFirstOpenApp) {    
		    SharedPreferences.Editor editor = sp.edit();    
		    editor.putBoolean(valName, false);    
		    editor.commit();    
		        
		    return true;  
		}
		return false;
	}
	
	/**
	 * 把以秒为单位的时间转换成以分为但闻的时间字符串
	 * @param s
	 * @return
	 */
	public static String s2m(double sTime) {
		int m = (int) (sTime / 60);
		int s = (int) (sTime % 60);
		String ms = m + "分";
		String ss = s + "秒";
		if(m == 0) {
			ms = "";
		}
		if(s == 0) {
			ss = "";
		}
		return ms + ss;
	}
}
