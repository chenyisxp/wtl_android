package com.wtl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wtl.bean.BleListBean;
import com.wtl.bean.BleRespBean;
import com.wtl.bean.WeldInfo;
import com.wtl.common.DateUtil;
import com.wtl.common.SQLiteDBUtil;
import com.wtl.common.SaveUtil;
import com.wtl.constant.Constant;
import com.wtl.service.BluetoothLeService;
import com.wtl.ui.Ble_Activity;
import com.wtl.util.CRC16M;

/**
 * 特别说明：WTL_BLE助手是上海威特力焊接科技有限公司独自研发的手机APP，用于用户连接08蓝牙模块。
 * 本软件只能支持安卓版本4.3并且有蓝牙4.0的手机使用。

 * **/
/**
 * @Description: TODO<MainActivity类实现打开蓝牙、扫描蓝牙>
 * @author 上海威特力焊接科技有限公司
 * @data: 2018-10-16 上午10:28:18
 * @version: V1.0
 */
public class CopyOfMainActivity extends Activity{
	/**
	 * 1、校验数据的当前状态
	 * 2、正确性
	 * 3、页面来源
	 * 4、重发时用 时间戳
	 */
	private static HashMap<String, String> checkData=new HashMap<String, String>();
	private static HashMap<String, String> checkPage=new HashMap<String, String>();
	private static HashMap<String, Boolean> checkStatus=new HashMap<String, Boolean>();
	private static HashMap<String, Long> checkTime=new HashMap<String, Long>();
	//重发时间间隔
	private static long  sechelTime=1400;
	/**
	 * 定时器
	 */
	private static Timer timer = new Timer(); 
	private static TimerTask task ;
	
	// 扫描蓝牙按钮
//	private Button scan_btn;
	private Button test_btn;
	public static  String scan_btn_html="startScan";
	public static  String bleRespInfo="empty";
	// 蓝牙适配器
	BluetoothAdapter mBluetoothAdapter;
	// 蓝牙信号强度
	private ArrayList<Integer> rssis;
	// 自定义Adapter
//	LeDeviceListAdapter mleDeviceListAdapter;
	// listview显示扫描到的蓝牙信息
//	ListView lv;
	// 描述扫描蓝牙的状态
	private boolean mScanning;
	private boolean scan_flag;
//	private Handler mHandler;
	int REQUEST_ENABLE_BT = 1;
	// 蓝牙扫描时间
	private static final long SCAN_PERIOD = 10000;
	private ArrayAdapter<String> aa;
	
	/**********************************/
	private static String nowUrl = "";
	private WebView mWebView;
	private static Context context;
//	public MainActivity(Context c) {
//        this.context = c;
//    }

	private final static String TAG = Ble_Activity.class.getSimpleName();
	// 蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_RESP = "DEVICE_RESP";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	// 蓝牙连接状态
	private boolean mConnected = false;
	private String status = "disconnected";
	// 蓝牙名字
	private String mDeviceName;
	// 蓝牙地址
	private String mDeviceAddress;
	// 蓝牙信号值
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	private String respData;
	// 蓝牙service,负责后台的蓝牙服务
	private static BluetoothLeService mBluetoothLeService;
	// 文本框，显示接受的内容
	private TextView send_tv, connect_state, resp_bit_data, preCurrentNum,
			realCurrentNum, preVoltageNum, realVoltageNum;
	private String connect_status;// 连接状态
	// 发送按钮
	private Button send_btn;
	// 文本编辑框
	private EditText send_current;
	private EditText send_voltage;
	// private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	// 字符串常量
	private String tempStr1 = "";
	private String tempStr2 = "";
	public JsInteration jsInteration = new JsInteration(){
		
	};
	//蓝牙集合 map 去除重复的
	private Map<String,BleListBean> bleList =new HashMap<String,BleListBean>();
	// 蓝牙特征值
	private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler() {
		// 2.重写消息处理函数
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 判断发送的消息
			case 1: {
				// 更新View
				// String state = msg.getData().getString("connect_state");
				// connect_state.setText(state);
				connect_status = msg.getData().getString("connect_state");
				// 状态传递给 html5页面
//				SendDataToHtmlFucUtil.updateHtmlBleConnectStatus(connect_status, mWebView);
				break;
			}

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		//初始化隐藏按钮
		test_btn = (Button) this.findViewById(R.id.scan_dev_btn);
		// 初始化蓝牙
		init_ble();
		scan_flag = true;
		
		/* 启动蓝牙service */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
			//初始化定时器
			initTimer();
			//webview
		   mWebView = (WebView) findViewById(R.id.webView);
		    mWebView.loadUrl("http://192.168.1.3:8082/#/");
//		   mWebView.loadUrl("file:///android_asset/index.html");
		    
		    WebSettings webSettings = mWebView.getSettings();
		    webSettings.setJavaScriptEnabled(true);
		    webSettings.setDefaultTextEncodingName("gbk");//设置编码格式
		    mWebView.addJavascriptInterface(jsInteration, "android");
		    mWebView.setWebViewClient(new WebViewClient() {
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        	 nowUrl=url;
		                mWebView.loadUrl(url);
		                return false;//是属于webview里的
//		            }
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
	private static void initTimer(){
		task = new TimerTask() { 
		    @Override 
		    public void run() {
		    	Boolean shutDownFlag =false;//终止的标识
		    	//检查是否需要重发
		    	for(String crcCode :checkStatus.keySet()){
		    		if(!checkStatus.get(crcCode)){
		    			shutDownFlag =true;
		    			//是否大于四百ms
		    			
		    			if((new Date().getTime() -checkTime.get(crcCode))>sechelTime){
		    				//更新时间戳
		    				checkTime.put(crcCode,new Date().getTime());
		    				//重发给蓝牙消息
		    				String regex = "(.{2})";
		    				String data = checkData.get(crcCode).replaceAll (regex, "$1 ");
		    				target_chara.setValue(HexCommandtoByte(data.getBytes()));
		    				mBluetoothLeService.writeCharacteristic(target_chara);
		    			}
		    		}
		    	}
		    	if(!shutDownFlag){
		    		//没有未完成的任务 停止
		    		task.cancel();
		    	}
		    } 
		}; 
		//需要新建
		timer =new Timer();
		timer.schedule(task, sechelTime, sechelTime);
	}
	private void onCreate_old() {
		setContentView(R.layout.activity_main);
		// 初始化控件
		// init();
		// 初始化蓝牙
		init_ble();
		scan_flag = true;
	}

	/**
	 * @Title: init
	 * @Description: TODO(初始化UI控件)
	 * @param 无
	 * @return void
	 * @throws
	 */
//	private void init() {
//		scan_btn = (Button) this.findViewById(R.id.scan_dev_btn);
//		scan_btn.setOnClickListener(this);
//		lv = (ListView) this.findViewById(R.id.lv);
//		mHandler = new Handler();
//	}

	/**
	 * @Title: init_ble
	 * @Description: TODO(初始化蓝牙)
	 * @param 无
	 * @return void
	 * @throws
	 */
	private void init_ble() {
		// 手机硬件支持蓝牙
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
			finish();
		}
		// Initializes Bluetooth adapter.
		// 获取手机本地的蓝牙适配器
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 打开蓝牙权限
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

	}

	/*
	 * 点击开始扫描
	 * to html5
	 */
	private void clickScan() {
		// TODO Auto-generated method stub

		if (scan_flag) {
//			mleDeviceListAdapter = new LeDeviceListAdapter();
//			lv.setAdapter(mleDeviceListAdapter);
			scanLeDevice(true);
		} else {

			scanLeDevice(false);
//			scan_btn.setText("扫描设备");
			scan_btn_html ="startScan";
//			SendDataToHtmlFucUtil.updateHtmlBleScanBtnText(scan_btn_html, mWebView);
		}
	}

	/**
	 * @Title: scanLeDevice
	 * @Description: TODO(扫描蓝牙设备 )
	 * @param enable
	 *            (扫描使能，true:扫描开始,false:扫描停止)
	 * @return void
	 * @throws
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mhandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					scan_flag = true;
//					scan_btn.setText("扫描设备");
					scan_btn_html ="startScan";
//					SendDataToHtmlFucUtil.updateHtmlBleScanBtnText(scan_btn_html, mWebView);
					Log.i("SCAN", "stop.....................");
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
			Log.i("SCAN", "begin.....................");
			mScanning = true;
			scan_flag = false;
//			scan_btn.setText("停止扫描");
			scan_btn_html="scaning";
//			SendDataToHtmlFucUtil.updateHtmlBleScanBtnText(scan_btn_html, mWebView);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			Log.i("Stop", "stoping................");
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			scan_flag = true;
		}

	}

	/**
	 * 蓝牙扫描回调函数 实现扫描蓝牙设备，回调蓝牙BluetoothDevice，可以获取name MAC等信息
	 * 
	 * **/
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			// TODO Auto-generated method stub

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/* 讲扫描到设备的信息输出到listview的适配器 */
					//TODO 
					BleListBean bean =new BleListBean();
					bean.setAddress("\\\""+device.getAddress()+"\\\"");
					bean.setBleName(device.getName());
					bean.setRssi(rssi+"");
					bleList.put(bean.getAddress(), bean);
//					SendDataToHtmlFucUtil.updateHtmlBleList(bean.toString(), mWebView);
//					mleDeviceListAdapter.addDevice(device, rssi);
//					mleDeviceListAdapter.notifyDataSetChanged();
				}
			});

			System.out.println("Address:" + device.getAddress());
			System.out.println("Name:" + device.getName());
			System.out.println("rssi:" + rssi);

		}
	};

	/**
	 * @Description: TODO<自定义适配器Adapter,作为listview的适配器>
	 * @author 广州汇承信息科技有限公司
	 * @data: 2014-10-12 上午10:46:30
	 * @version: V1.0
	 */
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;

		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			rssis = new ArrayList<Integer>();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device, int rssi) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
				rssis.add(rssi);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
			rssis.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		/**
		 * 重写getview
		 * 
		 * **/
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {

			// General ListView optimization code.
			// 加载listview每一项的视图
			view = mInflator.inflate(R.layout.listitem, null);
			// 初始化三个textview显示蓝牙信息
			TextView deviceAddress = (TextView) view
					.findViewById(R.id.tv_deviceAddr);
			TextView deviceName = (TextView) view
					.findViewById(R.id.tv_deviceName);
			TextView rssi = (TextView) view.findViewById(R.id.tv_rssi);

			BluetoothDevice device = mLeDevices.get(i);
			deviceAddress.setText(device.getAddress());
			deviceName.setText(device.getName());
			rssi.setText("" + rssis.get(i));

			return view;
		}
	}
	  //使用Webview的 时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if(keyCode==KeyEvent.KEYCODE_BACK&&mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	/***************************** 来自ble_activitybegig ****************************************/
	/************************************ begin *************************************/

//	private Handler mhandler = new Handler();


	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 解除广播接收器
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
	}

	// Activity出来时候，绑定广播接收器，监听蓝牙连接服务传过来的事件
//	@Override
//	protected void onResume() {
//		super.onResume();
//		// 绑定广播接收器
//		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//		if (mBluetoothLeService != null) {
//			// 根据蓝牙地址，建立连接
//			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//			Log.d(TAG, "Connect request result=" + result);
//		}
//	}
	
	private void connectBle(){
		// 绑定广播接收器
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			if (mBluetoothLeService != null) {
				// 根据蓝牙地址，建立连接
				final boolean result = mBluetoothLeService.connect(mDeviceAddress);
				Log.d(TAG, "Connect request result=" + result);
			}
	}

	/**
	 * 报文数据解析函数
	 * 
	 * @param respData
	 * @return
	 */
	private BleRespBean buildCurrentVoltageData(String respData) {
		BleRespBean respInfo =new BleRespBean();
		respData = respData.replaceAll(" ", "");
		if (respData.isEmpty()) {
			respInfo.setDataStatus(Constant.EMPTY_CODE);
		} else if (respData.length() >= 12) {
			try {
				respInfo.setDataStatus(Constant.SUC_CODE);
				respInfo.setPreCurrentNum(Integer.parseInt(respData.subSequence(2, 4)
						+ "", 16)
						+ "A");
				respInfo.setRealCurrentNum(Integer.parseInt(respData.subSequence(4, 6)
						+ "", 16)
						+ "A");
				int aa = Integer.parseInt(respData.subSequence(6, 10) + "", 16);
				respInfo.setPreVoltageNum(aa / 10 + "." + aa % 10 + "V");
				int bb = Integer.parseInt(respData.subSequence(10, 14) + "", 16);
				respInfo.setRealVoltageNum(bb / 10 + "." + bb % 10 + "V");
			} catch (Exception e) {
				respInfo.setDataStatus(Constant.ERROR_CODE);
			}
		}else{
			respInfo.setDataStatus(Constant.EMPTY_CODE);
		}
		return respInfo;
	}

	/* BluetoothLeService绑定的回调函数 */
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			// 根据蓝牙地址，连接设备
			mBluetoothLeService.connect(mDeviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}

	};

	/**
	 * 广播接收器，负责接收BluetoothLeService类发送的数据
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt连接成功
			{
				mConnected = true;
				status = "connected";
				// 更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED// Gatt连接失败
					.equals(action)) {
				mConnected = false;
				status = "disconnected";
				// 更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED// 发现GATT服务器
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				// 获取设备的所有蓝牙服务
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))// 有效数据
			{
				// 处理发送过来的数据
//				buildCurrentVoltageData(intent.getExtras().getString(
//						BluetoothLeService.EXTRA_DATA));
				bleRespInfo=intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA);
				if(!"00".equals(bleRespInfo.replaceAll(" ", ""))){
					commonBleRespData(bleRespInfo.replaceAll(" ", ""));
				}
				
			}
		}
	};
	//通用蓝牙返回数据处理格式
	private void commonBleRespData(final String data){
		//数据校验
		if(data.isEmpty()){
			return;
			//最小长度为7 ：帧头+指令+crc校验
		}else if(data.length()>7 && data.startsWith("DA")){
			//1、截取数据进行切割校验   最后四位 作为crc校验值    查看是否正确
			String midData ="";
			if(data.length()==8){
				//结束 没有数据字段
				midData="";
			}else{
				midData =data.substring(4, data.length()-4);
			}
			
			final String crc  =data.substring(data.length()-4, data.length());
			if(checkData.get(crc)==""||checkData.get(crc)==null){
				//异常 crc错了啊
//				return;
			//把数据转成crc 校验是否能取到职值
				if(midData!=""){
					if(checkData.get(CRC16M.getCRC3(HexCommandtoByte(midData.getBytes()))).isEmpty()){
						//错乱的数据
						return;
					}else{
						//TODO 重发
					}
				}
			}else if(!checkData.get(crc).equals(data)){
				//异常 
				//TODO 重发
				return;
			}else{
				//1、正确关闭 定时器
				timer.cancel();
				timer = null;
				//2、发送
				mWebView.post(new Runnable() {
				    @Override
				    public void run() {
				    	mWebView.loadUrl("javascript:broastFromAndroid('" + data + "','"+checkPage.get(crc)+"')");
				    	checkData.remove(crc);
						checkPage.remove(crc);
						checkStatus.remove(crc);
						checkTime.remove(crc);
				}});
				//3、清空数据
				
			}
			
		}
		
	}
	/* 更新连接状态 */
	private void updateConnectionState(String status) {
//		SendDataToHtmlFucUtil.updateHtmlBleConnectStatus(status, mWebView);
//		Message msg = new Message();
//		msg.what = 1;
//		Bundle b = new Bundle();
//		b.putString("connect_state", status);
//		msg.setData(b);
		// 将连接状态更新的UI的textview上
//		myHandler.sendMessage(msg);
//		System.out.println("connect_state:" + status);

	}

	/* 意图过滤器 */
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/**
	 * @Title: displayGattServices
	 * @Description: TODO(处理蓝牙服务)
	 * @param 无
	 * @return void
	 * @throws
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices) {

		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "unknown_service";
		String unknownCharaString = "unknown_characteristic";

		// 服务数据,可扩展下拉列表的第一级数据
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

		// 特征数据（隶属于某一级服务下面的特征值集合）
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

		// 部分层次，所有特征值集合
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {

			// 获取服务列表
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();

			// 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。

			gattServiceData.add(currentServiceData);

			System.out.println("Service uuid:" + uuid);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

			// 从当前循环所指向的服务中读取特征值列表
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			// 对于当前循环所指向的服务中的每一个特征值
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT)) {
					// 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
					mhandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mBluetoothLeService
									.readCharacteristic(gattCharacteristic);
						}
					}, 200);

					// 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
					mBluetoothLeService.setCharacteristicNotification(
							gattCharacteristic, true);
					target_chara = gattCharacteristic;
					// 设置数据内容
					// 往蓝牙模块写入数据
					// mBluetoothLeService.writeCharacteristic(gattCharacteristic);
				}
				List<BluetoothGattDescriptor> descriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor descriptor : descriptors) {
					System.out.println("---descriptor UUID:"
							+ descriptor.getUuid());
					// 获取特征值的描述
					mBluetoothLeService.getCharacteristicDescriptor(descriptor);
					// mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
					// true);
				}

				gattCharacteristicGroupData.add(currentCharaData);
			}
			// 按先后顺序，分层次放入特征值集合中，只有特征值
			mGattCharacteristics.add(charas);
			// 构件第二级扩展列表（服务下面的特征值）
			gattCharacteristicData.add(gattCharacteristicGroupData);

		}

	}

	// 新规则
	private String tenTohex(String tempC, String tempV) {
		// 举例：
		int tempA = 0;
		if (tempV.indexOf(".") > 0) {
			String[] tsr = tempV.split("\\.");
			tempV = tsr[0] + tsr[1];
			tempA = Integer.valueOf(tempV);
		} else {
			tempA = Integer.valueOf(tempV).intValue() * 10;
		}
		if (Integer.valueOf(tempC).intValue() < 16) {
			tempStr1 = "0"
					+ Integer.toHexString(Integer.valueOf(tempC).intValue());
		} else {
			tempStr1 = Integer.toHexString(Integer.valueOf(tempC).intValue());
		}
		if (tempA < 16) {
			tempStr2 = "000" + Integer.toHexString(tempA);
		} else if (tempA < 255) {
			tempStr2 = "00" + Integer.toHexString(tempA);
		} else if (tempA < 4095) {
			tempStr2 = "0" + Integer.toHexString(tempA);
		} else {
			tempStr2 = Integer.toHexString(tempA);
		}

		return tempStr1 + " " + tempStr2.substring(0, 2) + " "
				+ tempStr2.substring(2, 4);
	}
	// 十六进制的字符串转换成byte数组
		public static byte[] HexCommandtoByte(byte[] data) {
				if (data == null) {
					return null;
				}
				int nLength = data.length;

				String strTemString = new String(data, 0, nLength);
				String[] strings = strTemString.split(" ");
				nLength = strings.length;
				data = new byte[nLength];
				for (int i = 0; i < nLength; i++) {
					if (strings[i].length() != 2) {
						data[i] = 00;
						continue;
					}
					try {
						data[i] = (byte) Integer.parseInt(strings[i], 16);
					} catch (Exception e) {
						data[i] = 00;
						continue;
					}
				}

				return data;
			}
	public class JsInteration {
		
		/**
		 * come from html to ble
		 * @return
		 */
		@JavascriptInterface
		public void callSendDataToBle(String pageFrom,String data,String crcCode) {
			//0、初始化 开始定时器
			CopyOfMainActivity.requestFromHtmlInit(pageFrom, data, crcCode);
				//java.util.Timer.schedule(TimerTask task, long delay, long period)：这个方法是说，delay/1000秒后执行task,
				//然后进过period/1000秒再次执行task，这个用于循环任务，执行无数次，当然，你可以用timer.cancel();取消计时器的执行。
			initTimer();
			//1、改写直接 发字符串
			String regex = "(.{2})";
			data = data.replaceAll (regex, "$1 ");
			target_chara.setValue(HexCommandtoByte(data.getBytes()));
			mBluetoothLeService.writeCharacteristic(target_chara);
		}
		/**
		 * give data
		 * @return
		 */
		@JavascriptInterface
		public void callGiveMeData(String type) {
			String sendData="";
			if("0xE1".equals(type)){
				sendData ="DA E1 F8 C1";
			}else{
				//不对结束
				return;
			}
			byte[] buff = null;
			buff = HexCommandtoByte(sendData.getBytes());
			//2、改写直接 发字符串
			target_chara.setValue(buff);
			mBluetoothLeService.writeCharacteristic(target_chara);
		}
		/**
		 * 切断 蓝牙连接
		 * @return
		 */
		@JavascriptInterface
		public String closeBleConnect() {
			//将连接状态置为未连接
			status="disconnected";
			return Constant.SUC_CODE;
		}
		/**
		 * 蓝牙 返回的数据
		 * @return
		 */
		@JavascriptInterface
		public String getBleRespMsg() {
			BleRespBean data =new BleRespBean();
			if(bleRespInfo.isEmpty() ||bleRespInfo=="00" ||bleRespInfo=="empty"){
				//取测试值 E1
				//帧头(oxDA)    命令（一个字节）	数据	校验（命令+数据）
				//DA E1 00 00 00 00 
				//
				bleRespInfo="1111 6666";
			}
			 data = buildCurrentVoltageData(bleRespInfo);
			return data.toString();
		}
		/**
		 * getready 开始焊接
		 */
		@JavascriptInterface
		public String getReadyGo() {
			//1、判断是否连接了，且返回数据了
//			if(!mConnected){
//				return "请先连接上蓝牙";
//			}
//			else if(resp_bit_data.getText().toString().equals(Constant.EMPTY_TEXT)){
//				Toast.makeText(Ble_Activity.this, "当前蓝牙未返回数据", Toast.LENGTH_LONG)
//				.show();
//				return ;
//			}
			//2、去除预置电压和预置电流值
			String tempV = "10";
			String tempC ="20";
			//3、判断用户是否输入 begin 16进制开始--如果用户没输入取之前单片机返回的数据
			if(tempC.isEmpty()){
//				tempC =preCurrentNum.getText().toString().replaceAll("A", "");
			}else{
//				if(Integer.valueOf(tempC)<5){
//					Toast.makeText(Ble_Activity.this, "预置电流不能小于5V", Toast.LENGTH_LONG)
//					.show();
//					return;
//				}
			}
			if(tempV.isEmpty()){
//				tempV =preVoltageNum.getText().toString().replaceAll("V", "");
			}
			String tenTohex ="ee " +tenTohex(tempC,tempV)+" ff ff";
//			String tenTohex ="EE 1F 00 A4 FF FF";
			byte[] buff = null;
			buff = HexCommandtoByte(tenTohex.getBytes());
//			end
			//2、改写直接 发字符串
//			target_chara.setValue(tenTohex);
			target_chara.setValue(buff);
			mBluetoothLeService.writeCharacteristic(target_chara);
			return "success";
		}
		
		/**
		 *点击连接蓝牙
		 * @return
		 */
		@JavascriptInterface
		public void setBleConnect(String address) {
			if (mScanning)
			{
				/* 停止扫描设备 */
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
			}
			//启动蓝牙 原来ble_activity的逻辑
			/* 启动蓝牙service */
//			Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//			mDeviceAddress="78:04:73:00:AC:3D";
			mDeviceAddress=address.replaceAll("\"", "");
			connectBle();
		}
		/**
		 *获取 ble蓝牙返回的list
		 * @return
		 */
		@JavascriptInterface
		public String getBleList() {
			List<BleListBean> list =new ArrayList<BleListBean>();
			for(String key:bleList.keySet()){
				BleListBean bean = new BleListBean();
				bean.setAddress(key);
				bean.setBleName(bleList.get(key).getBleName());
				bean.setRssi(bleList.get(key).getRssi());
				list.add(bean);
			}
			return list.toString();
			
		}
		/**
		 *获取 扫描按钮 文字
		 * @return
		 */
		@JavascriptInterface
		public String getScanBtnText() {
			return scan_btn_html;
			
		}
		
		/**
		 * 开始扫描
		 * @return
		 */
		@JavascriptInterface
		public void beginScan() {
			clickScan();
	
//			test_btn.performClick();
//			mWebView.post(new Runnable() {
//			    @Override
//			    public void run() {
//			        
//			    	mWebView.loadUrl("javascript:aa('" + 11 + "')");
//			}});
			
			
//			updateHtmlBleScanBtnText("测试放回", mWebView);
//			 Handler mainHandler = new Handler(Looper.getMainLooper());
//			    mainHandler.post(new Runnable() {
//			        @Override
//			        public void run() {
			            //已在主线程中，可以更新UI
//			        	updateHtmlBleScanBtnText(11+"");
//			 mWebView.evaluateJavascript("aa('"+1+"')", new ValueCallback<String>() {
  		    	//处理返回值
//  		    	@Override
//  		        public void onReceiveValue(String value) {
//  		        }
//  		    });
//			        }
//			    });
			    
//			 ((MainActivity) context).runOnUiThread(new Runnable() {
//		            @Override
//		            public void run() {
//		                //此时已在主线程中，可以更新UI了
//		            	mWebView.loadUrl("javascript:aa("+"'"+11+"'"+")");
//		            }
//		        });
			    
//			mhandler.post(new Runnable() {
//	            @Override
//	            public void run() {
//				mWebView.loadUrl("javascript:aa("+"'"+11+"'"+")");
//	            }
//	          });
			
		}
		/**
		 * 获取连接状态
		 * @return
		 */
		@JavascriptInterface
		public String getConStatus() {
			// return "hello world";
//			return connect_status;
			return status;
		}

		@JavascriptInterface
		public String back() {
			// return "hello world";
			return getResources().getString(R.string.hello_world);
		}

		@JavascriptInterface
		public String getDate() {
			return "20:29:20";
		}

		// 设置值 根据SharedPreferences 碎片存储
		@JavascriptInterface
		public String setSettingInfo(String key, String value) {
			return SaveUtil.updateDataBykey(context, key, value);
		}

		// 获取设置值 根据SharedPreferences 碎片存储
		@JavascriptInterface
		public String getSettingInfo(String key) {
			return SaveUtil.getDataBykey(context, key);
		}

		// 获取设置值 根据sql 碎片存储
		@JavascriptInterface
		public void testSqliteset() {
			// 获取数据库操作对象
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getWritableDatabase();
			// 1、方法一
			// 创建sql语句
			// String sql =
			// "insert into weldInfo(machinedId,weldType,dataTAype,setInfo,noteInfo,weldBeginTime,weldEndTime,weldConnectTime,memo,weldType)"
			// + "values('"+name+"','"+age+"')";
			// 执行sql语句
			// sd.execSQL(sql);
			// 关闭数据库
			// db.close();
			// 2、方法二
			// 实例化常量值
			ContentValues cValue = new ContentValues();
			cValue.put("machinedId", "A0001");
			cValue.put("machinedName", "绿地金服一号机");
			cValue.put("weldType", "MMA");
			cValue.put("dataType", "0");
			cValue.put(
					"setInfo",
					"{\"weldType\":\"MIG SYN\",\"options\":[{\"name\":\"MODE\",\"value\":\"21T\",\"unit\":\"T\"},{\"name\":\"MATERIAL\",\"value\":\"FE\"},{\"name\":\"GAS\",\"value\":\"Ar\"},{\"name\":\"DIAMETER\",\"value\":\"0.6mm\"},{\"name\":\"THICKNESS\",\"value\":\"0.6mm\"},{\"name\":\"INDUCTANCE\",\"value\":\"90\",\"unit\":\"A\"},{\"name\":\"SPEED\",\"value\":\"8\",\"unit\":\"BIG1\"}]}");
			cValue.put("noteInfo", "我的备注");
			cValue.put("weldBeginTime", "2018-11-09 12:20:11");
			cValue.put("weldEndTime", "2018-11-09 15:20:11");
			cValue.put("weldConnectTime", "3.7");
			cValue.put("memo", "新建备注");
			cValue.put("rec_stat", "1");
			cValue.put("creator", "admin");
			cValue.put("modifier", "admin");
			Log.v("aaaa", DateUtil.getCurrentDateyyyyMMddHHmmss());
			cValue.put("cre_tm", DateUtil.getCurrentDateyyyyMMddHHmmss());
			cValue.put("up_tm", DateUtil.getCurrentDateyyyyMMddHHmmss());
			// 调用insert()方法插入数据
			sd.insert("weldInfo", null, cValue);
			db.close();
		}

		// 获取设置值 根据sql 碎片存储
		@JavascriptInterface
		public String testSqliteget() {
			// 将所有的信息展示在文本框里
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			String sql = "select id,machinedId,machinedName,weldType,dataType,setInfo,noteInfo,weldBeginTime"
					+ ",weldEndTime,weldConnectTime,memo,rec_stat,creator,modifier,cre_tm,up_tm"
					+ " from weldInfo  ORDER BY cre_tm DESC";// 根据时间排序
			Cursor cursor = sd.rawQuery(sql, null);
			WeldInfo weldInfo = new WeldInfo();
			List<WeldInfo> wldlist = new ArrayList<WeldInfo>();
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
				// s += "id:" + cursor.getInt(0) + "\tmachinedId:" +
				// cursor.getString(1)
				// + "\t machinedName:" + cursor.getString(2) + "\t setInfo:" +
				// cursor.getString(5) + "\n";
				// s+=weldInfo.toString();
				wldlist.add(weldInfo);
			}
			Log.w("aaaaa", s);
			db.close();
			cursor.close();
			return wldlist.toString();
		}

		// 获取设置值更更新
		@JavascriptInterface
		public String testSqliteupdate() {
			// 将所有的信息展示在文本框里
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			// 实例化内容值
			ContentValues values = new ContentValues();
			// 在values中添加内容
			values.put("setInfo", "{'current':'10','valatge':'20'}");
			// 修改条件
			String whereClause = "id=?";
			// 修改添加参数
			String[] whereArgs = { String.valueOf(1) };
			// 修改
			// sd.update("weldInfo",values,whereClause,whereArgs);
			sd.update("weldInfo", values, null, null);
			db.close();
			return s;
		}

		// 获取设置值更更新
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
	// Android调用有返回值js方法
//	@TargetApi(Build.VERSION_CODES.KITKAT)
//	public void updateHtmlBleConnectStatus(String connect_status) {
//
//		mWebView.evaluateJavascript("upConnectStatus(" + connect_status + ")",
//				new ValueCallback<String>() {
//					@Override
//					public void onReceiveValue(String value) {
//						Log.e(TAG, "onReceiveValue value=" + value);
//					}
//				});
//
//	}
	/**
	 * 
	 * 扫描按钮文字
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public  void updateHtmlBleScanBtnText(String text) {
		 
//		mWebView.post(new Runnable() {
//            @Override
//            public void run() {
            	 mWebView.evaluateJavascript("aa('"+1+"')", new ValueCallback<String>() {
     		    	//处理返回值
     		    	@Override
     		        public void onReceiveValue(String value) {
     		        }
     		    });
//            }
//        });
		   
	}
	/***************************** 来自ble_activitybegig ****************************************/
	/************************************ begin *************************************/
	/********************************************************************************/
	private static void requestFromHtmlInit(String pageFrom,String data,String crcCode){
		//当前页面 有值了返回
		if(pageFrom.equals(checkPage.get(crcCode))){
			return;
		}
		checkPage.put(crcCode, pageFrom);//页面来源
		checkStatus.put(crcCode, false);//默认请求还未成功
		checkData.put(crcCode, data);//要发送的数据
		checkTime.put(crcCode, new Date().getTime());//时间戳
	}
	private void reponseFromBleInit(String crcCode){
		// 有值 返回了
		if(!checkPage.get(crcCode).isEmpty()){
			checkStatus.put(crcCode, true);//成功 不需要重发
		}
	}
}
