package taeha.wheelloader.fserieshwtest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MiraCastActivity extends Activity {
	RelativeLayout layout;

	public static TextView textView, textStatus, textDes;
	Button successButton, stopButton, startButton, failButton;
	int isSuccess = 0;
	ListView apList;
	ArrayAdapter adapter;
	Handler wifiHandler = new Handler();
	public static int count = 0;
	int buttonCount = 0;
	int EditCount = 0;
	boolean isConnect = false;
	
	//Wifi
	WifiManager wifi;
	public static WifiInfo info;
	BroadcastReceiver receiver;
	ConnectivityManager conManager;
	NetworkInfo conWifi;
	List<ScanResult> scanResult;
	ArrayList<String> scanData;
	String TEXT = "";
	ScanResult sR;
	WifiConfiguration wifiCon;
	int networkId;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mira);
		//Setup UI
		layout = (RelativeLayout)findViewById(R.id.miraLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.miraText);
		textView.setTextColor(Color.WHITE);
		
		textStatus = (TextView) findViewById(R.id.textStatus);
		textStatus.setTextColor(Color.WHITE);
		
		textDes = (TextView) findViewById(R.id.wifiDes);
		textDes.setTextColor(Color.WHITE);
		
		apList = (ListView)findViewById(R.id.apList);
		apList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		apList.setSelector(new PaintDrawable(0xffffff));
		scanData = new ArrayList<String>();
		
		conManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		conWifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		//Setup WiFi
		wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(true);
		
		//Get WiFi status
		info = wifi.getConnectionInfo();
		textStatus.setText("Wifi Status : "+ info.getSSID() + " RSSI : " + info.getRssi() + "dBm");
		
		stopButton = (Button)findViewById(R.id.miraStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentMultiActivity = 
						new Intent(MiraCastActivity.this, FinalActivity.class);
				startActivity(intentMultiActivity);
				finish();
			}
		});
		wifiHandler.post(callback);
		
		startButton = (Button)findViewById(R.id.miraStart);
		startButton.setTextColor(Color.WHITE);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(buttonCount == 0){
					Log.d("WiFi", "onClick() wifi.startScan()");
					if(wifi.isWifiEnabled()){
						wifi.startScan();
					}
					startButton.setText("Connect");
					buttonCount++;
				}else if(buttonCount == 1){
					
					//Connect
					if(EditCount > 0){
						wifiCon = new WifiConfiguration();
						wifiCon.SSID = "\"".concat(sR.SSID).concat("\"");
						wifiCon.status = WifiConfiguration.Status.DISABLED;
						wifiCon.priority = 40;
						if(sR.capabilities.contains("WEP")){
							wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
							wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
							wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
							wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
							wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
							wifiCon.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
							wifiCon.wepKeys[0] = "\"".concat(WifiInputPasswordDialog.password).concat("\"");
							wifiCon.wepTxKeyIndex = 0;
						}else if(sR.capabilities.contains("WPA")){
							wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
							wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
							wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
							wifiCon.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
							wifiCon.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
							wifiCon.preSharedKey = "\"".concat(WifiInputPasswordDialog.password).concat("\"");
						}else{
							wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
							wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
							wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
							wifiCon.allowedAuthAlgorithms.clear();
							wifiCon.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
							wifiCon.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
						}
						networkId = wifi.addNetwork(wifiCon);
						wifi.disableNetwork(networkId);
						if(networkId != -1){
							wifi.enableNetwork(networkId, true);
						}else{
							startButton.setText("Fail");
						}
						buttonCount++;
					}
				}else{
					if(startButton.getText()=="Success" && info.getRssi() >= -75){
						isSuccess = 1;	
						SavePref();
						Log.i("onClick", "CallSubActivity");
						Intent intentMultiActivity = 
								new Intent(MiraCastActivity.this, FinalActivity.class);
						startActivity(intentMultiActivity);
						finish();
					}
					else if(startButton.getText() == "Fail" && info.getRssi() < -75){
						isSuccess = 2;	
						SavePref();
						Log.i("onClick", "CallSubActivity");
						Intent intentMultiActivity = 
								new Intent(MiraCastActivity.this, FinalActivity.class);
						startActivity(intentMultiActivity);
						finish();
					}
				}
			}
		});

		
		apList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d("TAG", "" + position);
				buttonCount = 1;
				EditCount++;
				WifiInputPasswordDialog.password ="";
				startButton.setText("Connect");
				sR= scanResult.get(position);
				Log.d("TAG", "" + sR.capabilities);
				if(sR.capabilities.contains("WEP")){	
					WifiInputPasswordDialog wifiDialog = new WifiInputPasswordDialog(MiraCastActivity.this, sR);
					wifiDialog.show();
				} else if(sR.capabilities.contains("WPA")){
					WifiInputPasswordDialog wifiDialog = new WifiInputPasswordDialog(MiraCastActivity.this, sR);
					wifiDialog.show();
				} else{
					
				}
			}
		});
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION){
					findWifiAp();	
				}else if(action == WifiManager.WIFI_STATE_CHANGED_ACTION){
					int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
					if(state == WifiManager.WIFI_STATE_ENABLED){
						Message msg = handler.obtainMessage();
						msg.what = 1;
						handler.sendEmptyMessage(1);
				}else if(action == ConnectivityManager.CONNECTIVITY_ACTION){

					}
				}
			}
		};
		registerReceiver(receiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what == 1){
				if(conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() && info.getRssi() >= -75)	{
					isConnect = true;
					if(buttonCount == 2){
						startButton.setText("Success");
					}
					
				}else if(!conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() || info.getRssi() < -75){
					isConnect = false;
					if(buttonCount == 2){
						startButton.setText("Fail");
					}
				}	
			}
		}
	};
	public Runnable callback = new Runnable(){
		public void run(){
			wifiHandler.postDelayed(callback,200);
			wifi = (WifiManager)getSystemService(WIFI_SERVICE);
			info = wifi.getConnectionInfo();		
			TEXT = "Wifi Name : " + info.getSSID() + "  RSSI : " + info.getRssi() + "dBm";
			textStatus.setText(TEXT);
			handler.sendEmptyMessage(1);
		}
	};
	@SuppressWarnings("unchecked")
	public void findWifiAp(){
		scanResult = wifi.getScanResults();		
		for(int i = 0; i < scanResult.size(); i++){
			ScanResult result = scanResult.get(i);
			scanData.add(result.SSID.toString());
		}
		if(scanResult.size() == 1 || scanResult.size() == 0){
			for(int i = 0; i < scanResult.size(); i++){
				ScanResult result = scanResult.get(i);
				scanData.add(result.SSID.toString());	
			}
		}
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, scanData);
		apList.setAdapter(adapter);
		adapter.notifyDataSetInvalidated();

	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Wifi", isSuccess);
		edit.commit();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
