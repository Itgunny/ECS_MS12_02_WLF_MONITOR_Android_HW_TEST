package taeha.wheelloader.fserieshwtest;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BuzzerActivity extends Activity {

	RelativeLayout layout;
	TextView textView;
	Button successButton, stopButton, failButton;
	ImageButton buzzerOffButton;
	int isSuccess = 0;
	boolean buzzerTestFlag;
	Handler keyBoardHandler;
	int count = 0;
	int status = CAN1CommManager.BUZZER_ON;
	Drawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_buzzer);
		layout = (RelativeLayout)findViewById(R.id.buzzerLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.buzzerText);
		textView.setTextColor(Color.WHITE);
		
		successButton = (Button)findViewById(R.id.buzzerNext);
		successButton.setEnabled(false);
		successButton.setTextColor(Color.BLACK);
		successButton.setText("Success");
		successButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(buzzerTestFlag == true){
					isSuccess = 1;
					SavePref();
					CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_OFF);
					Log.i("onClick", "CallSubActivity");
					Intent intentKEPADActivity = 
							new Intent(BuzzerActivity.this, KEYPADActivity.class);
					startActivity(intentKEPADActivity);
					finish();
				}
			}
		});
		
		failButton = (Button)findViewById(R.id.buzzerFail);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_OFF);
				Log.i("onClick", "CallSubActivity");
				Intent intentKEPADActivity = 
						new Intent(BuzzerActivity.this, KEYPADActivity.class);
				startActivity(intentKEPADActivity);
				finish();
			}
		});
		stopButton = (Button)findViewById(R.id.buzzerStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_OFF);
				Intent intentKEPADActivity = 
						new Intent(BuzzerActivity.this, FinalActivity.class);
				startActivity(intentKEPADActivity);
				finish();
			}
		});
		
		buzzerOffButton = (ImageButton)findViewById(R.id.buzzerOff);
		buzzerOffButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_OFF);
				buzzerTestFlag = true;
				if(status == CAN1Comm.BUZZER_ON){
					status = CAN1CommManager.BUZZER_OFF;
					BuzzerStatus(status);
				}else
				{
					status = CAN1CommManager.BUZZER_ON;
					BuzzerStatus(status);
				}	
				if(count == 3){
					successButton.setTextColor(Color.WHITE);
					successButton.setEnabled(true);	
				}
			}
		});
		keyBoardHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
				if(msg.what == 0){
					BuzzerStatus(status);
					buzzerTestFlag = true;

					
					if(count == 3){
						successButton.setEnabled(true);
						successButton.setTextColor(Color.WHITE);
					}
				}
			}
		};
	}
	
	public void BuzzerStatus(int Data){
		switch(Data){
		case CAN1CommManager.BUZZER_ON:
			CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_ON);
			buzzerOffButton.setImageResource(R.drawable.button_buzzer_off);
			break;
		case CAN1CommManager.BUZZER_OFF:
			CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_OFF);
			buzzerOffButton.setImageResource(R.drawable.button_buzzer_on);
			count++;
			break;
		default:
			break;
		}
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Buzzer", isSuccess);
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
		Intent intent = new Intent(BuzzerActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
		
		BuzzerStatus(status);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(BuzzerActivity.this,CommService.class))){
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
			Log.d("Key","KeybuttonCallback");
			
			if(Data == CAN1Comm.RIGHT){
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_OFF);
				
				status = CAN1Comm.BUZZER_OFF;
				keyBoardHandler.sendEmptyMessage(0);
			}					
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

		}

	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopCommService();
	}
	
}
