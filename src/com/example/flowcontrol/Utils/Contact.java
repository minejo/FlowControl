package com.example.flowcontrol.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 文件处理类
 * @author Jonathan
 *
 */
public class Contact{
    /** Called when the activity is first created. */
	 Context context;
	 private String Text_of_input;/* 字符串，用户输入的字符串 */	 
	 private OutputStream os;/* 文件输出流，保存文件流 */	 
	 String file;
	 
	 public Contact(Context context,String file){
		 this.context = context;
		 this.file = file;
	 }


	private void UIinit() {      

	       Parameter par = new Parameter(context);
	       String mLimit = par.getParameter(file);

	}

	private void Logic() {
					
	}
     
	public void saveParameter(String limit) {
			/* 提示 */
			os=null;
			
			try {
				/* 打开文件输出流，名称myPage，以不覆盖模式打开 */
				os = context.openFileOutput(file,Context.MODE_PRIVATE);
				Log.v("contact_context",context.toString());
				/* 获得用户输入的字符串 */
				//Log.v("Text_of_input",Text_of_input);
			
				Text_of_input = limit;
				
				/* 把字符串转换成字节数组，写入文件中 */
				os.write(Text_of_input.getBytes());
			} catch (FileNotFoundException e) {
				/* 文件未找到，异常 */
				// TODO Auto-generated catch block
				NoteDebug("文件关闭失败" + e);
			} catch (IOException e) {
				/* 文件写入错误 */
				// TODO Auto-generated catch block
				NoteDebug("文件写入失败" + e);
			} finally {
				if(os!=null&&! Text_of_input.equals("")){
				try {
					os.flush();
					/* 关闭文件输出流 */
					os.close();
					NoteDebug("设置成功");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					NoteDebug("文件关闭失败" + e);
				}
			} else
				NoteDebug("请重新输入");
			}


		}
        
	private void NoteDebug(String showString) {
		/*显示Toast提示*/
		Toast.makeText(context, showString, Toast.LENGTH_SHORT).show();
	}
}