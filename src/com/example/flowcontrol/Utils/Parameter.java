package com.example.flowcontrol.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

/**
 * �ļ�������
 * @author Jonathan
 *
 */
public class Parameter {
	Context context;

	public Parameter(Context context) {
		this.context = context;
	}
	public Parameter(Runnable runnable) {
		this.context = (Context) runnable;
	}

	private InputStream is;/* �ļ�����������ȡ�ļ��� */
	private String Text_of_output = "";/* �ַ��������ļ��ж�ȡ�����ַ��� */

	private OutputStream oss;/* �ļ�������������ļ��� */

	public String getParameter(String fileName) {
		/* ����ʹ��ʱ */
		oss = null;
		try {
			oss = context.openFileOutput(fileName, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} finally {
			try {
				oss.close();
			} catch (IOException e) {
			}
		}
		is = null;
		try {
			/* ���ļ������� */
			is = context.openFileInput(fileName);
			/* ��ʼ���ֽ����� */
			byte[] readBytes =  new byte[is.available()];
//			b = new byte[1024];
			/* ���ļ��������ж�ȡ���ݵ��ֽ������У��������ݳ��� */
			int length = is.read(readBytes);
			/* ���ֽ�����ת�����ַ��� */
			Text_of_output = new String(readBytes);
			/* ���ñ��⣬��ʾ�ļ����ݳ��� */
			// setTitle("�ļ�������" + length);
			/* ��ʾ�ļ����� */
			// textview.setText(Text_of_output);
			return Text_of_output;
		} catch (FileNotFoundException e) {
			/* �ļ�δ�ҵ����쳣 */
			// TODO Auto-generated catch block
			return null;

		} catch (IOException e) {
			/* �ļ���ȡ�����쳣 */
			// TODO Auto-generated catch block
			return null;
		} finally {
			try {
				/* �ر��ļ������� */
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
	}
}
