package com.qiantu.whereistime;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ReadmeActivity extends Activity {
	/** 用来存放要切换显示的view */
	private ViewFlipper viewFlipper;
	/** 用于获取layout文件 */
	private LayoutInflater layoutInflater;
	
	/** 记录当前是第几个view */
	private int index = 0;
	/** 一共有几个view */
	private int num = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_readme);

		layoutInflater = LayoutInflater.from(this);
		viewFlipper = (ViewFlipper) this
				.findViewById(R.id.viewFilpper_readme);

		// 绑定监听器，监听滑动事件
		viewFlipper.setOnTouchListener(new GestureListener(this) {
			@Override
			public boolean left() {
				if(index == num-1) return false;//如果当前的view是最后一个
				toLeft();
				index++;
				return super.left();
			}

			@Override
			public boolean right() {
				if(index == 0) return false;//如果当前的view是第一个
				toRight();
				index--;
				return super.right();
			}
		});

		View view = layoutInflater.inflate(R.layout.view_readme, null);
		TextView text_readme = (TextView) view.findViewById(R.id.text_readme);
		String str = "亲~\n是不是无聊的时候拿出手机，\n打开屏幕\n向左一滑\n向右一滑\n锁屏~\n时间就没了";
		text_readme.setText(str);
		viewFlipper.addView(view);
		num++;

		View view2 = layoutInflater.inflate(R.layout.view_readme, null);
		TextView text_readme2 = (TextView) view2.findViewById(R.id.text_readme);
		String str2 = "亲~\n知道你的时间去哪了吗\n这里帮你记录着\n时间到底花在了什么地方~";
		text_readme2.setText(str2);
		viewFlipper.addView(view2);
		num++;
		
		Button button = (Button) this.findViewById(R.id.button_start);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sp = getSharedPreferences("isReadme", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("isReadme", true);
				editor.commit();
				
				finish();
			}
		});
	}
	
	/**
	 * 向左滑动屏幕
	 * @return
	 */
	public boolean toLeft() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, 
                R.anim.in_from_right)); 
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, 
                R.anim.out_to_left));
		viewFlipper.showPrevious();
		return false;
	}

	/**
	 * 向右滑动屏幕
	 * @return
	 */
	public boolean toRight() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, 
                R.anim.in_from_left)); 
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, 
                R.anim.out_to_right));
		viewFlipper.showNext();
		return false;
	}
}
