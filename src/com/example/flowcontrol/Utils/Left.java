package com.example.flowcontrol.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * �ļ�������
 * @author Jonathan
 *
 */
public class Left{
    /** Called when the activity is first created. */
	 Context context;
	 private String Text_of_input;
	 private OutputStream os;	 
	 String file;
	 
	 public Left(Context context,String file){
		 this.context = context;
		 this.file = file;
	 }


	private void UIinit() {      

	       Parameter par = new Parameter(context);
	       String mLeft = par.getParameter(file);

	}

	private void Logic() {
					
	}
     
	public void saveParameter(String left) {
			os=null;
			
			try {
				os = context.openFileOutput(file,Context.MODE_PRIVATE);
				Log.v("contact_context",context.toString());			
				Text_of_input = left;						
				os.write(Text_of_input.getBytes());
			} catch (FileNotFoundException e) {
				NoteDebug("�ļ��ر�ʧ��" + e);
			} catch (IOException e) {
				NoteDebug("�ļ�д��ʧ��" + e);
			} finally {
				if(os!=null&&! Text_of_input.equals("")){
				try {
					os.flush();
					os.close();
					NoteDebug("���óɹ�");
				} catch (IOException e) {
					NoteDebug("�ļ��ر�ʧ��" + e);
				}
			} else
				NoteDebug("����������");
			}


		}
        
	private void NoteDebug(String showString) {
		Toast.makeText(context, showString, Toast.LENGTH_SHORT).show();
	}

}
