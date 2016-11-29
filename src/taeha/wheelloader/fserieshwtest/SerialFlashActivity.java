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
import android.widget.Toast;

public class SerialFlashActivity extends Activity{
	RelativeLayout layout;
	TextView textView, desTextView;
	Button successButton, stopButton, sendButton, failButton;
	int sendData = 0xA5, receiveData;
	Handler serialReceiver;
	int isSuccess = 0;
	boolean serialTestFlag = false, buttonFlag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_serial);
		layout = (RelativeLayout)findViewById(R.id.serialLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.serialText);
		textView.setTextColor(Color.WHITE);
		desTextView = (TextView)findViewById(R.id.serialDesText);
		desTextView.setTextColor(Color.WHITE);
		
		stopButton = (Button)findViewById(R.id.serialStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentUARTActivity = 
						new Intent(SerialFlashActivity.this, FinalActivity.class);
				startActivity(intentUARTActivity);
				finish();	
			}
		});

		sendButton = (Button)findViewById(R.id.serialSend);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//FLASHTEST ADDRESS : 0x0D
				CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_FLASHTEST, 0, sendData);
				
				if(serialTestFlag == true){
					isSuccess = 1;
					SavePref();
					if(buttonFlag == true){
						Log.i("onClick", "CallSubActivity");
						Intent intentUARTActivity = 
								new Intent(SerialFlashActivity.this, CANActivity.class);
						startActivity(intentUARTActivity);
						finish();	
					} 
				}
				else{
					isSuccess = 2;
					SavePref();
					if(buttonFlag == true){
						Log.i("onClick", "CallSubActivity");
						Intent intentUARTActivity = 
							new Intent(SerialFlashActivity.this, CANActivity.class);
						startActivity(intentUARTActivity);
						finish();
					}
				}
				buttonFlag = true;
			}
		});
		
		serialReceiver = new Handler(){
			public void handleMessage(Message msg){
				if(sendData == receiveData){
					sendButton.setText("Next");
					desTextView.setText("");
					desTextView.setBackgroundResource(R.drawable.success);
					serialTestFlag = true;
					
				}else{
					sendButton.setText("Fail");
					desTextView.setText("");
					desTextView.setBackgroundResource(R.drawable.fail);
					serialTestFlag = false;
				}
			}
		};
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Serial", isSuccess);
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
		Intent intent = new Intent(SerialFlashActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(SerialFlashActivity.this,CommService.class))){
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

		}

		@Override
		public void FlashCallBack(int Data) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d("TAG", "" + Data);
			receiveData = Data;
			serialReceiver.sendMessage(serialReceiver.obtainMessage(receiveData));
		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopCommService();
	}
	
}
