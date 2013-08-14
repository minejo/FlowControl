package com.example.flowcontrol.DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite���ݿ�Ĳ����࣬��һЩ���ݲ����ĺ��������˷�װ
 * @author Jonathan
 *
 */
public class DataBaseAdapter {
	// ���ݿ����
	private SQLiteDatabase mSQLiteDatabase = null;
	// ���ݿ�������
	private final static String DATABASE_NAME = "Flow.db";
	// ����
	private final static String TABLE_NAME = "table1";

	
	// ������ID
	private final static String TABLE_ID = "FlowID";
	// ������������λbyte
	private final static String TABLE_UPF = "UpFlow";
	// ������������λbyte
	private final static String TABLE_DPF = "DownFlow";
	// ��������
	// ��ʽ��YYYY-MM-DD HH:MM:SS
	private final static String TABLE_TIME = "Time";
	// �������ͣ���WIFI��GPRS
	private final static String TABLE_WEB = "WebType";

	
	// ���ݿ�汾��
	private final static int DB_VERSION = 1;

	private Context mContext = null;

	// ���������䣬���ڵ�һ�δ������ݿ�ʱ��������1
	private final static String CREATE_TABLE1 = "CREATE TABLE " + TABLE_NAME
			+ " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ TABLE_UPF + " Long," + TABLE_DPF + " Long," + TABLE_WEB
			+ " INTEGER," + TABLE_TIME + " DATETIME)";
		

	// ���ݿ�adapter�����ڴ������ݿ�
	private DatabaseHelper mDatabaseHelper = null;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}

		/*
		 * �������ݿ� ������
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE1);

		}

		/*
		 * ���ݿ����ɾ�������´����±�
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	/**
	 * ���캯�� ��ȡcontext
	 * 
	 * @param context
	 */
	public DataBaseAdapter(Context context) {
		mContext = context;
	}

	/**
	 * �����ݿ⣬�������ݿ����
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * �ر����ݿ�
	 */
	public void close() {
		mDatabaseHelper.close();
	}

	/**
	 * ����һ������
	 * 
	 * @param UpFlow
	 *            �ϴ�����
	 * @param DownFlow
	 *            ��������
	 * @param WebType
	 *            ��������
	 * @param date
	 *            ����ʱ��
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
	 * ��������
	 * 
	 * @param UpFlow
	 *            �ϴ�����
	 * @param DownFlow
	 *            ��������
	 * @param WebType
	 *            ��������
	 * @param date
	 *            ����ʱ��
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
	 * ����Ƿ���ڸ�������
	 * 
	 * @param netType
	 *            ��������
	 * @param date
	 *            ����ʱ��
	 * @return
	 */
	public Cursor check(int netType, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dataString = sdf.format(date);
		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				TABLE_UPF + " AS upPro", TABLE_DPF + " AS dwPro" }, TABLE_WEB
				+ "=" + netType + " and " + TABLE_TIME + " like '" + dataString
				+ "%'", null, null, null, null, null);// dateת��
		return mCursor;
	}

	/**
	 * ��ѯ������������
	 * 
	 * @param year
	 *            ������
	 * @param month
	 *            ������
	 * @param day
	 *            ������
	 * @param netType
	 *            ��������
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

	/* ��ѯÿ������ �������±����������ͳ�� */
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
	 * ����ÿ����ϴ�����
	 * 
	 * @param netType
	 *            ��������
	 * @param date
	 *            ����ʱ��
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
	 * ����ÿ�����������
	 * 
	 * @param netType
	 *            ��������
	 * @param date
	 *            ����ʱ��
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
	 * ����ÿ�յ����� 
	 * @param year
	 *            ������
	 * @param month
	 *            ������
	 * @param day
	 *            ������
	 * @param netType
	 *            ��������
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
	 * ����ÿ����������
	 * @param year ������
	 * @param Month ������
	 * @param netType ��������
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
	 * ����ĳ�µ������� 
	 * @param year ������
	 * @param Month ������
	 * @param netType ��������
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
	 * ����ÿ�յ��ϴ����� 
	 * @param year
	 *            ������
	 * @param month
	 *            ������
	 * @param day
	 *            ������
	 * @param netType
	 *            ��������
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
	 * ����ÿ�յ��������� 
	 * @param year
	 *            ������
	 * @param month
	 *            ������
	 * @param day
	 *            ������
	 * @param netType
	 *            ��������
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
	 * ������� 
	 */
	public void deleteAll() {
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}

   /**
    * ������ݿ�����
    */
	public void clear() {
		mSQLiteDatabase.delete(TABLE_NAME, null, null);
	}
}
