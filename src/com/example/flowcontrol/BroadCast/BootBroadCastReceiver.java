package com.example.flowcontrol.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.flowcontrol.Service.TrafficService;
import com.example.flowcontrol.Utils.Parameter;

/**
 * 开机监听类，当手机开始后自动开始程序流量监听
 * @author Jonathan
 *
 */
public class BootBroadCastReceiver extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		Parameter pa = new Parameter(context);
		if ((intent.getAction().equals(ACTION))
				|| this.isStart(pa.getParameter("mCount"))) {
			Intent sayHelloIntent = new Intent(context, TrafficService.class);
			context.startService(sayHelloIntent);
		}
	}		
		public boolean isStart(String s) {
			if (Integer.parseInt(s) == 1) {
				return true;
			} else {
				return false;
			}
		}
	}

