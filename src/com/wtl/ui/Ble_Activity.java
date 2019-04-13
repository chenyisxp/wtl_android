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
 * �ر�˵����WTL_BLE�������Ϻ����������ӿƼ����޹�˾�����з����ֻ�APP��
 * **/

/** 
 * @Description:  TODO<Ble_Activityʵ������BLE,���ͺͽ���BLE������> 
 * @author  �Ϻ����������ӿƼ����޹�˾
 * @data:  2014-10-20 ����12:12:04 
 * @version:  V1.0 
 */ 
public class Ble_Activity extends Activity{

	private static String nowUrl="";
	private WebView mWebView;
	private static Context context;
	
	
	private final static String TAG = Ble_Activity.class.getSimpleName();
	//����4.0��UUID,����0000ffe1-0000-1000-8000-00805f9b34fb�ǹ��ݻ����Ϣ�Ƽ����޹�˾08����ģ���UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_RESP = "DEVICE_RESP";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	//��������״̬
	private boolean mConnected = false;
	private String status = "disconnected";
	//��������
	private String mDeviceName;
	//������ַ
	private String mDeviceAddress;
	//�����ź�ֵ
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	private String respData;
	//����service,�����̨����������
	private static BluetoothLeService mBluetoothLeService;
	//�ı�����ʾ���ܵ�����
	private TextView send_tv,connect_state,resp_bit_data,preCurrentNum,realCurrentNum,preVoltageNum,realVoltageNum;
	private String connect_status;//����״̬
	//���Ͱ�ť
	private Button send_btn;
	//�ı��༭��
	private EditText send_current;
	private EditText send_voltage;
//	private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//�ַ�������
	private String tempStr1 = "";
	private String tempStr2 = "";
	//��������ֵ
	private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler()
	{
		// 2.��д��Ϣ������
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// �жϷ��͵���Ϣ
			case 1:
			{
				// ����View
//				String state = msg.getData().getString("connect_state");
//				connect_state.setText(state);
				connect_status = msg.getData().getString("connect_state");
				//״̬���ݸ� html5ҳ��
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
		//����ͼ��ȡ��ʾ��������Ϣ
		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
		
		/* ��������service */
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
		    webSettings.setDefaultTextEncodingName("gbk");//���ñ����ʽ
		    mWebView.addJavascriptInterface(new JsInteration(), "android");
		    mWebView.setWebViewClient(new WebViewClient() {
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        	 Toast.makeText(Ble_Activity.this,Data.getA(),1).show();
		        	 nowUrl=url;
//		            if (url.equals("file:///android_asset/test2.html")) {
//		                Log.e(TAG, "shouldOverrideUrlLoading: " + url);
//		                startActivity(new Intent(SecondActivity.this,MainActivity.class));
//		                return true;//activity֮����ת
//		            } else {
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
	//Android�����з���ֵjs����
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
//		//����ͼ��ȡ��ʾ��������Ϣ
//		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
//		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
//		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
//		
//		/* ��������service */
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
	 * ��ʼ��webview
	 */

	private void htmlInit(){
		
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
        //����㲥������
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
	}

	// Activity����ʱ�򣬰󶨹㲥�������������������ӷ��񴫹������¼�
	@Override
	protected void onResume()
	{
		super.onResume();
		//�󶨹㲥������
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null)
		{    
			//����������ַ����������
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}

	/** 
	* @Title: init 
	* @Description: TODO(��ʼ��UI�ؼ�) 
	* @param  ��
	* @return void    
	* @throws 
	*/ 
	private void init()
	{
		send_tv = (TextView) this.findViewById(R.id.send_tv);
		connect_state = (TextView) this.findViewById(R.id.connect_state);
		send_btn = (Button) this.findViewById(R.id.send_btn);
		//�޸��ı���
		send_current = (EditText) this.findViewById(R.id.send_current);
			InputFilter[] filters1 = { new EditInputContainFilter() };  
			send_current.setFilters(filters1);
		send_voltage = (EditText) this.findViewById(R.id.send_voltage);
			InputFilter[] filters2 = { new EditInputFilter() };  
			send_voltage.setFilters(filters2);
		connect_state.setText(status);

		//�����ԭʼ����
		resp_bit_data = (TextView) this.findViewById(R.id.resp_bit_data);
		resp_bit_data.setText(respData);
		preCurrentNum = (TextView) this.findViewById(R.id.preCurrentNum);
		realCurrentNum = (TextView) this.findViewById(R.id.realCurrentNum);
		preVoltageNum = (TextView) this.findViewById(R.id.preVoltageNum);
		realVoltageNum = (TextView) this.findViewById(R.id.realVoltageNum);
		//��װ�ַ�����⼰��װ��ʾ
		buildCurrentVoltageData(respData);
//		send_btn.setOnClickListener(this);

	}
	/**
	 * �������ݽ�������
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
     *  ��ͨ�ַ�ת����16�����ַ���
     * @param str
     * @used NO
     * @return
     */
    public static String str2HexStr(String str)
    {
        byte[] bytes = str.getBytes();
        // ������ǿ����͵Ŀ�����Integer
        BigInteger bigInteger = new BigInteger(1, bytes);  
        return bigInteger.toString(16);
    }
	/*
    /*
     * �ַ���ת�ֽ�����
     */
    public static byte[] string2Bytes(String s){
        byte[] r = s.getBytes();
        return r;
    }
    /*
     * �ֽ�����ת16�����ַ���
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
	/* BluetoothLeService�󶨵Ļص����� */
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
			// ����������ַ�������豸
			mBluetoothLeService.connect(mDeviceAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mBluetoothLeService = null;
		}

	};

	/**
	 * �㲥���������������BluetoothLeService�෢�͵�����
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt���ӳɹ�
			{
				mConnected = true;
				status = "connected";
				//��������״̬
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt����ʧ��
					.equals(action))
			{
				mConnected = false;
				status = "disconnected";
				//��������״̬
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//����GATT������
					.equals(action))
			{
				// Show all the supported services and characteristics on the
				// user interface.
				//��ȡ�豸��������������
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//��Ч����
			{    
				 //�����͹���������
				displayData(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
				//add new
//				AlertDialog.Builder builder  = new Builder(Ble_Activity.this);
//				 builder.setTitle("ȷ��" ) ;
//				 builder.setMessage(intent.getExtras().getString(
//							BluetoothLeService.EXTRA_DATA)) ;
//				 builder.setPositiveButton("��" ,  null );
//				 builder.show(); 
				 buildCurrentVoltageData(intent.getExtras().getString(
							BluetoothLeService.EXTRA_DATA));
				
				
			}
		}
	};

	/* ��������״̬ */
	private void updateConnectionState(String status)
	{
		Message msg = new Message();
		msg.what = 1;
		Bundle b = new Bundle();
		b.putString("connect_state", status);
		msg.setData(b);
		//������״̬���µ�UI��textview��
		myHandler.sendMessage(msg);
		System.out.println("connect_state:" + status);

	}

	/* ��ͼ������ */
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
	* @Description: TODO(���յ���������scrollview����ʾ) 
	* @param @param rev_string(���ܵ�����)
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
//					preCurrentNum.setText("��������");
//					realCurrentNum.setText("��������");
//					preVoltageNum.setText("��������");
//					realVoltageNum.setText("��������");
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
	* @Description: TODO(������������) 
	* @param ��  
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

		// ��������,����չ�����б�ĵ�һ������
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

		// �������ݣ�������ĳһ���������������ֵ���ϣ�
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

		// ���ֲ�Σ���������ֵ����
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices)
		{

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
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
			{
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT))
				{
					// ���Զ�ȡ��ǰCharacteristic���ݣ��ᴥ��mOnDataAvailable.onCharacteristicRead()
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
				for (BluetoothGattDescriptor descriptor : descriptors)
				{
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
	/**
	 * �����ݷְ�
	 * 
	 * **/
	public int[] dataSeparate(int len)
	{   
		int[] lens = new int[2];
		lens[0]=len/20;
		lens[1]=len-20*lens[0];
		return lens;
	}
	//�¹���
	private String tenTohex(String tempC,String tempV){
		//������
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
//	�ɹ���
	private String tenTohex2(String a,String tempV){
		//����16�������⴦��
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
	 * ���Ͱ�������Ӧ�¼�����Ҫ�����ı��������
	 */
	public void onClick1(View v)
	{
		
		//1���ж��Ƿ������ˣ��ҷ���������
		if(!mConnected){
			Toast.makeText(Ble_Activity.this, "��������������", Toast.LENGTH_LONG)
			.show();
			return;
		}else if(resp_bit_data.getText().toString().equals(Constant.EMPTY_TEXT)){
			Toast.makeText(Ble_Activity.this, "��ǰ����δ��������", Toast.LENGTH_LONG)
			.show();
			return;
		}
		Log.v("btnbtnididi", send_current.getText()+"|||"+send_voltage.getText());
		//2��ȥ��Ԥ�õ�ѹ��Ԥ�õ���ֵ
		String tempV = send_voltage.getText().toString();
		String tempC =send_current.getText().toString();
		//3���ж��û��Ƿ����� begin 16���ƿ�ʼ--����û�û����ȡ֮ǰ��Ƭ�����ص�����
		if(tempC.isEmpty()){
			tempC =preCurrentNum.getText().toString().replaceAll("A", "");
		}else{
			if(Integer.valueOf(tempC)<5){
				Toast.makeText(Ble_Activity.this, "Ԥ�õ�������С��5V", Toast.LENGTH_LONG)
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
		//2����дֱ�� ���ַ���
//		target_chara.setValue(tenTohex);
		Log.v("btnbtnididi",buff.toString());
		target_chara.setValue(buff);
		mBluetoothLeService.writeCharacteristic(target_chara);
		Toast.makeText(Ble_Activity.this, "���ͳɹ�", Toast.LENGTH_SHORT)
		.show();
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
		 public void goOtherActivity(View view){

	           Toast.makeText(Ble_Activity.this, "��ת��",Toast.LENGTH_SHORT).show();
	           //����
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
//					sd.update("weldInfo",values,whereClause,whereArgs);
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

}
