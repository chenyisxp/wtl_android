package com.wtl.html5;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.wtl.MainActivity;
import com.wtl.R;
import com.wtl.bean.WeldInfo;
import com.wtl.common.Data;
import com.wtl.common.DateUtil;
import com.wtl.common.SQLiteDBUtil;
import com.wtl.common.SaveUtil;

public class HtmlMainActivity extends Activity {
	public static final String TAG = "HtmlMainActivity";
	private static String nowUrl="";
	private WebView mWebView;
	private static Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    context =getApplicationContext();//������
	    setContentView(R.layout.activity_html);
	    mWebView = (WebView) findViewById(R.id.webView);
//	    mWebView.loadDataWithBaseURL(null,"file:///android_asset/test.html", "text/html", "gbk",null);
//
//	    mWebView.loadUrl("file:///android_asset/JavaScript.html");
//	    mWebView.loadUrl("file:///android_asset/test2.html");
//	    mWebView.loadUrl("file:///android_asset/test.html");
//	    mWebView.loadUrl("file:///android_asset/testJrange.html");
//	    mWebView.loadUrl("file:///android_asset/index.html");
	    mWebView.loadUrl("http://192.168.1.5:8081/#/");
	    
	    WebSettings webSettings = mWebView.getSettings();
	    webSettings.setJavaScriptEnabled(true);
	    webSettings.setDefaultTextEncodingName("gbk");//���ñ����ʽ
	    mWebView.addJavascriptInterface(new JsInteration(), "android");
	    mWebView.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        	 Toast.makeText(HtmlMainActivity.this,Data.getA(),1).show();
	        	 nowUrl=url;
//	            if (url.equals("file:///android_asset/test2.html")) {
//	                Log.e(TAG, "shouldOverrideUrlLoading: " + url);
//	                startActivity(new Intent(SecondActivity.this,MainActivity.class));
//	                return true;//activity֮����ת
//	            } else {
	                mWebView.loadUrl(url);
	                return false;//������webview���
//	            }
	        }
		    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
		        return onJsAlert(view, url, message, result);
		    }
	    });  
	    //������� js�����Ȳ���
	    mWebView.setWebChromeClient(new WebChromeClient());
	    //�������
	    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

	}
	//Android�����з���ֵjs����
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void onClick(View v) {
	 
	    mWebView.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
	        @Override
	        public void onReceiveValue(String value) {
	            Log.e(TAG, "onReceiveValue value=" + value);
	        }
	    });
	}
	public class JsInteration {
	 
	    @JavascriptInterface
	    public String back() {
//	        return "hello world";
	    	return getResources().getString(R.string.hello_world);
	    }
	    @JavascriptInterface
	    public String getDate() {
	        return "20:29:20";
	    }
	    //����ֵ ����SharedPreferences ��Ƭ�洢
	    @JavascriptInterface
	    public String setSettingInfo(String key,String value) {
	    	return SaveUtil.updateDataBykey(context, key, value);
	    }
	    //��ȡ����ֵ ����SharedPreferences ��Ƭ�洢
	    @JavascriptInterface
	    public String getSettingInfo(String key) {
	        return  SaveUtil.getDataBykey(context,key);
	    }
	    //��ȡ����ֵ ����sql ��Ƭ�洢
	    @JavascriptInterface
	    public void testSqliteset() {
	    	// ��ȡ���ݿ��������
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getWritableDatabase();
			//1������һ
				// ����sql���
				//String sql = "insert into weldInfo(machinedId,weldType,dataTAype,setInfo,noteInfo,weldBeginTime,weldEndTime,weldConnectTime,memo,weldType)"
				//			+ "values('"+name+"','"+age+"')";
				// ִ��sql���
				//	sd.execSQL(sql);
				// �ر����ݿ�
				//db.close();
			//2��������
				 //ʵ��������ֵ
				 ContentValues cValue = new ContentValues();
				 cValue.put("machinedId","A0001");
				 cValue.put("machinedName","�̵ؽ��һ�Ż�");
				 cValue.put("weldType","MMA");
				 cValue.put("dataType","0");
				 cValue.put("setInfo","{\"weldType\":\"MIG SYN\",\"options\":[{\"name\":\"MODE\",\"value\":\"21T\",\"unit\":\"T\"},{\"name\":\"MATERIAL\",\"value\":\"FE\"},{\"name\":\"GAS\",\"value\":\"Ar\"},{\"name\":\"DIAMETER\",\"value\":\"0.6mm\"},{\"name\":\"THICKNESS\",\"value\":\"0.6mm\"},{\"name\":\"INDUCTANCE\",\"value\":\"90\",\"unit\":\"A\"},{\"name\":\"SPEED\",\"value\":\"8\",\"unit\":\"BIG1\"}]}");
				 cValue.put("noteInfo","�ҵı�ע");
				 cValue.put("weldBeginTime","2018-11-09 12:20:11");
				 cValue.put("weldEndTime","2018-11-09 15:20:11");
				 cValue.put("weldConnectTime","3.7");
				 cValue.put("memo","�½���ע");
				 cValue.put("rec_stat","1");
				 cValue.put("creator","admin");
				 cValue.put("modifier","admin");
				 Log.v("aaaa", DateUtil.getCurrentDateyyyyMMddHHmmss());
				 cValue.put("cre_tm",DateUtil.getCurrentDateyyyyMMddHHmmss());
				 cValue.put("up_tm",DateUtil.getCurrentDateyyyyMMddHHmmss());
				 //����insert()������������
				 sd.insert("weldInfo", null, cValue);
				 db.close();
	    }
	    //��ȡ����ֵ ����sql ��Ƭ�洢
	    @JavascriptInterface
	    public String testSqliteget() {
	    	// �����е���Ϣչʾ���ı�����
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			String sql = "select id,machinedId,machinedName,weldType,dataType,setInfo,noteInfo,weldBeginTime"
								+ ",weldEndTime,weldConnectTime,memo,rec_stat,creator,modifier,cre_tm,up_tm"
								+ " from weldInfo  ORDER BY cre_tm DESC";//����ʱ������
			Cursor cursor = sd.rawQuery(sql, null);
			WeldInfo weldInfo =new WeldInfo();
			List<WeldInfo> wldlist =new ArrayList<WeldInfo>();
			while (cursor.moveToNext()) {
				weldInfo.setId(cursor.getInt(0));
				weldInfo.setMachinedId(cursor.getString(1));
				weldInfo.setMachinedName(cursor.getString(2));
				weldInfo.setWeldType(cursor.getString(3));
				weldInfo.setDataType(cursor.getString(4));
				weldInfo.setSetInfo(cursor.getString(5));
				weldInfo.setNoteInfo(cursor.getString(6));
				weldInfo.setWeldBeginTime(cursor.getString(7));
				weldInfo.setWeldEndTime(cursor.getString(8));
				weldInfo.setWeldConnectTime(cursor.getString(9));
				weldInfo.setMemo(cursor.getString(10));
				weldInfo.setRec_stat(cursor.getString(11));
				weldInfo.setCreator(cursor.getString(12));
				weldInfo.setModifier(cursor.getString(13));
				weldInfo.setCre_tm(cursor.getString(14));
				weldInfo.setUp_tm(cursor.getString(15));
//				s += "id:" + cursor.getInt(0) + "\tmachinedId:" + cursor.getString(1)
//						+ "\t machinedName:" + cursor.getString(2) + "\t setInfo:" + cursor.getString(5) + "\n";
//				s+=weldInfo.toString();
				wldlist.add(weldInfo);
			}
			Log.w("aaaaa", s);
			db.close();
			cursor.close();
			return wldlist.toString();
	    }
	    //��ȡ����ֵ������
	    @JavascriptInterface
	    public String testSqliteupdate() {
	    	// �����е���Ϣչʾ���ı�����
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			//ʵ��������ֵ
			ContentValues values = new ContentValues();  
			//��values���������  
			values.put("setInfo","{'current':'10','valatge':'20'}");  
			//�޸�����  
			String whereClause = "id=?";  
			//�޸���Ӳ���  
			String[] whereArgs={String.valueOf(1)};  
			//�޸�  
//			sd.update("weldInfo",values,whereClause,whereArgs);
			sd.update("weldInfo",values,null,null);  
			db.close();
			return s;
	    }
	    //��ȡ����ֵ������
	    @JavascriptInterface
	    public String testSqlitedelete() {
	    	// �����е���Ϣչʾ���ı�����
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			String sql = "delete from weldInfo";
			sd.execSQL(sql);
			db.close();
			return s;
	    }
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)  {
//	     if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //���µ������BACK��ͬʱû���ظ�
//	      
//	        return true;
//	    }
//
//	    return super.onKeyDown(keyCode, event);
//	}
	@Override
	public void onBackPressed() {
			// ���ﴦ���߼����룬���ע�⣺�÷�����������2.0����°��sdk
		 	Toast.makeText(HtmlMainActivity.this,Data.getA(),1).show();
			Data.setA("������Ŷ��");
		 	if(nowUrl.equals("file:///android_asset/test2.html")){
		 		Data.setA("������Ŷ��");
		 		 nowUrl ="file:///android_asset/test.html";
		 		 mWebView.loadUrl("file:///android_asset/test.html");
		 	}else if(nowUrl.equals("file:///android_asset/test.html")){
		 		  startActivity(new Intent(HtmlMainActivity.this,MainActivity.class));
		 	}
			return;
	}
	
}
