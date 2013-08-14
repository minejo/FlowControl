package com.example.flowcontrol.Activity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.flowcontrol.R;
import com.example.flowcontrol.DataBase.DataBaseAdapter;
import com.example.flowcontrol.Utils.Parameter;
import com.example.flowcontrol.Utils.TrafficMonitoring;
/**
 * 主界面的Activity
 * @author Jonathan
 *
 */
public class MainActivity extends Activity {
	ImageButton button = null;
	TextView gprsR = null;
	TextView wifiR = null;
	TextView gprsT = null;
	TextView wifiT = null;
	TextView time = null;
	TextView avdata = null;
	TextView uprate = null;
	TextView downrate = null;
	TextView daygprsAll = null;
	TextView daywifiAll = null;
	TextView usage = null;
	ProgressBar progress = null;
	ProgressBar notifibar = null;
	
	int notification_id=19172439; 
	NotificationManager nm;  	  
    Notification notification;  

	Calendar currenCa;
	DataBaseAdapter db;

	Date date = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	String dataString = sdf.format(date);

	int maxflow;
	long remainflow;
	String usedGPRS;

	long temp_totalUp = 0;
	long temp_totalDn = 0;
	long totalUp = 0;
	long totalDn = 0;

	String limtdata;

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("thread", "refresh thread start");
			updateMessage();
			handler.postDelayed(this, 1000 * 3);
			temp_totalDn = TrafficMonitoring.TotalR();
			temp_totalUp = TrafficMonitoring.TotalS();
			//更新notification
			notification.contentView.setProgressBar(R.id.progressBar2, maxflow,maxflow - (int) remainflow, false); 
			 notification.contentView.setTextViewText(R.id.usage, usedGPRS + "/" + limtdata);
			
		    nm.notify(notification_id, notification);   
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setProgressBarVisibility(true);

		button = (ImageButton) findViewById(R.id.updatebutton);
		gprsR = (TextView) findViewById(R.id.Rgprs);
		wifiR = (TextView) findViewById(R.id.Rwifi);
		gprsT = (TextView) findViewById(R.id.Tgprs);
		wifiT = (TextView) findViewById(R.id.Twifi);
		time = (TextView) findViewById(R.id.updtime);
		avdata = (TextView) findViewById(R.id.aveflow);
		uprate = (TextView) findViewById(R.id.uprate);
		downrate = (TextView) findViewById(R.id.downrate);
		daygprsAll = (TextView) findViewById(R.id.todaygprsAll);
		daywifiAll = (TextView) findViewById(R.id.todaywifiAll);
		progress = (ProgressBar) findViewById(R.id.progressBar1);

		// 初始化数据库
		db = new DataBaseAdapter(MainActivity.this);
		db.open();
		//建立通知栏
		showNotification();

		handler.postDelayed(runnable, 1000 * 6);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 刷新textview内容
		this.updateMessage();
	}

	/**
	 * 启动联系作者的activity
	 * 
	 * @param v
	 */
	public void AboutMeOnclick(View v) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, AboutAuthorActivity.class);
		startActivity(intent);
	}

	/**
	 * 启动设置页面
	 * 
	 * @param v
	 */
	public void SettingOnclick(View v) {
		Intent intent1 = new Intent();
		intent1.setClass(MainActivity.this, MainSetActivity.class);
		startActivity(intent1);
	}

	/**
	 * 更新按钮onclick
	 */
	public void getFlowData(View v) {
		this.updateMessage();
	}

	/**
	 * 建立通知栏
	 */
	public void showNotification() {		
		
	    
		nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);  
        notification=new Notification(R.drawable.ic_launcher,"流量监控",System.currentTimeMillis());  
        notification.contentView = new RemoteViews(getPackageName(),R.layout.notification);   
        //使用notification.xml文件作VIEW  
        notification.contentView.setProgressBar(R.id.progressBar2, maxflow,maxflow - (int) remainflow, false);  
        notification.contentView.setTextViewText(R.id.usage, usedGPRS + "/" + limtdata);
      
      		
        Intent notificationIntent = new Intent(this,MainActivity.class);   
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);   
        notification.contentIntent = contentIntent; 
        nm.notify(notification_id, notification);    
	}

	/**
	 * 更新信息
	 */
	public void updateMessage() {
		// 刷新textview内容
		currenCa = Calendar.getInstance();
		int year = currenCa.get(Calendar.YEAR);
		int month = currenCa.get(Calendar.MONTH) + 1;
		int day = currenCa.get(Calendar.DATE);

		// 获取当日数据
		long day_gprs_up = db.getProFlowUp(1, date);
		long day_gprs_down = db.getProFlowDw(1, date);
		long day_wifi_up = db.getProFlowUp(0, date);
		long day_wifi_down = db.getProFlowDw(0, date);
		long day_gprs_all = day_gprs_down + day_gprs_up;
		long day_wifi_all = day_wifi_down + day_wifi_up;
		System.out.println("daygprs:" + day_gprs_down + " wifid:"
				+ day_wifi_down);
		// 获取当月数据
		long month_gprs_up = db.calculateForMonth(year, month, 1);
		System.out.println("mongprsup:" + month_gprs_up);
		long month_gprs_down = db.calculateForMonth(year, month, 1);
		long month_wifi_up = db.calculateUpForMonth(year, month, 0);
		long month_wifi_down = db.calculateDnForMonth(year, month, 0);
		String month3GTraffic = TrafficMonitoring.convertTraffic(db
				.calculateForMonth(year, month, 1));
		// 计算速率
		totalDn = TrafficMonitoring.TotalR();
		totalUp = TrafficMonitoring.TotalS();
		long mrx = totalDn - temp_totalDn;
		temp_totalDn = totalDn;
		long msx = totalUp - temp_totalUp;
		temp_totalUp = totalUp;

		// 显示流量限额
		try {
			Parameter par = new Parameter(this);
			String parLimit = par.getParameter("mLimit");
			if (parLimit.equals("")) {
				parLimit = "30";
				maxflow = Integer.valueOf(parLimit);
				limtdata = "30MB";
			} else {
				limtdata = parLimit + "MB";
				maxflow = Integer.valueOf(parLimit);
			}

			// 剩余3G流量
			Parameter isWarn = new Parameter(this);
			String warn = isWarn.getParameter("mWarn");

			String tempString[];// 临时存储3G流量
			double iLimit;
			if (parLimit.equals("")) {
				iLimit = 30.0;
			} else {
				iLimit = Double.valueOf(parLimit);
			}

			double remain;
			if (month3GTraffic.contains("KB")) {
				tempString = month3GTraffic.split("KB");
				double temp = Double.valueOf(tempString[0]);
				remain = new BigDecimal(iLimit * 1000 - temp).divide(
						new BigDecimal(1000), 2, 1).doubleValue();
			} else if (month3GTraffic.contains("MB")) {
				tempString = month3GTraffic.split("MB");
				double temp = Double.valueOf(tempString[0]);
				remain = iLimit - temp;
			} else {
				tempString = month3GTraffic.split("GB");
				double temp = Double.valueOf(tempString[0]);
				remain = new BigDecimal(iLimit * 1000 - temp).doubleValue();
			}

			double percent = new BigDecimal(remain).divide(
					new BigDecimal(iLimit), 2, 1).doubleValue();
			if (warn.equals("") || Integer.parseInt(warn) == 0) {

				avdata.setTextColor(Color.WHITE);

			} else if (Integer.parseInt(warn) == 1 && percent < 0.2) {
				avdata.setTextColor(Color.RED);
			}

			avdata.setText(remain + "MB");
			remainflow = (int) remain;
		} catch (Exception ex) {
			System.out.println(" ");
		}

		// 设置progressbar
		progress.setMax(maxflow);
		// progress.setMax(100);
		progress.setProgress(maxflow - (int) remainflow);
		// progress.setProgress(60);
		Log.d("testeetsetsetests", "max:" + String.valueOf(maxflow) + "  now: "
				+ String.valueOf(maxflow - (int) remainflow));
		progress.setVisibility(1);
  
		usedGPRS = TrafficMonitoring.convertTraffic(month_gprs_down);
		// 设置界面textview
		wifiR.setText(TrafficMonitoring.convertTraffic(month_wifi_down));
		wifiT.setText(TrafficMonitoring.convertTraffic(month_wifi_up));
		gprsR.setText(TrafficMonitoring.convertTraffic(month_gprs_down));
		gprsT.setText(TrafficMonitoring.convertTraffic(month_gprs_up));
		daygprsAll.setText(TrafficMonitoring.convertTraffic(day_gprs_all));
		daywifiAll.setText(TrafficMonitoring.convertTraffic(day_wifi_all));
		time.setText(dataString);
		uprate.setText("上传:" + TrafficMonitoring.convertTraffic(msx));
		downrate.setText("下载:" + TrafficMonitoring.convertTraffic(mrx));
		
		
		
	}
}
