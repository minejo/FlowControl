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
public class Contact{
    /** Called when the activity is first created. */
	 Context context;
	 private String Text_of_input;/* �ַ������û�������ַ��� */	 
	 private OutputStream os;/* �ļ�������������ļ��� */	 
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
			/* ��ʾ */
			os=null;
			
			try {
				/* ���ļ������������myPage���Բ�����ģʽ�� */
				os = context.openFileOutput(file,Context.MODE_PRIVATE);
				Log.v("contact_context",context.toString());
				/* ����û�������ַ��� */
				//Log.v("Text_of_input",Text_of_input);
			
				Text_of_input = limit;
				
				/* ���ַ���ת�����ֽ����飬д���ļ��� */
				os.write(Text_of_input.getBytes());
			} catch (FileNotFoundException e) {
				/* �ļ�δ�ҵ����쳣 */
				// TODO Auto-generated catch block
				NoteDebug("�ļ��ر�ʧ��" + e);
			} catch (IOException e) {
				/* �ļ�д����� */
				// TODO Auto-generated catch block
				NoteDebug("�ļ�д��ʧ��" + e);
			} finally {
				if(os!=null&&! Text_of_input.equals("")){
				try {
					os.flush();
					/* �ر��ļ������ */
					os.close();
					NoteDebug("���óɹ�");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					NoteDebug("�ļ��ر�ʧ��" + e);
				}
			} else
				NoteDebug("����������");
			}


		}
        
	private void NoteDebug(String showString) {
		/*��ʾToast��ʾ*/
		Toast.makeText(context, showString, Toast.LENGTH_SHORT).show();
	}
}