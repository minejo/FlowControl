package com.example.flowcontrol.DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite数据库的操作类，对一些数据操作的函数进行了封装
 * @author Jonathan
 *
 */
public class DataBaseAdapter {
	// 数据库对象
	private SQLiteDatabase mSQLiteDatabase = null;
	// 数据库名名称
	private final static String DATABASE_NAME = "Flow.db";
	// 表名
	private final static String TABLE_NAME = "table1";

	
	// 主键，ID
	private final static String TABLE_ID = "FlowID";
	// 上行流量，单位byte
	private final static String TABLE_UPF = "UpFlow";
	// 下载流量，单位byte
	private final static String TABLE_DPF = "DownFlow";
	// 储存日期
	// 格式：YYYY-MM-DD HH:MM:SS
	private final static String TABLE_TIME = "Time";
	// 联网类型，有WIFI和GPRS
	private final static String TABLE_WEB = "WebType";

	
	// 数据库版本号
	private final static int DB_VERSION = 1;

	private Context mContext = null;

	// 创建表的语句，用于第一次创建数据库时，创建表1
	private final static String CREATE_TABLE1 = "CREATE TABLE " + TABLE_NAME
			+ " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ TABLE_UPF + " Long," + TABLE_DPF + " Long," + TABLE_WEB
			+ " INTEGER," + TABLE_TIME + " DATETIME)";
		

	// 数据库adapter，用于创建数据库
	private DatabaseHelper mDatabaseHelper = null;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}

		/*
		 * 创建数据库 创建表
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE1);

		}

		/*
		 * 数据库跟新删除表并重新创建新表
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	/**
	 * 构造函数 获取context
	 * 
	 * @param context
	 */
	public DataBaseAdapter(Context context) {
		mContext = context;
	}

	/**
	 * 打开数据库，返回数据库对象
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		mDatabaseHelper.close();
	}

	/**
	 * 插入一条数据
	 * 
	 * @param UpFlow
	 *            上传流量
	 * @param DownFlow
	 *            下载流量
	 * @param WebType
	 *            网络类型
	 * @param date
	 *            更新时间
	 */
	public void insertData(long UpFlow, long DownFlow, int WebType, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
		String dataString = sdf.format(date);
		String insertData = " INSERT INTO " + TABLE_NAME + " (" + TABLE_UPF
				+ ", " + TABLE_DPF + "," + TABLE_WEB + "," + TABLE_TIME
				+ " ) values(" + UpFlow + ", " + DownFlow + "," + WebType + ","
				+ "datetime('" + dataString + "'));";
		mSQLiteDatabase.execSQL(insertData);
		System.out.println("up:" + UpFlow + "|down:" + DownFlow + " type:"
				+ WebType + " date:" + dataString);
	}

	/**
	 * 更新数据
	 * 
	 * @param UpFlow
	 *            上传流量
	 * @param DownFlow
	 *            下载流量
	 * @param WebType
	 *            网络类型
	 * @param date
	 *            更新时间
	 */
	public void updateData(long upFlow, long downFlow, int webType, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dataString = sdf.format(date);
		String updateData = " UPDATE " + TABLE_NAME + " SET " + TABLE_UPF + "="
				+ upFlow + " , " + TABLE_DPF + "=" + downFlow + " WHERE "
				+ TABLE_WEB + "=" + webType + " and " + TABLE_TIME + " like '"
				+ dataString + "%'";
		mSQLiteDatabase.execSQL(updateData);
	}

	/**
	 * 检查是否存在该条数据
	 * 
	 * @param netType
	 *            网络类型
	 * @param date
	 *            更新时间
	 * @return
	 */
	public Cursor check(int netType, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dataString = sdf.format(date);
		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				TABLE_UPF + " AS upPro", TABLE_DPF + " AS dwPro" }, TABLE_WEB
				+ "=" + netType + " and " + TABLE_TIME + " like '" + dataString
				+ "%'", null, null, null, null, null);// date转化
		return mCursor;
	}

	/**
	 * 查询今日流量数据
	 * 
	 * @param year
	 *            更新年
	 * @param month
	 *            更新月
	 * @param day
	 *            更新日
	 * @param netType
	 *            网络类型
	 * @return
	 */
	public Cursor fetchDayFlow(int year, int month, int day, int netType) {
		StringBuffer date = new StringBuffer();
		date.append(String.valueOf(year) + "-");
		if (month < 10) {
			date.append("0" + String.valueOf(month) + "-");
		} else {
			date.append(String.valueOf(month) + "-");
		}
		if (day < 10) {
			date.append("0" + String.valueOf(day));
		} else {
			date.append(String.valueOf(day));
		}
		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				"sum(" + TABLE_UPF + ") AS sumUp",
				"sum(" + TABLE_DPF + ") as sumDw" }, TABLE_WEB + "=" + netType
				+ " and " + TABLE_TIME + " LIKE '" + date.toString() + "%'",
				null, null, null, null, null);
		return mCursor;
	}

	/* 查询每月流量 可用于月报表和月流量统计 */
	public Cursor fetchMonthFlow(int year, int Month, int netType) {
		StringBuffer date = new StringBuffer();
		date.append(String.valueOf(year) + "-");
		if (Month < 10) {
			date.append("0" + String.valueOf(Month) + "-");
		} else {
			date.append(String.valueOf(Month) + "-");
		}
		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				"sum(" + TABLE_UPF + ") AS monthUp",
				"sum(" + TABLE_DPF + ") as monthDw" }, TABLE_WEB + "="
				+ netType + " and " + TABLE_TIME + " LIKE '" + date.toString()
				+ "%'", null, null, null, null, null);

		return mCursor;
	}

	/**
	 * 计算每天的上传流量
	 * 
	 * @param netType
	 *            网络类型
	 * @param date
	 *            更新时间
	 * @return
	 */
	public long getProFlowUp(int netType, Date date) {
		Cursor cur = check(netType, date);
		long UP = 0;
		if (cur.moveToNext()) {
			int up = cur.getColumnIndex("upPro");
			UP = cur.getLong(up);
		}
		cur.close();
		return UP;
	}

	/**
	 * 计算每天的下载流量
	 * 
	 * @param netType
	 *            网络类型
	 * @param date
	 *            更新时间
	 * @return
	 */
	public long getProFlowDw(int netType, Date date) {
		Cursor cur = check(netType, date);
		long UP = 0;
		if (cur.moveToNext()) {
			int up = cur.getColumnIndex("dwPro");
			UP = cur.getLong(up);
		}
		cur.close();
		return UP;
	}

	/**
	 * 计算每日的流量 
	 * @param year
	 *            更新年
	 * @param month
	 *            更新月
	 * @param day
	 *            更新日
	 * @param netType
	 *            网络类型
	 * @return
	 */
	public long calculateDay(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int upColumn = calCurso.getColumnIndex("sumUp");
					int dwColumn = calCurso.getColumnIndex("sumDw");
					sum = calCurso.getLong(upColumn)
							+ calCurso.getLong(dwColumn);
				} while (calCurso.moveToNext());
			}
		}		
		return sum;
	}


	public long calculateUpForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum = 0;

		if (lCursor != null) {
			if (lCursor.moveToFirst()) {
				do {
					int upColumn = lCursor.getColumnIndex("monthUp");
					sum += lCursor.getLong(upColumn);
				} while (lCursor.moveToNext());
			}
			lCursor.close();
		}
		return sum;
	}

	/**
	 * 计算每月下载流量
	 * @param year 更新年
	 * @param Month 更新月
	 * @param netType 网络类型
	 * @return
	 */
	public long calculateDnForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum = 0;

		if (lCursor != null) {
			if (lCursor.moveToFirst()) {
				do {
					int dwColumn = lCursor.getColumnIndex("monthDw");
					sum += lCursor.getLong(dwColumn);
				} while (lCursor.moveToNext());
			}
			lCursor.close();
		}
		return sum;
	}

	/**
	 * 计算某月的总流量 
	 * @param year 更新年
	 * @param Month 更新月
	 * @param netType 网络类型
	 * @return
	 */
	public long calculateForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum;
		long monthSum = 0;

		if (lCursor != null) {
			if (lCursor.moveToFirst()) {
				do {
					int upColumn = lCursor.getColumnIndex("monthUp");
					int dwColumn = lCursor.getColumnIndex("monthDw");
					sum = lCursor.getLong(upColumn) + lCursor.getLong(dwColumn);
					monthSum += sum;
				} while (lCursor.moveToNext());
			}
			lCursor.close();
		}
		return monthSum;
	}

	/**
	 * 计算每日的上传流量 
	 * @param year
	 *            更新年
	 * @param month
	 *            更新月
	 * @param day
	 *            更新日
	 * @param netType
	 *            网络类型
	 * @return
	 */
	public long calculateUp(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int upColumn = calCurso.getColumnIndex("sumUp");
					sum = calCurso.getLong(upColumn);
				} while (calCurso.moveToNext());
			}
		}
		calCurso.close();
		return sum;
	}

	/**
	 * 计算每日的下载流量 
	 * @param year
	 *            更新年
	 * @param month
	 *            更新月
	 * @param day
	 *            更新日
	 * @param netType
	 *            网络类型
	 * @return
	 */
	public long calculateDw(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int dwColumn = calCurso.getColumnIndex("sumDw");
					sum = calCurso.getLong(dwColumn);
				} while (calCurso.moveToNext());
			}
		}
		calCurso.close();
		return sum;
	}

	/**
	 * 清空数据 
	 */
	public void deleteAll() {
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}

   /**
    * 清楚数据库内容
    */
	public void clear() {
		mSQLiteDatabase.delete(TABLE_NAME, null, null);
	}
}
