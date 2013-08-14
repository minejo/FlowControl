package com.example.flowcontrol.Service;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.flowcontrol.R;
import com.example.flowcontrol.Activity.AboutAuthorActivity;
import com.example.flowcontrol.Activity.MainActivity;
import com.example.flowcontrol.DataBase.DataBaseAdapter;
import com.example.flowcontrol.Utils.TrafficMonitoring;

/**
 * 后台服务类，主要用于在后台不断获取流量数据和数据库存储业务
 * 
 * @author Jonathan
 * 
 */
public class TrafficService extends Service {
	private DataBaseAdapter db;
	private Handler handler = new Handler();
	private long gprsR = 0, gprsS = 0, totalR = 0, totalS = 0, wifiR = 0,
			wifiS = 0;
	private long temp_gprsR = 0, temp_gprsS = 0, temp_wifiR = 0, temp_wifiS;
	private long gr = 0, gs = 0, wr = 0, ws = 0;
	private long gprsRA = 0, gprsSA = 0, wifiRA = 0, wifiSA = 0;
	private Intent in = new Intent("Runnable");
	private static final int threadNum = 12;
	private int count = 12;
	NetworkInfo nwi;
	public TrafficService() {

	}

	Runnable thread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("Thread", "线程启动" + count);
			// 初始化数据库
			db = new DataBaseAdapter(TrafficService.this);
			db.open();
			// 获取启动机器后获取的网络数据流量
			gprsR = TrafficMonitoring.mReceive();
			System.out.println("gprsR:" + gprsR);
			gprsS = TrafficMonitoring.mSend();
			totalR = TrafficMonitoring.TotalR();
			totalS = TrafficMonitoring.TotalS();
			wifiR = TrafficMonitoring.wReceive();
			System.out.println("wifiR:" + wifiR);
			wifiS = TrafficMonitoring.wSend();

			if (gprsR == -1 && gprsS == -1) {
				in.putExtra("gprsR", "No");
				in.putExtra("gprsS", "No");
			} else {
				gr = gprsR - temp_gprsR; // 瞬时值
				System.out.println("gr:" + gr);
				temp_gprsR = gprsR;
				gs = gprsS - temp_gprsS;
				temp_gprsS = gprsS;

				gr = (long) ((float) (Math.round(gr * 100.0)) / 100);
				gs = (long) ((float) (Math.round(gs * 100.0)) / 100);

				in.putExtra("gprsR", TrafficMonitoring.convertTraffic(gr));
				in.putExtra("gprsS", TrafficMonitoring.convertTraffic(gs));
			}

			if (wifiR == -1 && wifiS == -1) {
				in.putExtra("wifiR", "No");
				in.putExtra("wifiT", "No");
			} else {
				wr = (wifiR - temp_wifiR); // 得到瞬时wifi流量
				temp_wifiR = wifiR;
				ws = (wifiS - temp_wifiS); // 得到瞬时wifi流量
				temp_wifiS = wifiS;
				wr = (long) ((float) (Math.round(wr * 100.0)) / 100);// 保留两位小数
				ws = (long) ((float) (Math.round(ws * 100.0)) / 100);
				in.putExtra("wifiR", TrafficMonitoring.convertTraffic(wr));
				System.out
						.println("wr:" + TrafficMonitoring.convertTraffic(wr));
				in.putExtra("wifiS", TrafficMonitoring.convertTraffic(ws));
			}

			Date date = new Date();
			gprsRA += gr; // 一天的数据之和
			gprsSA += gs;
			wifiSA += ws;
			System.out.println("wifiRA:" + wifiRA + "date:" + date.toString());
			wifiRA += wr;

			if (count == threadNum) {
				// 如果存在当天的gprs流量记录则更新本条记录，无则插入
				Log.d("startInsert", "startInsert" + "gprsRA:" + gprsRA
						+ "grpsSA:" + gprsSA);
				if (gprsRA != 0 || gprsSA != 0) {
					Log.d("update", "update data");
					Cursor checkgprs = db.check(1, date);// type 1 为gprs
					if (checkgprs.moveToNext()) {
						long up = db.getProFlowUp(1, date);
						long dw = db.getProFlowDw(1, date);
						gprsSA += up;
						gprsRA += dw;
						db.updateData(gprsSA, gprsRA, 1, date);
						Log.d("Update", "数据更新gprsSA:" + gprsSA + " gprsRA:"
								+ gprsRA);
						gprsSA = 0;
						gprsRA = 0;
					} else {
						Log.d("insert", "insert data");
						// 插入数据
						db.insertData(gprsSA, gprsRA, 1, date);
						Log.d("INSert", "数据插入gprsSA:" + gprsSA + " gprsRA:"
								+ gprsRA);
					}
				}
				Log.d("startInsert", "startInsert" + "wifiRA:" + wifiRA
						+ "wifiSA:" + wifiSA);
				// 如果存在当天的wifi流量记录则更新，无则插入
				if (wifiRA != 0 || wifiSA != 0) {
					System.out.println("check");
					Cursor checkwifi = db.check(0, date);
					System.out.println("checkout");
					if (checkwifi.moveToNext()) {
						long up = db.getProFlowUp(0, date);
						long dw = db.getProFlowDw(0, date);
						wifiRA += dw;
						wifiSA += up;
						db.updateData(wifiSA, wifiRA, 0, date);
						Log.d("Update", "数据更新wifiSA:" + wifiSA + " wifiRA:"
								+ wifiRA);
						wifiRA = 0;
						wifiSA = 0;
					} else {
						db.insertData(wifiSA, wifiRA, 0, date);
						Log.d("INSert", "数据插入wifiSA:" + wifiSA + " wifiRA:"
								+ wifiRA);
					}
				}
				count = 1;
			}

			count++;
			db.close();
			handler.postDelayed(thread, 500); // 延迟

		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		temp_gprsR = TrafficMonitoring.mReceive();
		temp_gprsS = TrafficMonitoring.mSend();
		totalR = TrafficMonitoring.TotalR();
		totalS = TrafficMonitoring.TotalS();
		temp_wifiR = TrafficMonitoring.wReceive();
		temp_wifiS = TrafficMonitoring.wSend();

		// handler.post(thread);
		super.onCreate();
		
		Log.d("Service", "Service create");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(thread);
		Log.d("Destory", "destory");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		handler.post(thread);
		Log.d("ServiceStart", "service start");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
