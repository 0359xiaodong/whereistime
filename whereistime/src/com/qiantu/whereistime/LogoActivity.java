package com.qiantu.whereistime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.qiantu.whereistime.util.AppUtil;

public class LogoActivity extends ActivityBase {
	private Activity act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		
		act = this;

		// 判断是不是第一次打开
		if (AppUtil.isFirstOpenApp(this)) {
			Intent intent = new Intent();
			intent.setClass(this, ReadmeActivity.class);
			this.startActivityForResult(intent, 0);
		} else {
			startMainActivity();
		}
	}
	
	private void startMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityMain.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		startMainActivity();
		super.onActivityResult(requestCode, resultCode, data);
	}
}

















