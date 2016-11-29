package taeha.wheelloader.fserieshwtest;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EEPROMActivity extends Activity{
	RelativeLayout layout;
	TextView textView, desTextView;
	Button successButton, stopButton, sendButton;
	Handler eepReceiver;
	int sendData = 0xA5, receiveData;
	int isSuccess = 0;
	boolean eepTestFlag = false, buttonFlag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_eeprom);
		
		layout = (RelativeLayout)findViewById(R.id.eepromLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.eepromText);
		textView.setTextColor(Color.WHITE);
		
		desTextView = (TextView)findViewById(R.id.eepromDesText);
		desTextView.setTextColor(Color.WHITE);
		
		stopButton = (Button)findViewById(R.id.eepromStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentSerialFlashActivity = 
						new Intent(EEPROMActivity.this, FinalActivity.class);	
				startActivity(intentSerialFlashActivity);
			}
		});
		
		sendButton = (Button)findViewById(R.id.eepromSend);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//EEPROM ADDRESS : 0x0C
				CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_EEPROMTEST, 0, sendData);
				if(eepTestFlag == true){
					isSuccess = 1;
					SavePref();
					if(buttonFlag == true){
						Log.i("onClick", "CallSubActivity");
						Intent intentSerialFlashActivity = 
								new Intent(EEPROMActivity.this, SerialFlashActivity.class);	
						startActivity(intentSerialFlashActivity);
						finish();
					}
				}else{
					isSuccess = 2;
					SavePref();
					if(buttonFlag == true){
						Log.i("onClick", "CallSubActivity");
						Intent intentSerialFlashActivity = 
								new Intent(EEPROMActivity.this, SerialFlashActivity.class);	
						startActivity(intentSerialFlashActivity);
						finish();
					}
				}
				buttonFlag = true;
			}
			
		});

		eepReceiver = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(sendData == receiveData){
					sendButton.setText("Next");
					desTextView.setText("");
					desTextView.setBackgroundResource(R.drawable.success);
					eepTestFlag = true;
				}
				else{
					sendButton.setText("Fail");
					desTextView.setText("");
					desTextView.setBackgroundResource(R.drawable.fail);
					eepTestFlag = false;
				}
			}
			
		};
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Eeprom", isSuccess);
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
		Intent intent = new Intent(EEPROMActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(EEPROMActivity.this,CommService.class))){
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

		}
	};


	// Service Callback
	ICAN1CommManagerCallback mCallback = new ICAN1CommManagerCallback.Stub() {

		@Override
		public void KeyButtonCallBack(int Data) throws RemoteException {
			// TODO Auto-generated method stub
		}

		@Override
		public void CallbackFunc(int Data) throws RemoteException {
			// TODO Auto-generated method stub
		}

		@Override
		public void CIDCallBack() throws RemoteException {
			// TODO Auto-generated method stub
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
			Log.d("TAG", "" + Data);
			receiveData = Data;
			eepReceiver.sendMessage(eepReceiver.obtainMessage(receiveData));
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
		stopCommService();
	}
	
}
