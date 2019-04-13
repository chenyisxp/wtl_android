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
 * �ر�˵����WTL_BLE�������Ϻ����������ӿƼ����޹�˾�����з����ֻ�APP�������û�����08����ģ�顣
 * �����ֻ��֧�ְ�׿�汾4.3����������4.0���ֻ�ʹ�á�

 * **/
/**
 * @Description: TODO<MainActivity��ʵ�ִ�������ɨ������>
 * @author �Ϻ����������ӿƼ����޹�˾
 * @data: 2018-10-16 ����10:28:18
 * @version: V1.0
 */
public class CopyOfMainActivity extends Activity{
	/**
	 * 1��У�����ݵĵ�ǰ״̬
	 * 2����ȷ��
	 * 3��ҳ����Դ
	 * 4���ط�ʱ�� ʱ���
	 */
	private static HashMap<String, String> checkData=new HashMap<String, String>();
	private static HashMap<String, String> checkPage=new HashMap<String, String>();
	private static HashMap<String, Boolean> checkStatus=new HashMap<String, Boolean>();
	private static HashMap<String, Long> checkTime=new HashMap<String, Long>();
	//�ط�ʱ����
	private static long  sechelTime=1400;
	/**
	 * ��ʱ��
	 */
	private static Timer timer = new Timer(); 
	private static TimerTask task ;
	
	// ɨ��������ť
//	private Button scan_btn;
	private Button test_btn;
	public static  String scan_btn_html="startScan";
	public static  String bleRespInfo="empty";
	// ����������
	BluetoothAdapter mBluetoothAdapter;
	// �����ź�ǿ��
	private ArrayList<Integer> rssis;
	// �Զ���Adapter
//	LeDeviceListAdapter mleDeviceListAdapter;
	// listview��ʾɨ�赽��������Ϣ
//	ListView lv;
	// ����ɨ��������״̬
	private boolean mScanning;
	private boolean scan_flag;
//	private Handler mHandler;
	int REQUEST_ENABLE_BT = 1;
	// ����ɨ��ʱ��
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
	// ����4.0��UUID,����0000ffe1-0000-1000-8000-00805f9b34fb�ǹ��ݻ����Ϣ�Ƽ����޹�˾08����ģ���UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_RESP = "DEVICE_RESP";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	// ��������״̬
	private boolean mConnected = false;
	private String status = "disconnected";
	// ��������
	private String mDeviceName;
	// ������ַ
	private String mDeviceAddress;
	// �����ź�ֵ
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	private String respData;
	// ����service,�����̨����������
	private static BluetoothLeService mBluetoothLeService;
	// �ı�����ʾ���ܵ�����
	private TextView send_tv, connect_state, resp_bit_data, preCurrentNum,
			realCurrentNum, preVoltageNum, realVoltageNum;
	private String connect_status;// ����״̬
	// ���Ͱ�ť
	private Button send_btn;
	// �ı��༭��
	private EditText send_current;
	private EditText send_voltage;
	// private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	// �ַ�������
	private String tempStr1 = "";
	private String tempStr2 = "";
	public JsInteration jsInteration = new JsInteration(){
		
	};
	//�������� map ȥ���ظ���
	private Map<String,BleListBean> bleList =new HashMap<String,BleListBean>();
	// ��������ֵ
	private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler() {
		// 2.��д��Ϣ������
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// �жϷ��͵���Ϣ
			case 1: {
				// ����View
				// String state = msg.getData().getString("connect_state");
				// connect_state.setText(state);
				connect_status = msg.getData().getString("connect_state");
				// ״̬���ݸ� html5ҳ��
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
		//��ʼ�����ذ�ť
		test_btn = (Button) this.findViewById(R.id.scan_dev_btn);
		// ��ʼ������
		init_ble();
		scan_flag = true;
		
		/* ��������service */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
			//��ʼ����ʱ��
			initTimer();
			//webview
		   mWebView = (WebView) findViewById(R.id.webView);
		    mWebView.loadUrl("http://192.168.1.3:8082/#/");
//		   mWebView.loadUrl("file:///android_asset/index.html");
		    
		    WebSettings webSettings = mWebView.getSettings();
		    webSettings.setJavaScriptEnabled(true);
		    webSettings.setDefaultTextEncodingName("gbk");//���ñ����ʽ
		    mWebView.addJavascriptInterface(jsInteration, "android");
		    mWebView.setWebViewClient(new WebViewClient() {
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        	 nowUrl=url;
		                mWebView.loadUrl(url);
		                return false;//������webview���
//		            }
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
	private static void initTimer(){
		task = new TimerTask() { 
		    @Override 
		    public void run() {
		    	Boolean shutDownFlag =false;//��ֹ�ı�ʶ
		    	//����Ƿ���Ҫ�ط�
		    	for(String crcCode :checkStatus.keySet()){
		    		if(!checkStatus.get(crcCode)){
		    			shutDownFlag =true;
		    			//�Ƿ�����İ�ms
		    			
		    			if((new Date().getTime() -checkTime.get(crcCode))>sechelTime){
		    				//����ʱ���
		    				checkTime.put(crcCode,new Date().getTime());
		    				//�ط���������Ϣ
		    				String regex = "(.{2})";
		    				String data = checkData.get(crcCode).replaceAll (regex, "$1 ");
		    				target_chara.setValue(HexCommandtoByte(data.getBytes()));
		    				mBluetoothLeService.writeCharacteristic(target_chara);
		    			}
		    		}
		    	}
		    	if(!shutDownFlag){
		    		//û��δ��ɵ����� ֹͣ
		    		task.cancel();
		    	}
		    } 
		}; 
		//��Ҫ�½�
		timer =new Timer();
		timer.schedule(task, sechelTime, sechelTime);
	}
	private void onCreate_old() {
		setContentView(R.layout.activity_main);
		// ��ʼ���ؼ�
		// init();
		// ��ʼ������
		init_ble();
		scan_flag = true;
	}

	/**
	 * @Title: init
	 * @Description: TODO(��ʼ��UI�ؼ�)
	 * @param ��
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
	 * @Description: TODO(��ʼ������)
	 * @param ��
	 * @return void
	 * @throws
	 */
	private void init_ble() {
		// �ֻ�Ӳ��֧������
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "��֧��BLE", Toast.LENGTH_SHORT).show();
			finish();
		}
		// Initializes Bluetooth adapter.
		// ��ȡ�ֻ����ص�����������
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// ������Ȩ��
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

	}

	/*
	 * �����ʼɨ��
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
//			scan_btn.setText("ɨ���豸");
			scan_btn_html ="startScan";
//			SendDataToHtmlFucUtil.updateHtmlBleScanBtnText(scan_btn_html, mWebView);
		}
	}

	/**
	 * @Title: scanLeDevice
	 * @Description: TODO(ɨ�������豸 )
	 * @param enable
	 *            (ɨ��ʹ�ܣ�true:ɨ�迪ʼ,false:ɨ��ֹͣ)
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
//					scan_btn.setText("ɨ���豸");
					scan_btn_html ="startScan";
//					SendDataToHtmlFucUtil.updateHtmlBleScanBtnText(scan_btn_html, mWebView);
					Log.i("SCAN", "stop.....................");
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			/* ��ʼɨ�������豸����mLeScanCallback �ص����� */
			Log.i("SCAN", "begin.....................");
			mScanning = true;
			scan_flag = false;
//			scan_btn.setText("ֹͣɨ��");
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
	 * ����ɨ��ص����� ʵ��ɨ�������豸���ص�����BluetoothDevice�����Ի�ȡname MAC����Ϣ
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
					/* ��ɨ�赽�豸����Ϣ�����listview�������� */
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
	 * @Description: TODO<�Զ���������Adapter,��Ϊlistview��������>
	 * @author ���ݻ����Ϣ�Ƽ����޹�˾
	 * @data: 2014-10-12 ����10:46:30
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
		 * ��дgetview
		 * 
		 * **/
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {

			// General ListView optimization code.
			// ����listviewÿһ�����ͼ
			view = mInflator.inflate(R.layout.listitem, null);
			// ��ʼ������textview��ʾ������Ϣ
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
	  //ʹ��Webview�� ʱ�򣬷��ؼ�û����д��ʱ���ֱ�ӹرճ�����ʱ����ʵ����Ҫ��ִ�е�֪ʶ���˵���һ���Ĳ���
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //����һ�������õİ����ķ�����keyCode �����û��Ķ���������ǰ��˷��ؼ���ͬʱWebviewҪ���صĻ���WebViewִ�л��˲�������ΪmWebView.canGoBack()���ص���һ��Boolean���ͣ��������ǰ�������Ϊtrue
        if(keyCode==KeyEvent.KEYCODE_BACK&&mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	/***************************** ����ble_activitybegig ****************************************/
	/************************************ begin *************************************/

//	private Handler mhandler = new Handler();


	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ����㲥������
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
	}

	// Activity����ʱ�򣬰󶨹㲥�������������������ӷ��񴫹������¼�
//	@Override
//	protected void onResume() {
//		super.onResume();
//		// �󶨹㲥������
//		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//		if (mBluetoothLeService != null) {
//			// ����������ַ����������
//			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
//			Log.d(TAG, "Connect request result=" + result);
//		}
//	}
	
	private void connectBle(){
		// �󶨹㲥������
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			if (mBluetoothLeService != null) {
				// ����������ַ����������
				final boolean result = mBluetoothLeService.connect(mDeviceAddress);
				Log.d(TAG, "Connect request result=" + result);
			}
	}

	/**
	 * �������ݽ�������
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

	/* BluetoothLeService�󶨵Ļص����� */
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
			// ����������ַ�������豸
			mBluetoothLeService.connect(mDeviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}

	};

	/**
	 * �㲥���������������BluetoothLeService�෢�͵�����
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt���ӳɹ�
			{
				mConnected = true;
				status = "connected";
				// ��������״̬
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED// Gatt����ʧ��
					.equals(action)) {
				mConnected = false;
				status = "disconnected";
				// ��������״̬
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED// ����GATT������
					.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				// ��ȡ�豸��������������
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))// ��Ч����
			{
				// �����͹���������
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
	//ͨ�������������ݴ����ʽ
	private void commonBleRespData(final String data){
		//����У��
		if(data.isEmpty()){
			return;
			//��С����Ϊ7 ��֡ͷ+ָ��+crcУ��
		}else if(data.length()>7 && data.startsWith("DA")){
			//1����ȡ���ݽ����и�У��   �����λ ��ΪcrcУ��ֵ    �鿴�Ƿ���ȷ
			String midData ="";
			if(data.length()==8){
				//���� û�������ֶ�
				midData="";
			}else{
				midData =data.substring(4, data.length()-4);
			}
			
			final String crc  =data.substring(data.length()-4, data.length());
			if(checkData.get(crc)==""||checkData.get(crc)==null){
				//�쳣 crc���˰�
//				return;
			//������ת��crc У���Ƿ���ȡ��ְֵ
				if(midData!=""){
					if(checkData.get(CRC16M.getCRC3(HexCommandtoByte(midData.getBytes()))).isEmpty()){
						//���ҵ�����
						return;
					}else{
						//TODO �ط�
					}
				}
			}else if(!checkData.get(crc).equals(data)){
				//�쳣 
				//TODO �ط�
				return;
			}else{
				//1����ȷ�ر� ��ʱ��
				timer.cancel();
				timer = null;
				//2������
				mWebView.post(new Runnable() {
				    @Override
				    public void run() {
				    	mWebView.loadUrl("javascript:broastFromAndroid('" + data + "','"+checkPage.get(crc)+"')");
				    	checkData.remove(crc);
						checkPage.remove(crc);
						checkStatus.remove(crc);
						checkTime.remove(crc);
				}});
				//3���������
				
			}
			
		}
		
	}
	/* ��������״̬ */
	private void updateConnectionState(String status) {
//		SendDataToHtmlFucUtil.updateHtmlBleConnectStatus(status, mWebView);
//		Message msg = new Message();
//		msg.what = 1;
//		Bundle b = new Bundle();
//		b.putString("connect_state", status);
//		msg.setData(b);
		// ������״̬���µ�UI��textview��
//		myHandler.sendMessage(msg);
//		System.out.println("connect_state:" + status);

	}

	/* ��ͼ������ */
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
	 * @Description: TODO(������������)
	 * @param ��
	 * @return void
	 * @throws
	 */
	private void displayGattServices(List<BluetoothGattService> gattServices) {

		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "unknown_service";
		String unknownCharaString = "unknown_characteristic";

		// ��������,����չ�����б�ĵ�һ������
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

		// �������ݣ�������ĳһ���������������ֵ���ϣ�
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

		// ���ֲ�Σ���������ֵ����
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {

			// ��ȡ�����б�
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();

			// ������ݸ�uuid��ȡ��Ӧ�ķ������ơ�SampleGattAttributes�������Ҫ�Զ��塣

			gattServiceData.add(currentServiceData);

			System.out.println("Service uuid:" + uuid);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

			// �ӵ�ǰѭ����ָ��ķ����ж�ȡ����ֵ�б�
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			// ���ڵ�ǰѭ����ָ��ķ����е�ÿһ������ֵ
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT)) {
					// ���Զ�ȡ��ǰCharacteristic���ݣ��ᴥ��mOnDataAvailable.onCharacteristicRead()
					mhandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mBluetoothLeService
									.readCharacteristic(gattCharacteristic);
						}
					}, 200);

					// ����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
					mBluetoothLeService.setCharacteristicNotification(
							gattCharacteristic, true);
					target_chara = gattCharacteristic;
					// ������������
					// ������ģ��д������
					// mBluetoothLeService.writeCharacteristic(gattCharacteristic);
				}
				List<BluetoothGattDescriptor> descriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor descriptor : descriptors) {
					System.out.println("---descriptor UUID:"
							+ descriptor.getUuid());
					// ��ȡ����ֵ������
					mBluetoothLeService.getCharacteristicDescriptor(descriptor);
					// mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
					// true);
				}

				gattCharacteristicGroupData.add(currentCharaData);
			}
			// ���Ⱥ�˳�򣬷ֲ�η�������ֵ�����У�ֻ������ֵ
			mGattCharacteristics.add(charas);
			// �����ڶ�����չ�б��������������ֵ��
			gattCharacteristicData.add(gattCharacteristicGroupData);

		}

	}

	// �¹���
	private String tenTohex(String tempC, String tempV) {
		// ������
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
	// ʮ�����Ƶ��ַ���ת����byte����
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
			//0����ʼ�� ��ʼ��ʱ��
			CopyOfMainActivity.requestFromHtmlInit(pageFrom, data, crcCode);
				//java.util.Timer.schedule(TimerTask task, long delay, long period)�����������˵��delay/1000���ִ��task,
				//Ȼ�����period/1000���ٴ�ִ��task���������ѭ������ִ�������Σ���Ȼ���������timer.cancel();ȡ����ʱ����ִ�С�
			initTimer();
			//1����дֱ�� ���ַ���
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
				//���Խ���
				return;
			}
			byte[] buff = null;
			buff = HexCommandtoByte(sendData.getBytes());
			//2����дֱ�� ���ַ���
			target_chara.setValue(buff);
			mBluetoothLeService.writeCharacteristic(target_chara);
		}
		/**
		 * �ж� ��������
		 * @return
		 */
		@JavascriptInterface
		public String closeBleConnect() {
			//������״̬��Ϊδ����
			status="disconnected";
			return Constant.SUC_CODE;
		}
		/**
		 * ���� ���ص�����
		 * @return
		 */
		@JavascriptInterface
		public String getBleRespMsg() {
			BleRespBean data =new BleRespBean();
			if(bleRespInfo.isEmpty() ||bleRespInfo=="00" ||bleRespInfo=="empty"){
				//ȡ����ֵ E1
				//֡ͷ(oxDA)    ���һ���ֽڣ�	����	У�飨����+���ݣ�
				//DA E1 00 00 00 00 
				//
				bleRespInfo="1111 6666";
			}
			 data = buildCurrentVoltageData(bleRespInfo);
			return data.toString();
		}
		/**
		 * getready ��ʼ����
		 */
		@JavascriptInterface
		public String getReadyGo() {
			//1���ж��Ƿ������ˣ��ҷ���������
//			if(!mConnected){
//				return "��������������";
//			}
//			else if(resp_bit_data.getText().toString().equals(Constant.EMPTY_TEXT)){
//				Toast.makeText(Ble_Activity.this, "��ǰ����δ��������", Toast.LENGTH_LONG)
//				.show();
//				return ;
//			}
			//2��ȥ��Ԥ�õ�ѹ��Ԥ�õ���ֵ
			String tempV = "10";
			String tempC ="20";
			//3���ж��û��Ƿ����� begin 16���ƿ�ʼ--����û�û����ȡ֮ǰ��Ƭ�����ص�����
			if(tempC.isEmpty()){
//				tempC =preCurrentNum.getText().toString().replaceAll("A", "");
			}else{
//				if(Integer.valueOf(tempC)<5){
//					Toast.makeText(Ble_Activity.this, "Ԥ�õ�������С��5V", Toast.LENGTH_LONG)
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
			//2����дֱ�� ���ַ���
//			target_chara.setValue(tenTohex);
			target_chara.setValue(buff);
			mBluetoothLeService.writeCharacteristic(target_chara);
			return "success";
		}
		
		/**
		 *�����������
		 * @return
		 */
		@JavascriptInterface
		public void setBleConnect(String address) {
			if (mScanning)
			{
				/* ֹͣɨ���豸 */
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				mScanning = false;
			}
			//�������� ԭ��ble_activity���߼�
			/* ��������service */
//			Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//			bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
//			mDeviceAddress="78:04:73:00:AC:3D";
			mDeviceAddress=address.replaceAll("\"", "");
			connectBle();
		}
		/**
		 *��ȡ ble�������ص�list
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
		 *��ȡ ɨ�谴ť ����
		 * @return
		 */
		@JavascriptInterface
		public String getScanBtnText() {
			return scan_btn_html;
			
		}
		
		/**
		 * ��ʼɨ��
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
			
			
//			updateHtmlBleScanBtnText("���ԷŻ�", mWebView);
//			 Handler mainHandler = new Handler(Looper.getMainLooper());
//			    mainHandler.post(new Runnable() {
//			        @Override
//			        public void run() {
			            //�������߳��У����Ը���UI
//			        	updateHtmlBleScanBtnText(11+"");
//			 mWebView.evaluateJavascript("aa('"+1+"')", new ValueCallback<String>() {
  		    	//������ֵ
//  		    	@Override
//  		        public void onReceiveValue(String value) {
//  		        }
//  		    });
//			        }
//			    });
			    
//			 ((MainActivity) context).runOnUiThread(new Runnable() {
//		            @Override
//		            public void run() {
//		                //��ʱ�������߳��У����Ը���UI��
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
		 * ��ȡ����״̬
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

		// ����ֵ ����SharedPreferences ��Ƭ�洢
		@JavascriptInterface
		public String setSettingInfo(String key, String value) {
			return SaveUtil.updateDataBykey(context, key, value);
		}

		// ��ȡ����ֵ ����SharedPreferences ��Ƭ�洢
		@JavascriptInterface
		public String getSettingInfo(String key) {
			return SaveUtil.getDataBykey(context, key);
		}

		// ��ȡ����ֵ ����sql ��Ƭ�洢
		@JavascriptInterface
		public void testSqliteset() {
			// ��ȡ���ݿ��������
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getWritableDatabase();
			// 1������һ
			// ����sql���
			// String sql =
			// "insert into weldInfo(machinedId,weldType,dataTAype,setInfo,noteInfo,weldBeginTime,weldEndTime,weldConnectTime,memo,weldType)"
			// + "values('"+name+"','"+age+"')";
			// ִ��sql���
			// sd.execSQL(sql);
			// �ر����ݿ�
			// db.close();
			// 2��������
			// ʵ��������ֵ
			ContentValues cValue = new ContentValues();
			cValue.put("machinedId", "A0001");
			cValue.put("machinedName", "�̵ؽ��һ�Ż�");
			cValue.put("weldType", "MMA");
			cValue.put("dataType", "0");
			cValue.put(
					"setInfo",
					"{\"weldType\":\"MIG SYN\",\"options\":[{\"name\":\"MODE\",\"value\":\"21T\",\"unit\":\"T\"},{\"name\":\"MATERIAL\",\"value\":\"FE\"},{\"name\":\"GAS\",\"value\":\"Ar\"},{\"name\":\"DIAMETER\",\"value\":\"0.6mm\"},{\"name\":\"THICKNESS\",\"value\":\"0.6mm\"},{\"name\":\"INDUCTANCE\",\"value\":\"90\",\"unit\":\"A\"},{\"name\":\"SPEED\",\"value\":\"8\",\"unit\":\"BIG1\"}]}");
			cValue.put("noteInfo", "�ҵı�ע");
			cValue.put("weldBeginTime", "2018-11-09 12:20:11");
			cValue.put("weldEndTime", "2018-11-09 15:20:11");
			cValue.put("weldConnectTime", "3.7");
			cValue.put("memo", "�½���ע");
			cValue.put("rec_stat", "1");
			cValue.put("creator", "admin");
			cValue.put("modifier", "admin");
			Log.v("aaaa", DateUtil.getCurrentDateyyyyMMddHHmmss());
			cValue.put("cre_tm", DateUtil.getCurrentDateyyyyMMddHHmmss());
			cValue.put("up_tm", DateUtil.getCurrentDateyyyyMMddHHmmss());
			// ����insert()������������
			sd.insert("weldInfo", null, cValue);
			db.close();
		}

		// ��ȡ����ֵ ����sql ��Ƭ�洢
		@JavascriptInterface
		public String testSqliteget() {
			// �����е���Ϣչʾ���ı�����
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			String sql = "select id,machinedId,machinedName,weldType,dataType,setInfo,noteInfo,weldBeginTime"
					+ ",weldEndTime,weldConnectTime,memo,rec_stat,creator,modifier,cre_tm,up_tm"
					+ " from weldInfo  ORDER BY cre_tm DESC";// ����ʱ������
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

		// ��ȡ����ֵ������
		@JavascriptInterface
		public String testSqliteupdate() {
			// �����е���Ϣչʾ���ı�����
			String s = "";
			SQLiteDBUtil db = new SQLiteDBUtil(getApplicationContext());
			SQLiteDatabase sd = db.getReadableDatabase();
			// ʵ��������ֵ
			ContentValues values = new ContentValues();
			// ��values���������
			values.put("setInfo", "{'current':'10','valatge':'20'}");
			// �޸�����
			String whereClause = "id=?";
			// �޸���Ӳ���
			String[] whereArgs = { String.valueOf(1) };
			// �޸�
			// sd.update("weldInfo",values,whereClause,whereArgs);
			sd.update("weldInfo", values, null, null);
			db.close();
			return s;
		}

		// ��ȡ����ֵ������
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
	// Android�����з���ֵjs����
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
	 * ɨ�谴ť����
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public  void updateHtmlBleScanBtnText(String text) {
		 
//		mWebView.post(new Runnable() {
//            @Override
//            public void run() {
            	 mWebView.evaluateJavascript("aa('"+1+"')", new ValueCallback<String>() {
     		    	//������ֵ
     		    	@Override
     		        public void onReceiveValue(String value) {
     		        }
     		    });
//            }
//        });
		   
	}
	/***************************** ����ble_activitybegig ****************************************/
	/************************************ begin *************************************/
	/********************************************************************************/
	private static void requestFromHtmlInit(String pageFrom,String data,String crcCode){
		//��ǰҳ�� ��ֵ�˷���
		if(pageFrom.equals(checkPage.get(crcCode))){
			return;
		}
		checkPage.put(crcCode, pageFrom);//ҳ����Դ
		checkStatus.put(crcCode, false);//Ĭ������δ�ɹ�
		checkData.put(crcCode, data);//Ҫ���͵�����
		checkTime.put(crcCode, new Date().getTime());//ʱ���
	}
	private void reponseFromBleInit(String crcCode){
		// ��ֵ ������
		if(!checkPage.get(crcCode).isEmpty()){
			checkStatus.put(crcCode, true);//�ɹ� ����Ҫ�ط�
		}
	}
}
