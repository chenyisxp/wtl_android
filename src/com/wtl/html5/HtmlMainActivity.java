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
	    context =getApplicationContext();//上下文
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
	    webSettings.setDefaultTextEncodingName("gbk");//设置编码格式
	    mWebView.addJavascriptInterface(new JsInteration(), "android");
	    mWebView.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        	 Toast.makeText(HtmlMainActivity.this,Data.getA(),1).show();
	        	 nowUrl=url;
//	            if (url.equals("file:///android_asset/test2.html")) {
//	                Log.e(TAG, "shouldOverrideUrlLoading: " + url);
//	                startActivity(new Intent(SecondActivity.this,MainActivity.class));
//	                return true;//activity之间跳转
//	            } else {
	                mWebView.loadUrl(url);
	                return false;//是属于webview里的
//	            }
	        }
		    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
		        return onJsAlert(view, url, message, result);
		    }
	    });  
	    //配置完成 js弹窗等操作
	    mWebView.setWebChromeClient(new WebChromeClient());
	    //清除缓存
	    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

	}
	//Android调用有返回值js方法
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
	    //设置值 根据SharedPreferences 碎片存储
	    @JavascriptInterface
	    public String setSettingInfo(String key,String value) {
	    	return SaveUtil.updateDataBykey(context, key, value);
	    }
	    //获取设置值 根据SharedPreferences 碎片存储
	    @JavascriptInterface
	    public String getSettingInfo(String key) {
	        return  SaveUtil.getDataBykey(context,key);
	    }
	    //获取设置值 根据sql 碎片存储
	    @JavascriptInterface
	    public void testSqliteset() {
	    	// 获取数据库操作对象
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getWritableDatabase();
			//1、方法一
				// 创建sql语句
				//String sql = "insert into weldInfo(machinedId,weldType,dataTAype,setInfo,noteInfo,weldBeginTime,weldEndTime,weldConnectTime,memo,weldType)"
				//			+ "values('"+name+"','"+age+"')";
				// 执行sql语句
				//	sd.execSQL(sql);
				// 关闭数据库
				//db.close();
			//2、方法二
				 //实例化常量值
				 ContentValues cValue = new ContentValues();
				 cValue.put("machinedId","A0001");
				 cValue.put("machinedName","绿地金服一号机");
				 cValue.put("weldType","MMA");
				 cValue.put("dataType","0");
				 cValue.put("setInfo","{\"weldType\":\"MIG SYN\",\"options\":[{\"name\":\"MODE\",\"value\":\"21T\",\"unit\":\"T\"},{\"name\":\"MATERIAL\",\"value\":\"FE\"},{\"name\":\"GAS\",\"value\":\"Ar\"},{\"name\":\"DIAMETER\",\"value\":\"0.6mm\"},{\"name\":\"THICKNESS\",\"value\":\"0.6mm\"},{\"name\":\"INDUCTANCE\",\"value\":\"90\",\"unit\":\"A\"},{\"name\":\"SPEED\",\"value\":\"8\",\"unit\":\"BIG1\"}]}");
				 cValue.put("noteInfo","我的备注");
				 cValue.put("weldBeginTime","2018-11-09 12:20:11");
				 cValue.put("weldEndTime","2018-11-09 15:20:11");
				 cValue.put("weldConnectTime","3.7");
				 cValue.put("memo","新建备注");
				 cValue.put("rec_stat","1");
				 cValue.put("creator","admin");
				 cValue.put("modifier","admin");
				 Log.v("aaaa", DateUtil.getCurrentDateyyyyMMddHHmmss());
				 cValue.put("cre_tm",DateUtil.getCurrentDateyyyyMMddHHmmss());
				 cValue.put("up_tm",DateUtil.getCurrentDateyyyyMMddHHmmss());
				 //调用insert()方法插入数据
				 sd.insert("weldInfo", null, cValue);
				 db.close();
	    }
	    //获取设置值 根据sql 碎片存储
	    @JavascriptInterface
	    public String testSqliteget() {
	    	// 将所有的信息展示在文本框里
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			String sql = "select id,machinedId,machinedName,weldType,dataType,setInfo,noteInfo,weldBeginTime"
								+ ",weldEndTime,weldConnectTime,memo,rec_stat,creator,modifier,cre_tm,up_tm"
								+ " from weldInfo  ORDER BY cre_tm DESC";//根据时间排序
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
	    //获取设置值更更新
	    @JavascriptInterface
	    public String testSqliteupdate() {
	    	// 将所有的信息展示在文本框里
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			//实例化内容值
			ContentValues values = new ContentValues();  
			//在values中添加内容  
			values.put("setInfo","{'current':'10','valatge':'20'}");  
			//修改条件  
			String whereClause = "id=?";  
			//修改添加参数  
			String[] whereArgs={String.valueOf(1)};  
			//修改  
//			sd.update("weldInfo",values,whereClause,whereArgs);
			sd.update("weldInfo",values,null,null);  
			db.close();
			return s;
	    }
	    //获取设置值更更新
	    @JavascriptInterface
	    public String testSqlitedelete() {
	    	// 将所有的信息展示在文本框里
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
//	     if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
//	      
//	        return true;
//	    }
//
//	    return super.onKeyDown(keyCode, event);
//	}
	@Override
	public void onBackPressed() {
			// 这里处理逻辑代码，大家注意：该方法仅适用于2.0或更新版的sdk
		 	Toast.makeText(HtmlMainActivity.this,Data.getA(),1).show();
			Data.setA("问我我哦我");
		 	if(nowUrl.equals("file:///android_asset/test2.html")){
		 		Data.setA("问我我哦我");
		 		 nowUrl ="file:///android_asset/test.html";
		 		 mWebView.loadUrl("file:///android_asset/test.html");
		 	}else if(nowUrl.equals("file:///android_asset/test.html")){
		 		  startActivity(new Intent(HtmlMainActivity.this,MainActivity.class));
		 	}
			return;
	}
	
}
