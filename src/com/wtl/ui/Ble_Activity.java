package com.wtl.ui;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wtl.MainActivity;
import com.wtl.R;
import com.wtl.SplashActivity;
import com.wtl.TestSecondConnectActivity;
import com.wtl.bean.WeldInfo;
import com.wtl.common.Data;
import com.wtl.common.DateUtil;
import com.wtl.common.SQLiteDBUtil;
import com.wtl.common.SaveUtil;
import com.wtl.constant.Constant;
import com.wtl.filter.EditInputContainFilter;
import com.wtl.filter.EditInputFilter;
import com.wtl.html5.HtmlMainActivity;
import com.wtl.html5.HtmlMainActivity.JsInteration;
import com.wtl.service.BluetoothLeService;

/**
 * 特别说明：WTL_BLE助手是上海威特力焊接科技有限公司独自研发的手机APP。
 * **/

/** 
 * @Description:  TODO<Ble_Activity实现连接BLE,发送和接受BLE的数据> 
 * @author  上海威特力焊接科技有限公司
 * @data:  2014-10-20 下午12:12:04 
 * @version:  V1.0 
 */ 
public class Ble_Activity extends Activity{

	private static String nowUrl="";
	private WebView mWebView;
	private static Context context;
	
	
	private final static String TAG = Ble_Activity.class.getSimpleName();
	//蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_RESP = "DEVICE_RESP";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	//蓝牙连接状态
	private boolean mConnected = false;
	private String status = "disconnected";
	//蓝牙名字
	private String mDeviceName;
	//蓝牙地址
	private String mDeviceAddress;
	//蓝牙信号值
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	private String respData;
	//蓝牙service,负责后台的蓝牙服务
	private static BluetoothLeService mBluetoothLeService;
	//文本框，显示接受的内容
	private TextView send_tv,connect_state,resp_bit_data,preCurrentNum,realCurrentNum,preVoltageNum,realVoltageNum;
	private String connect_status;//连接状态
	//发送按钮
	private Button send_btn;
	//文本编辑框
	private EditText send_current;
	private EditText send_voltage;
//	private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//字符串常量
	private String tempStr1 = "";
	private String tempStr2 = "";
	//蓝牙特征值
	private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler()
	{
		// 2.重写消息处理函数
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 判断发送的消息
			case 1:
			{
				// 更新View
//				String state = msg.getData().getString("connect_state");
//				connect_state.setText(state);
				connect_status = msg.getData().getString("connect_state");
				//状态传递给 html5页面
				updateHtmlBleConnectStatus(connect_status);
				break;
			}

			}
			super.handleMessage(msg);
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		b = getIntent().getExtras();
		//从意图获取显示的蓝牙信息
		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
		
		/* 启动蓝牙service */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		
		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
		respData=b.getString(EXTRAS_DEVICE_RESP);
		
		//webview
		   mWebView = (WebView) findViewById(R.id.webView);
//		    mWebView.loadDataWithBaseURL(null,"file:///android_asset/test.html", "text/html", "gbk",null);

//		    mWebView.loadUrl("file:///android_asset/JavaScript.html");
//		    mWebView.loadUrl("file:///android_asset/test2.html");
//		    mWebView.loadUrl("file:///android_asset/test.html");
//		    mWebView.loadUrl("file:///android_asset/testJrange.html");
//		    mWebView.loadUrl("file:///android_asset/index.html");
		    mWebView.loadUrl("http://192.168.1.5:8081/#/");
		    
		    WebSettings webSettings = mWebView.getSettings();
		    webSettings.setJavaScriptEnabled(true);
		    webSettings.setDefaultTextEncodingName("gbk");//设置编码格式
		    mWebView.addJavascriptInterface(new JsInteration(), "android");
		    mWebView.setWebViewClient(new WebViewClient() {
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        	 Toast.makeText(Ble_Activity.this,Data.getA(),1).show();
		        	 nowUrl=url;
//		            if (url.equals("file:///android_asset/test2.html")) {
//		                Log.e(TAG, "shouldOverrideUrlLoading: " + url);
//		                startActivity(new Intent(SecondActivity.this,MainActivity.class));
//		                return true;//activity之间跳转
//		            } else {
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
	//Android调用有返回值js方法
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void updateHtmlBleConnectStatus(String connect_status) {
		 
		    mWebView.evaluateJavascript("upConnectStatus("+connect_status+")", new ValueCallback<String>() {
		        @Override
		        public void onReceiveValue(String value) {
		            Log.e(TAG, "onReceiveValue value=" + value);
		        }
		    });

		
	}
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.ble_activity);
//		b = getIntent().getExtras();
//		Log.v("bbbb", b.toString());
//		//从意图获取显示的蓝牙信息
//		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
//		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
//		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
//		
//		/* 启动蓝牙service */
//		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//		
//		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
//		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
//		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
//		respData=b.getString(EXTRAS_DEVICE_RESP);
//		init();
//
//	}
	/**
	 * 初始化webview
	 */

	private void htmlInit(){
		
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
        //解除广播接收器
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
	}

	// Activity出来时候，绑定广播接收器，监听蓝牙连接服务传过来的事件
	@Override
	protected void onResume()
	{
		super.onResume();
		//绑定广播接收器
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null)
		{    
			//根据蓝牙地址，建立连接
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	/** 
	* @Title: init 
	* @Description: TODO(初始化UI控件) 
	* @param  无
	* @return void    
	* @throws 
	*/ 
	private void init()
	{
		send_tv = (TextView) this.findViewById(R.id.send_tv);
		connect_state = (TextView) this.findViewById(R.id.connect_state);
		send_btn = (Button) this.findViewById(R.id.send_btn);
		//修改文本框
		send_current = (EditText) this.findViewById(R.id.send_current);
			InputFilter[] filters1 = { new EditInputContainFilter() };  
			send_current.setFilters(filters1);
		send_voltage = (EditText) this.findViewById(R.id.send_voltage);
			InputFilter[] filters2 = { new EditInputFilter() };  
			send_voltage.setFilters(filters2);
		connect_state.setText(status);

		//请求的原始数据
		resp_bit_data = (TextView) this.findViewById(R.id.resp_bit_data);
		resp_bit_data.setText(respData);
		preCurrentNum = (TextView) this.findViewById(R.id.preCurrentNum);
		realCurrentNum = (TextView) this.findViewById(R.id.realCurrentNum);
		preVoltageNum = (TextView) this.findViewById(R.id.preVoltageNum);
		realVoltageNum = (TextView) this.findViewById(R.id.realVoltageNum);
		//改装字符串拆解及组装显示
		buildCurrentVoltageData(respData);
//		send_btn.setOnClickListener(this);

	}
	/**
	 * 报文数据解析函数
	 * @param respData
	 * @return
	 */
	private void buildCurrentVoltageData(String respData){
		respData =respData.replaceAll(" ", "");
		if(respData.isEmpty()){
			resp_bit_data.setText(Constant.EMPTY_TEXT);
			preCurrentNum.setText(Constant.EMPTY_TEXT);
			realCurrentNum.setText(Constant.EMPTY_TEXT);
			preVoltageNum.setText(Constant.EMPTY_TEXT);
			realVoltageNum.setText(Constant.EMPTY_TEXT);
		}else if(respData.length()>=14){
			resp_bit_data.setText(respData);
			preCurrentNum.setText(Integer.parseInt(respData.subSequence(2, 4)+"",16)+"A");
			realCurrentNum.setText(Integer.parseInt(respData.subSequence(4, 6)+"",16)+"A");
			int aa=Integer.parseInt(respData.subSequence(6,10)+"",16);
			preVoltageNum.setText(aa/10+"."+aa%10+"V");
			int bb=Integer.parseInt(respData.subSequence(10,14)+"",16);
			realVoltageNum.setText(bb/10+"."+bb%10+"V");
		}
		
	}
	/**
     *  普通字符转换成16进制字符串
     * @param str
     * @used NO
     * @return
     */
    public static String str2HexStr(String str)
    {
        byte[] bytes = str.getBytes();
        // 如果不是宽类型的可以用Integer
        BigInteger bigInteger = new BigInteger(1, bytes);  
        return bigInteger.toString(16);
    }
	/*
    /*
     * 字符串转字节数组
     */
    public static byte[] string2Bytes(String s){
        byte[] r = s.getBytes();
        return r;
    }
    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";
        
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }
        
        return r;
    }
	/* BluetoothLeService绑定的回调函数 */
	private final ServiceConnection mServiceConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service)
		{
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize())
			{
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			// 根据蓝牙地址，连接设备
			mBluetoothLeService.connect(mDeviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mBluetoothLeService = null;
		}

	};

	/**
	 * 广播接收器，负责接收BluetoothLeService类发送的数据
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
			{
				mConnected = true;
				status = "connected";
				//更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt连接失败
					.equals(action))
			{
				mConnected = false;
				status = "disconnected";
				//更新连接状态
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//发现GATT服务器
					.equals(action))
			{
				// Show all the supported services and characteristics on the
				// user interface.
				//获取设备的所有蓝牙服务
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//有效数据
			{    
				 //处理发送过来的数据
				displayData(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
				//add new
//				AlertDialog.Builder builder  = new Builder(Ble_Activity.this);
//				 builder.setTitle("确认" ) ;
//				 builder.setMessage(intent.getExtras().getString(
//							BluetoothLeService.EXTRA_DATA)) ;
//				 builder.setPositiveButton("是" ,  null );
//				 builder.show(); 
				 buildCurrentVoltageData(intent.getExtras().getString(
							BluetoothLeService.EXTRA_DATA));
				
				
			}
		}
	};

	/* 更新连接状态 */
	private void updateConnectionState(String status)
	{
		Message msg = new Message();
		msg.what = 1;
		Bundle b = new Bundle();
		b.putString("connect_state", status);
		msg.setData(b);
		//将连接状态更新的UI的textview上
		myHandler.sendMessage(msg);
		System.out.println("connect_state:" + status);

	}

	/* 意图过滤器 */
	private static IntentFilter makeGattUpdateIntentFilter()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/** 
	* @Title: displayData 
	* @Description: TODO(接收到的数据在scrollview上显示) 
	* @param @param rev_string(接受的数据)
	* @return void   
	* @throws 
	*/ 
	private void displayData(String rev_string)
	{
		rev_str += rev_string;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
//				rev_tv.setText(rev_str);
				//add begin
//				resp_bit_data.setText(rev_str);
//				rev_str =rev_str.replaceAll(" ", "");
//				if(rev_str.isEmpty()){
//					preCurrentNum.setText("暂无数据");
//					realCurrentNum.setText("暂无数据");
//					preVoltageNum.setText("暂无数据");
//					realVoltageNum.setText("暂无数据");
//				}else if(respData.length()>=14){
//					preCurrentNum.setText(Integer.parseInt(rev_str.subSequence(4, 6)+"",16)+"A");
//					realCurrentNum.setText(Integer.parseInt(rev_str.subSequence(5, 7)+"",16)+"A");
//					preVoltageNum.setText(Integer.parseInt(rev_str.subSequence(8, 10)+"",16)+"V");
//					realVoltageNum.setText(Integer.parseInt(rev_str.subSequence(11,13)+"",16)+"V");
//				}
				//add end
//				rev_sv.scrollTo(0, rev_tv.getMeasuredHeight());
				System.out.println("rev:" + rev_str);
			}
		});

	}

	/** 
	* @Title: displayGattServices 
	* @Description: TODO(处理蓝牙服务) 
	* @param 无  
	* @return void  
	* @throws 
	*/ 
	private void displayGattServices(List<BluetoothGattService> gattServices)
	{

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
		for (BluetoothGattService gattService : gattServices)
		{

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
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
			{
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT))
				{
					// 测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
					mhandler.postDelayed(new Runnable()
					{

						@Override
						public void run()
						{
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
				for (BluetoothGattDescriptor descriptor : descriptors)
				{
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
	/**
	 * 将数据分包
	 * 
	 * **/
	public int[] dataSeparate(int len)
	{   
		int[] lens = new int[2];
		lens[0]=len/20;
		lens[1]=len-20*lens[0];
		return lens;
	}
	//新规则
	private String tenTohex(String tempC,String tempV){
		//举例：
		int tempA = 0;
		if(tempV.indexOf(".")>0){
			String[] tsr =tempV.split("\\.");
			tempV =tsr[0]+tsr[1];
			tempA =Integer.valueOf(tempV);
		}else{
			tempA =Integer.valueOf(tempV).intValue()*10;
		}
		if(Integer.valueOf(tempC).intValue()<16){
			 tempStr1 ="0"+ Integer.toHexString(Integer.valueOf(tempC).intValue()); 
		}else{
			 tempStr1 = Integer.toHexString(Integer.valueOf(tempC).intValue()); 
		}
		if(tempA<16){
			 tempStr2 ="000"+Integer.toHexString(tempA); 
		}else if(tempA<255){
			 tempStr2 ="00"+Integer.toHexString(tempA); 
		}else if(tempA<4095){
			tempStr2 ="0"+Integer.toHexString(tempA);
		}else{
			tempStr2 =Integer.toHexString(tempA);
		}
		
		return tempStr1+" "+tempStr2.substring(0, 2)+" "+tempStr2.substring(2, 4);
	}
//	旧规则
	private String tenTohex2(String a,String tempV){
		//不满16进制特殊处理
		int valueA = 0;
		if(tempV.indexOf(".")>0){
			String[] tsr =tempV.split("\\.");
			tempV =tsr[0]+tsr[1];
			valueA =Integer.valueOf(tempV);
		}else{
			valueA =Integer.valueOf(tempV).intValue()*10;
		}
		if(Integer.valueOf(a).intValue()<16){
			tempStr1 ="0"+ Integer.toHexString(Integer.valueOf(a).intValue()); 
		}else{
			tempStr1 = Integer.toHexString(Integer.valueOf(a).intValue()); 
		}
		if(valueA<16){
			tempStr2 ="000"+Integer.toHexString(valueA); 
		}else if(valueA<255){
			tempStr2 ="00"+Integer.toHexString(valueA); 
		}else if(valueA<4095){
			tempStr2 ="0"+Integer.toHexString(valueA);
		}else{
			tempStr2 =Integer.toHexString(valueA);
		}
		
		return tempStr1+tempStr2;
	}
	

	/* 
	 * 发送按键的响应事件，主要发送文本框的数据
	 */
	public void onClick1(View v)
	{
		
		//1、判断是否连接了，且返回数据了
		if(!mConnected){
			Toast.makeText(Ble_Activity.this, "请先连接上蓝牙", Toast.LENGTH_LONG)
			.show();
			return;
		}else if(resp_bit_data.getText().toString().equals(Constant.EMPTY_TEXT)){
			Toast.makeText(Ble_Activity.this, "当前蓝牙未返回数据", Toast.LENGTH_LONG)
			.show();
			return;
		}
		Log.v("btnbtnididi", send_current.getText()+"|||"+send_voltage.getText());
		//2、去除预置电压和预置电流值
		String tempV = send_voltage.getText().toString();
		String tempC =send_current.getText().toString();
		//3、判断用户是否输入 begin 16进制开始--如果用户没输入取之前单片机返回的数据
		if(tempC.isEmpty()){
			tempC =preCurrentNum.getText().toString().replaceAll("A", "");
		}else{
			if(Integer.valueOf(tempC)<5){
				Toast.makeText(Ble_Activity.this, "预置电流不能小于5V", Toast.LENGTH_LONG)
				.show();
				return;
			}
		}
		if(tempV.isEmpty()){
			tempV =preVoltageNum.getText().toString().replaceAll("V", "");
		}
		String tenTohex ="ee " +tenTohex(tempC,tempV)+" ff ff";
//		String tenTohex ="EE 1F 00 A4 FF FF";
		byte[] buff = null;
		buff = HexCommandtoByte(tenTohex.getBytes());
//		end
		Log.v("TAG",tenTohex);
		send_tv.setText(tenTohex);
		//2、改写直接 发字符串
//		target_chara.setValue(tenTohex);
		Log.v("btnbtnididi",buff.toString());
		target_chara.setValue(buff);
		mBluetoothLeService.writeCharacteristic(target_chara);
		Toast.makeText(Ble_Activity.this, "发送成功", Toast.LENGTH_SHORT)
		.show();
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
		 public void goOtherActivity(View view){

	           Toast.makeText(Ble_Activity.this, "跳转啦",Toast.LENGTH_SHORT).show();
	           //销毁
	           onDestroy();
	           final Intent intent = new Intent(Ble_Activity.this,
	        		   TestSecondConnectActivity.class);
				intent.putExtra(Ble_Activity.EXTRAS_DEVICE_NAME,
						mDeviceName);
				intent.putExtra(Ble_Activity.EXTRAS_DEVICE_ADDRESS,
						mDeviceAddress);
				intent.putExtra(Ble_Activity.EXTRAS_DEVICE_RSSI,
						mRssi);
				intent.putExtra(Ble_Activity.EXTRAS_DEVICE_RESP,
						"");
				startActivity(intent);
	    }
		 
			public class JsInteration {
				 
				@JavascriptInterface
			    public String getConStatus() {
//				        return "hello world";
			    	return connect_status;
			    }
			    @JavascriptInterface
			    public String back() {
//			        return "hello world";
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
//						s += "id:" + cursor.getInt(0) + "\tmachinedId:" + cursor.getString(1)
//								+ "\t machinedName:" + cursor.getString(2) + "\t setInfo:" + cursor.getString(5) + "\n";
//						s+=weldInfo.toString();
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
//					sd.update("weldInfo",values,whereClause,whereArgs);
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

}
