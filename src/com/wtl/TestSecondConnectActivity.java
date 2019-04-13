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
 * ��������activity��תʱ�Ƿ���Ա�������
 * 
 * @author chenyi
 *
 */
public class TestSecondConnectActivity extends Activity {
	// �ı�����ʾ���ܵ�����
	private TextView sed_connect_state, sed_resp_bit_data;
	//��������״̬
	private boolean mConnected = false;
	private String status = "disconnected";
	//����service,�����̨����������
	private static BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//����4.0��UUID,����0000ffe1-0000-1000-8000-00805f9b34fb�ǹ��ݻ����Ϣ�Ƽ����޹�˾08����ģ���UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_RESP = "DEVICE_RESP";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	//��������ֵ
	private static BluetoothGattCharacteristic target_chara = null;
	//��������
	private String mDeviceName;
	//������ַ
	private String mDeviceAddress;
	//�����ź�ֵ
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	private String respData;
	
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler() {
		// 2.��д��Ϣ������
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// �жϷ��͵���Ϣ
			case 1: {
				// ����View
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
//		respData=b.getString(EXTRAS_DEVICE_RESP);
		init();
		
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
		//״̬
		sed_connect_state = (TextView) this.findViewById(R.id.sed_connect_state);
		sed_connect_state.setText(status);
		//����ֵ
		sed_resp_bit_data = (TextView) this.findViewById(R.id.sed_resp_bit_data);
		sed_resp_bit_data.setText(respData);

	}
	/* ��������״̬ */
	private void updateConnectionState(String status)
	{
//		sed_connect_state = (TextView) this.findViewById(R.id.sed_connect_state);
//		sed_connect_state.setText(status);
//		Message msg = new Message();
//		msg.what = 1;
//		Bundle b = new Bundle();
//		b.putString("sed_connect_state", status);
//		msg.setData(b);
//		//������״̬���µ�UI��textview��
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
				sed_resp_bit_data.setText(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));
			
				
				
			}
		}
	};
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
	// Activity����ʱ�򣬰󶨹㲥�������������������ӷ��񴫹������¼�
		@Override
		protected void onResume()
		{
			super.onResume();
			//�󶨹㲥������
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			//ע��ʵʱ �鿴������״̬
			//TODO ��ѯ
			registerBoradcastReceiver();
			if (mBluetoothLeService != null)
			{    
				//����������ַ����������
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
		@Override
		protected void onDestroy()
		{
			super.onDestroy();
	        //����㲥������
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