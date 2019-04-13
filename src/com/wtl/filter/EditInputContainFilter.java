package com.wtl.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wtl.ui.Ble_Activity;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

public class EditInputContainFilter implements InputFilter {
	/**
	 * ��С����
	 */
	public static final int MIN_VALUE = 0;
	/**
	 * �������
	 */
	public static final int MAX_VALUE = 230;
	/**
	 * С���������ֵ�λ��
	 */
	public static final int PONTINT_LENGTH = 0;
	Pattern p;
	public EditInputContainFilter(){   
		p = Pattern.compile("[0-9]*");   //���������������
	}   

	/**
	 *  source    ��������ַ���   
	 *  start    ��������ַ�����ʼ�±꣬һ��Ϊ0    
	 *  end    ��������ַ����յ��±꣬һ��Ϊsource����-1    
	 *  dest    ����֮ǰ�ı�������    
	 *  dstart    ԭ������ʼ���꣬һ��Ϊ0    
	 *  dend    ԭ�����յ����꣬һ��Ϊdest����-1
	 */

	@Override  
	public CharSequence filter(CharSequence src, int start, int end,   
			Spanned dest, int dstart, int dend) {   
		String oldtext =  dest.toString();
		System.out.println(oldtext);
		//��֤ɾ���Ȱ���
		if ("".equals(src.toString())) {   
			return null;
		}
		//��֤�����ֻ���С��������
		Matcher m = p.matcher(src); 
		if(oldtext.contains(".")){
			//�Ѿ�����С���������£�ֻ����������
			if(!m.matches()){
				return null;
			}
		}else{
			//δ����С���������£���������С���������
			if(!m.matches() && !src.equals(".") ){
				return null;
			} 
		}
		//��֤������Ĵ�С
		if(!src.toString().equals("")){
			double dold = Double.parseDouble(oldtext+src.toString());
			if(dold > MAX_VALUE){
//				Toast.makeText(Ble_Activity.this, "����������ܴ���MAX_VALUE", Toast.LENGTH_SHORT)
//				.show();
				return dest.subSequence(dstart, dend);
			}else if(dold == MAX_VALUE){
				if(src.toString().equals(".")){
//					CustomerToast.showToast(RechargeActivity.this, "����������ܴ���MAX_VALUE");
					return dest.subSequence(dstart, dend);
				}
			}else if(dold<MIN_VALUE){
				return "";
			}
		}
		//��֤С��λ�����Ƿ���ȷ
		if(oldtext.contains(".")){
			int index = oldtext.indexOf(".");
			int len = dend - index;	
			//С��λֻ��2λ
			if(len > PONTINT_LENGTH){
				CharSequence newText = dest.subSequence(dstart, dend);
				return newText;
			}
		}
		return dest.subSequence(dstart, dend) +src.toString();
	}   

}
