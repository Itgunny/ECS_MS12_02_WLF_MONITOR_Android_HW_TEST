package taeha.wheelloader.fserieshwtest;


import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeSetActivity extends Activity{
	public static final String TAG = "TimeSetActivity";
	int Year, Month, Date, Hour, Min, Sec;
	int MCUYear = 0, MCUMonth = 0, MCUDate = 0, MCUHour = 0, MCUMin = 0, MCUSec = 0;
	DatePicker datePicker;
	TimePicker timePicker;
	RelativeLayout layout;
	TextView textView, timeTextView;
	Button settingButton, stopButton, failButton;
	Handler timerHandler;
	String dateMsg;
	SharedPreferences sp;
	int isfail, setFlag = 0, buttonFlag = 0;
	int isSuccess;
	int lotFlag = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_timeset);


		layout = (RelativeLayout)findViewById(R.id.timeSetLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.timeText);
		textView.setTextColor(Color.WHITE);
		
		timeTextView = (TextView)findViewById(R.id.expTimeText);
		timeTextView.setTextColor(Color.WHITE);
		
		settingButton = (Button)findViewById(R.id.timeSetButton);
		settingButton.setText("Success");
		settingButton.setVisibility(View.INVISIBLE);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				SavePref();
				
				Log.i("onClick", "CallSubActivity");
				Intent intentUSBMemoryActivity= 
						new Intent(TimeSetActivity.this, USBMemoryActivity.class);
				startActivity(intentUSBMemoryActivity);
				finish();
			}
		});
		failButton = (Button)findViewById(R.id.timeFailButton);
		failButton.setVisibility(View.INVISIBLE);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentUSBMemoryActivity= 
				new Intent(TimeSetActivity.this, USBMemoryActivity.class);
				startActivity(intentUSBMemoryActivity);
				finish();
			}
		});
		stopButton = (Button)findViewById(R.id.timeSetStop);
		stopButton.setVisibility(View.INVISIBLE);
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentUSBMemoryActivity= 
				new Intent(TimeSetActivity.this, FinalActivity.class);
				startActivity(intentUSBMemoryActivity);
				finish();
			}
		});
		timerHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == 0){
					MCUYear = CAN1Comm.Get_RTColock_Year();
					MCUMonth = CAN1Comm.Get_RTColock_Month();
					MCUDate = CAN1Comm.Get_RTColock_Date();
					MCUHour = CAN1Comm.Get_RTColock_Hour();
					MCUMin = CAN1Comm.Get_RTColock_Min();
					MCUSec = CAN1Comm.Get_RTColock_Sec();
					dateMsg = String.format("%d / %d / %d / %dh : %dm : %ds", MCUYear+2000, MCUMonth, MCUDate, MCUHour, MCUMin, MCUSec);
					if(lotFlag == 1){
						timeTextView.setText(dateMsg + "\nTurn off the machine.");
					}else if(lotFlag == 2){
						timeTextView.setText(dateMsg);
					}
					timerHandler.sendEmptyMessageDelayed(0, 200);
				}
			}
		};
		getLot();

	}
	public void SaveLot(){
		SharedPreferences sp = getSharedPreferences("LotNumber", 0);
		SharedPreferences.Editor prefEditor = sp.edit();
		prefEditor.putString("fileName", MainActivity.fileName);
		prefEditor.commit();
		Log.d(TAG, "FileName : " + sp.getString("fileName", MainActivity.fileName));
	}
	public void getLot(){
		String str ="";
		sp = getSharedPreferences("LotNumber", 0);
		sp.getString("fileName", str);
		Log.d(TAG, "fileName : " + sp.getString("fileName", str));
		if(sp.getString("fileName", str).equals("")){
			settingButton.setVisibility(View.INVISIBLE);
			failButton.setVisibility(View.INVISIBLE);
			stopButton.setVisibility(View.INVISIBLE);
			lotFlag = 1;
		}else{
			settingButton.setVisibility(View.VISIBLE);
			failButton.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.VISIBLE);
			lotFlag = 2;
		}
	}
	//save
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", isSuccess);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putString("Timeset", dateMsg);
		edit.commit();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// CAN1CommManager
	private CAN1CommManager CAN1Comm = null;	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			StartCommService();
			CAN1Comm.SetScreenTopFlag(true);
		} catch (RuntimeException e) {
		} 
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			CAN1Comm.SetScreenTopFlag(false);

		} catch (NullPointerException e) {
			// TODO: handle exception
		}

	}

	//Communication//////////////////////////////////////
	// Communication Service Start
	private void StartCommService() {
		Intent intent = new Intent(TimeSetActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(TimeSetActivity.this,CommService.class))){
		}
		else{
		}
		try {
			CAN1Comm.unregisterCallback(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Service Connection
	private ServiceConnection serConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			StartCommService();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			boolean Success;
			CAN1Comm = CAN1CommManager.getInstance();
			try {
				Success = CAN1Comm.registerCallback(mCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CAN1Comm.SetScreenTopFlag(true);
			if(MainActivity.LotPass == true || lotFlag == 2){
				Log.d("LOT_PASS", "LOTPASS : " +MainActivity.LotPass);
				timerHandler.sendEmptyMessage(0);
			}

		}
	};


	// Service Callback
	ICAN1CommManagerCallback mCallback = new ICAN1CommManagerCallback.Stub() {

		@Override
		public void KeyButtonCallBack(int Data) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("Key","KeybuttonCallback");
		}

		@Override
		public void CallbackFunc(int Data) throws RemoteException {
			// TODO Auto-generated method stub
		}

		@Override
		public void CIDCallBack() throws RemoteException {
			// TODO Auto-generated method stub
			Log.i(TAG, "CIDCallBack");
			if(MainActivity.LotPass == false){
				Log.d("LOT_PASS", "LOTPASS : " +MainActivity.LotPass);
				int SetHour, SetMinute;
				byte[] ComponentBasicInformation;
				ComponentBasicInformation = new byte[37];
				ComponentBasicInformation = CAN1Comm.Get_ComponentBasicInformation_1698_PGN65330_MONITOR();
				SetHour = CAN1Comm.Get_ComponentCode_1699_PGN65330_MONITOR();
				SetMinute = CAN1Comm.Get_ManufacturerCode_1700_PGN65330_MONITOR();
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_RTC,ComponentBasicInformation[0],ComponentBasicInformation[1],ComponentBasicInformation[2],0x01,SetHour,SetMinute,0x00,0x00);
				SaveLot();
				timerHandler.sendEmptyMessage(0);
				
			}
		}

		@Override
		public void ASCallBack() throws RemoteException {
			// TODO Auto-generated method stub
		}

		@Override
		public void StopCommServiceCallBack() throws RemoteException {
			// TODO Auto-generated method stub
			stopCommService();
		}

		@Override
		public void EEPRomCallBack(int Data) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void FlashCallBack(int Data) throws RemoteException {
			// TODO Auto-generated method stub

		}

	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//stopCommService();
	}
	
}
