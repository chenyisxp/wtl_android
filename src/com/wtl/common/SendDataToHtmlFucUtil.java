package com.wtl.common;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
/**
 *  工具类：
 *  用户可以传值给html5
 * @author chenyi
 *
 */

public class SendDataToHtmlFucUtil {
	/**
	 * 更新屏幕左上角连接状态
	 * @param connect_status
	 * @param mWebView
	 */
	//Android调用有返回值js方法
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void updateHtmlBleConnectStatus(String connect_status,WebView mWebView) {
		 
		    mWebView.evaluateJavascript("upConnectStatus("+connect_status+")", new ValueCallback<String>() {
		        @Override
		        public void onReceiveValue(String value) {
		        }
		    });

		
	}
	/**
	 * Android调用有返回值js方法
	 * @param connect_status
	 * 更新这个搜索出来的蓝牙
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void updateHtmlBleList(String bleList,WebView mWebView) {
		 
		    mWebView.evaluateJavascript("updateHtmlBleList("+bleList+")", new ValueCallback<String>() {
		    	//处理返回值
		    	@Override
		        public void onReceiveValue(String value) {
		        }
		    });

		
	}
	/**
	 * 
	 * 扫描按钮文字
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void updateHtmlBleScanBtnText(String text,WebView mWebView) {
		 
		    mWebView.evaluateJavascript("updateHtmlBleScanBtnText("+text+")", new ValueCallback<String>() {
		    	//处理返回值
		    	@Override
		        public void onReceiveValue(String value) {
		        }
		    });

		
	}
}
