package com.example.flowcontrol.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

/**
 * 文件处理类
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

	private InputStream is;/* 文件输入流，读取文件流 */
	private String Text_of_output = "";/* 字符串，从文件中读取到得字符串 */

	private OutputStream oss;/* 文件输出流，保存文件流 */

	public String getParameter(String fileName) {
		/* 初次使用时 */
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
			/* 打开文件输入流 */
			is = context.openFileInput(fileName);
			/* 初始化字节数组 */
			byte[] readBytes =  new byte[is.available()];
//			b = new byte[1024];
			/* 从文件输入流中读取内容到字节数组中，返回内容长度 */
			int length = is.read(readBytes);
			/* 把字节数组转换成字符串 */
			Text_of_output = new String(readBytes);
			/* 设置标题，显示文件内容长度 */
			// setTitle("文件字数：" + length);
			/* 显示文件内容 */
			// textview.setText(Text_of_output);
			return Text_of_output;
		} catch (FileNotFoundException e) {
			/* 文件未找到，异常 */
			// TODO Auto-generated catch block
			return null;

		} catch (IOException e) {
			/* 文件读取错误，异常 */
			// TODO Auto-generated catch block
			return null;
		} finally {
			try {
				/* 关闭文件输入流 */
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
	}
}
