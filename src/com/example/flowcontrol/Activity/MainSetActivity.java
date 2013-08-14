package com.example.flowcontrol.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.flowcontrol.R;
import com.example.flowcontrol.DataBase.DataBaseAdapter;
import com.example.flowcontrol.Utils.*;

/**
 * 主设置页面的Activity
 * @author Jonathan
 *
 */
public class MainSetActivity extends Activity {
	//申明GestureDetector对象
	GestureDetector mGestureDetector;
	
	private View View_quota;
	private View View_left;
	private View View_clear;
	private View View_date;
	private EditText dt;
	private EditText dt1;
	private EditText dt2;
	private DataBaseAdapter dbAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		setTitle("系统设置");
		dbAdapter = new DataBaseAdapter(this);
        dbAdapter.open();        
      

        TableLayout viewSnsLayout = (TableLayout)findViewById(R.id.test);  
	   
	    viewSnsLayout.setLongClickable(true);
	}
	@Override
	public void onResume() {
		super.onResume();
		setContentView(R.layout.setup);
		ToggleButton isCount;
		ToggleButton isWarn;

		isCount = (ToggleButton) findViewById(R.id.ToggleButton1);
		isWarn = (ToggleButton) findViewById(R.id.ToggleButton2);
		Parameter par1 = new Parameter(this);
		String mCount = par1.getParameter("mCount");
		final TextView is_statist = (TextView)findViewById(R.id.is_statist);
		
		//初始化is_statist的值
		if(mCount.equals("1"))
		{
			is_statist.setText("流量统计功能已开启");
		}
		else
		{
			is_statist.setText("流量统计功能已关闭");
		}
		
		// 设置流量统计功能是否启
		isCount.setChecked(this.BoxState(mCount));
		isCount
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Contact contact;
						// TODO Auto-generated method stub
						if (isChecked) {
							contact = new Contact(MainSetActivity.this, "mCount");
							contact.saveParameter("1");
							is_statist.setText("流量统计功能已开启");
						} else {
							contact = new Contact(MainSetActivity.this, "mCount");
							contact.saveParameter("0");
							is_statist.setText("流量统计功能已关闭");
						}
					}
				});

		
		// 设置流量警示功能是否开启
		final TextView is_warn = (TextView)findViewById(R.id.is_warn);
		Parameter par2 = new Parameter(this);
		String mWarn = par2.getParameter("mWarn");
		//初始化is_warn的值
		if(mWarn.equals("1"))
		{
			is_warn.setText("流量警示功能已开启");
		}
		else
		{
			is_warn.setText("流量警示功能已关闭");
		}
		
		isWarn.setChecked(this.BoxState(mWarn));
		isWarn
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Contact contact;
						// TODO Auto-generated method stub
						if (isChecked) {
							contact = new Contact(MainSetActivity.this, "mWarn");
							contact.saveParameter("1");
							is_warn.setText("流量警示功能已开启");
						} else {
							contact = new Contact(MainSetActivity.this, "mWarn");
							contact.saveParameter("0");
							is_warn.setText("流量警示功能已关闭");
						}
					}
				});

		ImageView imgView1 = (ImageView) this.findViewById(R.id.imageButton_quota);
		ImageView imgView2 = (ImageView) this.findViewById(R.id.imageButton_date);
		ImageView imgView3 = (ImageView) this.findViewById(R.id.imageButton_period);
	
		ImageView imgView5 = (ImageView) this.findViewById(R.id.imageButton_clear);
		final TextView limit_flow = (TextView)findViewById(R.id.limit_flow);

		final TextView count_date = (TextView)findViewById(R.id.count_date);
		final TextView refresh = (TextView)findViewById(R.id.refresh);
		final TextView clear_data = (TextView)findViewById(R.id.clear_data);
		
		//初始化limit_flow的值
		Parameter par3 = new Parameter(this);
		final String mLimit = par3.getParameter("mLimit");
		limit_flow.setText("每月流量限额为"+mLimit+"MB");
        // 设置每月流量限额
		imgView1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				LayoutInflater factory = LayoutInflater.from(MainSetActivity.this);
				View_quota = factory.inflate(R.layout.setup_quota, null);

				new AlertDialog.Builder(MainSetActivity.this).setTitle("每月流量限额").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_quota)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Contact contact = new Contact(
												MainSetActivity.this, "mLimit");
										dt = (EditText) View_quota
												.findViewById(R.id.quota12);
										String limit = dt.getText().toString();
										contact.saveParameter(limit);
										limit_flow.setText("每月流量限额为"+limit+"MB");
									}

								}).setNegativeButton("取消", null).show();
			}
		});

		
		//初始化count_date的值
		Parameter par4 = new Parameter(this);
		String mCountDate = par3.getParameter("mdate");
		count_date.setText("月结算日为该月"+mCountDate+"日");
		//设置月结算日
		imgView2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				LayoutInflater factory = LayoutInflater.from(MainSetActivity.this);
				View_date = factory.inflate(R.layout.setup_date, null);

				new AlertDialog.Builder(MainSetActivity.this).setTitle("月结算日").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_date)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Contact contact = new Contact(
												MainSetActivity.this, "mdate");
										dt2 = (EditText) View_date
												.findViewById(R.id.setup_date);
										String date = dt2.getText().toString();
										if(Integer.valueOf(date)<1||Integer.valueOf(date)>31)
										{
											Toast.makeText(MainSetActivity.this, "请输入1~31的数字！", Toast.LENGTH_SHORT).show();
										}
										else{
																				
										contact.saveParameter(date);
										count_date.setText("月结算日为该月"+date+"日");
										}
									}

								}).setNegativeButton("取消", null).show();
			}
		});

		//初始化刷新频率
		Parameter par5 = new Parameter(this);
		String mRefresh = par3.getParameter("myRound");
		refresh.setText("数据刷新频率为"+mRefresh+"一次");
		//设置刷新频率
		imgView3.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(MainSetActivity.this);
				final View View_quota = factory.inflate(R.layout.setup_period,
						null);

				final RadioGroup rGroup = (RadioGroup) View_quota
						.findViewById(R.id.date_DatePicker);
				Parameter par = new Parameter(MainSetActivity.this);
				final String str = par.getParameter("myRound");

				final RadioButton rb1 = (RadioButton) View_quota.findViewById(R.id.RadioButton1);
				final RadioButton rb2 = (RadioButton) View_quota.findViewById(R.id.RadioButton2);
				final RadioButton rb3 = (RadioButton) View_quota.findViewById(R.id.RadioButton3);
				final RadioButton rb4 = (RadioButton) View_quota.findViewById(R.id.RadioButton4);
				
				final StringBuilder sBuilder = new StringBuilder();;
				if (str.equals("30秒")) {
					rb1.setChecked(true);
				} else if (str.equals("50秒") || str.equals("")) {
					rb2.setChecked(true);
				} else if (str.equals("100秒")) {
					rb3.setChecked(true);
				} else if (str.equals("150秒")) {
					rb4.setChecked(true);
				}
				rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
					
						if (checkedId == rb1.getId()) {
							sBuilder.append(rb1
									.getText()
									.toString());
						} else if (checkedId == rb2
								.getId()) {
							sBuilder.append(rb2
									.getText()
									.toString());
						} else if (checkedId == rb3
								.getId()) {
							sBuilder.append(rb3
									.getText()
									.toString());
						} else if (checkedId == rb4
								.getId()) {
							sBuilder.append(rb4
									.getText()
									.toString());
		
						}
					}
					
				});
				new AlertDialog.Builder(MainSetActivity.this).setTitle("周期设置").setView(
						View_quota).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Log.v("click","click");
								Contact contact = new Contact(
										MainSetActivity.this, "myRound");
								contact.saveParameter(sBuilder.toString());
								refresh.setText("数据刷新频率为"+sBuilder.toString()+"一次");
						}

						}).setNegativeButton("取消", null).show();
			}
		});
		
	
		
		//清空所有统计数据
		imgView5.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(MainSetActivity.this);
				View_clear = factory.inflate(R.layout.setup_clear, null);

				new AlertDialog.Builder(MainSetActivity.this).setTitle("您确定要清楚所有统计记录吗").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_clear)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										
										// TODO Auto-generated method stub
										dbAdapter.clear();
										clear_data.setText("数据已清空");
									}

								}).setNegativeButton("取消", null).show();
			}
		});
		
		
		
		



	}

	public boolean BoxState(String s) {
		if (s.equals("")) {
			return false;
		} else if (Integer.parseInt(s) == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 按下键盘上返回按钮

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent1 = new Intent();
			intent1.setClass(MainSetActivity.this, MainActivity.class);
			startActivity(intent1);
			MainSetActivity.this.finish();
		}
		return true;
	}			



}

