package com.qiantu.whereistime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.qiantu.whereistime.domain.AppInfo;
import com.qiantu.whereistime.domain.Day;
import com.qiantu.whereistime.util.AppUtil;
import com.qiantu.whereistime.util.GlobleVar;
import com.qiantu.whereistime.util.StringUtil;

/**
 * @author LinZhiquan
 *
 */
public class MainActivity extends SuperActivity {
	private Dao<AppInfo, Integer> appInfoDao;
	private Dao<Day, Integer> dayDao;
	
	/* 一系列的广播接收器 */
	private MyReceiver updateUIReceiver;//注册广播接收更新UI的命令;
	private MyReceiver clearDatabaseReceiver;//注册广播删除数据库数据
	
	private List<TextView> list_text;
	private TextView text_date;
	
	/** 用来存放要切换显示的view */
	private ViewFlipper viewFlipper;
	/** 用于获取layout文件 */
	private LayoutInflater layoutInflater;
	/** viewFlipper中存放的view的数量 */
	private int view_num = 0;
	/** 记录当前是第几个view */
	private int view_index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);
		
		//分享
//		ShareSDK.initSDK(this);
		
		//初始化组件
		this.initComponents();
		
		//设置titlebar的一些监听器
		super.setTitleBar();
		
		//注册一系列的广播接收器
		this.registerReceivers();
		
		//初始化数据库
		appInfoDao = this.getHelper().getAppInfoDao();
		dayDao = this.getHelper().getDayDao();
		
		//绑定监听器，监听滑动事件
		viewFlipper.setOnTouchListener(new GestureListener(this) {
			@Override
			public boolean left() {
				if(view_index == view_num-1) {
					Toast.makeText(MainActivity.this, "这是第一天", Toast.LENGTH_SHORT).show();
					return false;//如果当前的view是最后一个
				}
				toLeft();
				view_index++;
				return super.left();
			}

			@Override
			public boolean right() {
				if(view_index == 0) {
					Toast.makeText(MainActivity.this, "这是最后一天", Toast.LENGTH_SHORT).show();
					return false;//如果当前的view是第一个
				}
				toRight();
				view_index--;
				return super.right();
			}
		});
		
		//启动service
		Intent intent = new Intent(this, BackService.class);
		this.startService(intent);
	}
	
	/**
	 * 注册一系列的广播接收器
	 */
	public void registerReceivers() {
		IntentFilter filter = null;
		String action = null;
		
		//注册广播接收更新UI的命令
		action = GlobleVar.UPDATE_UI;
		filter = new IntentFilter();
		filter.addAction(action);
		updateUIReceiver = new MyReceiver(action);
		this.registerReceiver(updateUIReceiver, filter);
		
		//注册广播接收清除数据库信息的命令
		action = GlobleVar.CLEAR_DATABASE_INFO;
		filter = new IntentFilter();
		filter.addAction(action);
		clearDatabaseReceiver = new MyReceiver(action);
		this.registerReceiver(clearDatabaseReceiver, filter);
	}

	public void unRegisterReceivers() {
		this.unregisterReceiver(updateUIReceiver);
		this.unregisterReceiver(clearDatabaseReceiver);
	}
	
	@Override
	protected void onDestroy() {
//		ShareSDK.stopSDK(this);
		this.unRegisterReceivers();
		super.onDestroy();
	}

	/**
	 * 初始化组件
	 */
	public void initComponents() {
		layoutInflater = LayoutInflater.from(this); 
		viewFlipper = (ViewFlipper) this.findViewById(R.id.viewFilpper);
	}
	
	@Override
	protected void onResume() {
		this.updateUI();
		super.onResume();
	}

	public void updateUI() {
		List<Day> days = this.getDays(10);
		
		//当数据库没有数据的时候
		if(days.isEmpty()) {
			LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.view, null);
			viewFlipper.addView(view, view_num);
			view_num++;
			Toast.makeText(this, "亲~开始浏览其它应用吧，这里会记录着~", Toast.LENGTH_LONG).show();
			return;
		}
		
		//第一天是今天，第二天是昨天
		Day d = days.get(0);
		d.setDate(d.getDate() + "(今天)");
		days.remove(0);
		days.add(0, d);
		if(days.size() > 1) {
			Day d2 = days.get(1);
			d2.setDate(d2.getDate() + "(昨天)");
			days.remove(1);
			days.add(1, d2);
		}
		
		for(Day day : days) {
			//设定view
			LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.view, null);
			//a代表大格，b代表中格，c代表小格，d代表最小
			TextView text_1_a = (TextView) view.findViewById(R.id.text_1_a);
			TextView text_2_b = (TextView) view.findViewById(R.id.text_2_b);
			TextView text_3_b = (TextView) view.findViewById(R.id.text_3_b);
			TextView text_4_c = (TextView) view.findViewById(R.id.text_4_c);
			TextView text_5_c = (TextView) view.findViewById(R.id.text_5_c);
			TextView text_6_c = (TextView) view.findViewById(R.id.text_6_c);
			TextView text_7_c = (TextView) view.findViewById(R.id.text_7_c);
			TextView text_8_d = (TextView) view.findViewById(R.id.text_8_d);
			TextView text_9_d = (TextView) view.findViewById(R.id.text_9_d);
			TextView text_10_d = (TextView) view.findViewById(R.id.text_10_d);
			TextView text_11_d = (TextView) view.findViewById(R.id.text_11_d);
			TextView text_12_d = (TextView) view.findViewById(R.id.text_12_d);
			TextView text_13_d = (TextView) view.findViewById(R.id.text_13_d);
			TextView text_14_d = (TextView) view.findViewById(R.id.text_14_d);
			TextView text_15_d = (TextView) view.findViewById(R.id.text_15_d);
			list_text = new ArrayList<TextView>();
			list_text.add(text_1_a);
			list_text.add(text_2_b);
			list_text.add(text_3_b);
			list_text.add(text_4_c);
			list_text.add(text_5_c);
			list_text.add(text_6_c);
			list_text.add(text_7_c);
			list_text.add(text_8_d);
			list_text.add(text_9_d);
			list_text.add(text_10_d);
			list_text.add(text_11_d);
			list_text.add(text_12_d);
			list_text.add(text_13_d);
			list_text.add(text_14_d);
			list_text.add(text_15_d);
			
			text_date = (TextView) view.findViewById(R.id.text_date);
			
			//数据从数据库中取得，根据usertime排列
			List<AppInfo> list_app = this.getAppInfos(day.getId(), 15);
			
			//设置日期
			text_date.setText(day.getDate());
			
			//总的使用时间
			double sum = 0;
			for(AppInfo app : list_app) {
				sum += app.getUseTime();
			}
			final double sumTime = sum;//用于传递到下一个activity
			
			//把所有text清楚
			for(TextView text : list_text) {
				text.setText("");
			}
			
			int index = 0;
			for(TextView text : list_text) {
				if(list_app.size() == index) break;
				final AppInfo app = list_app.get(index);
				
				String str = "";
				if(index < 7) {
					str = app.getName()+"\n"+AppUtil.s2m(app.getUseTime())
							+StringUtil.subDouble(app.getUseTime()/sum*100)+"%";
				} else {
					str = app.getName()+"\n"
							+StringUtil.subDouble(app.getUseTime()/sum*100)+"%";
				}
				
				text.setText(str);
				text.setLongClickable(true);//这个是必须的
				text.setOnTouchListener(new GestureListener(this) {
					@Override
					public boolean left() {
						toLeft();
						return super.left();
					}
					@Override
					public boolean right() {
						toRight();
						return super.right();
					}
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_UP){  
							view.setBackgroundResource(R.color.box_text_up_color);
				        }   
				        if(event.getAction() == MotionEvent.ACTION_DOWN){  
				            view.setBackgroundResource(R.color.box_text_down_color);
				        }  
						return super.onTouch(view, event);
					}
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, AppInfoActivity.class);
						intent.putExtra("app", app);
						intent.putExtra("sumTime", sumTime);
						startActivity(intent);
						return super.onSingleTapUp(e);
					}
				});
				index++;
			}
			
			viewFlipper.addView(view, view_num);
			view_num++;
		}
		
	}
	
	/** 监听用户点击TextView
	 * @author LinZhiquan
	 */
	class AppInfoListener implements OnClickListener {
		private AppInfo app;
		public AppInfoListener(AppInfo app) {
			super();
			this.app = app;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, AppInfoActivity.class);
			intent.putExtra("app", app);
			startActivity(intent);
		}
	}

	/**
	 * 从数据库中读取AppInfo表中的所有记录
	 * @param dayId
	 * @param n
	 * @return
	 */
	public List<AppInfo> getAppInfos(int dayId, long n) {
		List<AppInfo> appInfos = null;
		try {
			QueryBuilder<AppInfo, Integer> qb = appInfoDao.queryBuilder();
			qb.orderBy("useTime", false);
			qb.setWhere(qb.where().eq("day_id", dayId));
			qb.limit(n);
			PreparedQuery<AppInfo> pq = qb.prepare();
			appInfos = appInfoDao.query(pq);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appInfos;
	}
	
	/**
	 * 从数据库中读取前n天
	 * @param n
	 * @return
	 */
	public List<Day> getDays(long n) {
		List<Day> days = null;
		try {
			QueryBuilder<Day, Integer> qb = dayDao.queryBuilder();
			qb.orderBy("id", false);
			qb.limit(n);
			PreparedQuery<Day> pq = qb.prepare();
			days = dayDao.query(pq);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return days;
	}
	
	/**
	 * 清楚数据库已经保存的信息
	 */
	public void clearDatabaseInfo() {
		try {
			appInfoDao.delete(appInfoDao.queryForAll());
			this.updateUI();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	class MyReceiver extends BroadcastReceiver {
		private String action;
		public MyReceiver(String action) {
			super();
			this.action = action;
		}
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(action.equals(GlobleVar.UPDATE_UI)) {
				//注册广播接收更新UI的命令
				updateUI();
			} else if(action.equals(GlobleVar.CLEAR_DATABASE_INFO)) {
				//注册广播接收清除数据库信息的命令
				clearDatabaseInfo();
			}
		}
	}
	
	/**
	 * 向左滑动屏幕
	 * @return
	 */
	public boolean toLeft() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, 
                R.anim.in_from_right)); 
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, 
                R.anim.out_to_left));
		viewFlipper.showPrevious();
		return false;
	}

	/**
	 * 向右滑动屏幕
	 * @return
	 */
	public boolean toRight() {
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, 
                R.anim.in_from_left)); 
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, 
                R.anim.out_to_right));
		viewFlipper.showNext();
		return false;
	}
}















