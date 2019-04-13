package com.wtl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wtl.service.BluetoothLeService;
import com.wtl.ui.Ble_Activity;

/**
 * 测试蓝牙activity跳转时是否可以保持连接
 * 
 * @author chenyi
 *
 */
public class TestSecondConnectActivity extends Activity {
	// 文本框，显示接受的内容
	private TextView sed_connect_state, sed_resp_bit_data;
	//蓝牙连接状态
	private boolean mConnected = false;
	private String status = "disconnected";
	//蓝牙service,负责后台的蓝牙服务
	private static BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_RESP = "DEVICE_RESP";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	//蓝牙特征值
	private static BluetoothGattCharacteristic target_chara = null;
	//蓝牙名字
	private String mDeviceName;
	//蓝牙地址
	private String mDeviceAddress;
	//蓝牙信号值
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	private String respData;
	
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler() {
		// 2.重写消息处理函数
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 判断发送的消息
			case 1: {
				// 更新View
				String state = msg.getData().getString("sed_connect_state");
				sed_connect_state.setText(state);
				break;
			}
			}
			super.handleMessage(msg);
		}
	};

	public void onCreate(Bundle arg0) {
		final View view = View.inflate(this, R.layout.activity_second, null);
		setContentView(view);
		super.onCreate(arg0);
		b = getIntent().getExtras();
		System.out.println("bbbb"+b.toString());
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
//		respData=b.getString(EXTRAS_DEVICE_RESP);
		init();
		
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
		//状态
		sed_connect_state = (TextView) this.findViewById(R.id.sed_connect_state);
		sed_connect_state.setText(status);
		//返回值
		sed_resp_bit_data = (TextView) this.findViewById(R.id.sed_resp_bit_data);
		sed_resp_bit_data.setText(respData);

	}
	/* 更新连接状态 */
	private void updateConnectionState(String status)
	{
//		sed_connect_state = (TextView) this.findViewById(R.id.sed_connect_state);
//		sed_connect_state.setText(status);
//		Message msg = new Message();
//		msg.what = 1;
//		Bundle b = new Bundle();
//		b.putString("sed_connect_state", status);
//		msg.setData(b);
//		//将连接状态更新的UI的textview上
//		myHandler.sendMessage(msg);
		
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				sed_connect_state.setText("ok");
			}
		});


	}
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
				sed_resp_bit_data.setText(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
			
				
				
			}
		}
	};
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
	// Activity出来时候，绑定广播接收器，监听蓝牙连接服务传过来的事件
		@Override
		protected void onResume()
		{
			super.onResume();
			//绑定广播接收器
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			//注册实时 查看蓝牙的状态
			//TODO 轮询
			registerBoradcastReceiver();
			if (mBluetoothLeService != null)
			{    
				//根据蓝牙地址，建立连接
				final boolean result = mBluetoothLeService.connect(mDeviceAddress);
				if(result){
					status="connected";
				}else{
					status="disConnected";
					
				}
			}else{
				status="connected";
			}
			sed_connect_state.setText(status);
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
		@Override
		protected void onDestroy()
		{
			super.onDestroy();
	        //解除广播接收器
			unregisterReceiver(mGattUpdateReceiver);
			mBluetoothLeService = null;
			
		}
	    private void registerBoradcastReceiver() {
	        IntentFilter stateChangeFilter = new IntentFilter(
	                BluetoothAdapter.ACTION_STATE_CHANGED);
	        IntentFilter connectedFilter = new IntentFilter(
	                BluetoothDevice.ACTION_ACL_CONNECTED);
	        IntentFilter disConnectedFilter = new IntentFilter(
	                BluetoothDevice.ACTION_ACL_DISCONNECTED);
	        registerReceiver(stateChangeReceiver, stateChangeFilter);
	        registerReceiver(stateChangeReceiver, connectedFilter);
	        registerReceiver(stateChangeReceiver, disConnectedFilter);
	    }

	        private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (BluetoothDevice.ACTION_ACL_CONNECTED .equals(action)) {
	            	Toast.makeText(TestSecondConnectActivity.this, "ACTION_ACL_CONNECTED", Toast.LENGTH_LONG)
	    			.show();
	            }
	            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
	            	Toast.makeText(TestSecondConnectActivity.this, "ACTION_ACL_DISCONNECTED", Toast.LENGTH_LONG)
	    			.show();
	            }
	            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	            	Toast.makeText(TestSecondConnectActivity.this, "ACTION_STATE_CHANGED", Toast.LENGTH_LONG)
	    			.show();
	            }
	        }
	    };
}