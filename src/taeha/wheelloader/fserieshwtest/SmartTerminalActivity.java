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

public class SmartTerminalActivity extends Activity {
	RelativeLayout smartTerminalLayout;
	Button successButton, failButton, stopButton, executeButton;
	TextView SmartTerminalDes;
	Handler handler;
	int isSuccess = 0;
	boolean flag = false; 
	public static boolean flagPdfCopy = false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smartterminal);
		
		Log.d("SmartTermianlActivity", "onCreate()");
		smartTerminalLayout = (RelativeLayout)findViewById(R.id.smartTerminalLayout);
		smartTerminalLayout.setBackgroundColor(Color.BLACK);
		SmartTerminalDes = (TextView)findViewById(R.id.smartTerminalText);
		
		executeButton = (Button)findViewById(R.id.smartTerminalExecute);
		executeButton.setTextColor(Color.WHITE);
		executeButton.setText("Execute");
		executeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent;
				intent = getPackageManager().getLaunchIntentForPackage("com.powerone.wfd.sink");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				if(intent != null){
					startActivity(intent);
				}
				
			}
		});
		
		
		
		successButton = (Button)findViewById(R.id.smartTerminalSuccess);
		successButton.setTextColor(Color.WHITE);
		successButton.setText("Success");
		successButton.setVisibility(View.INVISIBLE);
		successButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				SavePref();
				Intent intentFinalActivity = 
						new Intent(SmartTerminalActivity.this, FinalActivity.class);
				startActivity(intentFinalActivity);
				finish();
			}
		});
		
		failButton = (Button)findViewById(R.id.smartTerminalFail);
		failButton.setVisibility(View.INVISIBLE);
		failButton.setTextColor(Color.WHITE);
		failButton.setText("Fail");
		failButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Intent intentFinalActivity = 
						new Intent(SmartTerminalActivity.this, FinalActivity.class);
				startActivity(intentFinalActivity);
				finish();
				
			}
		});
		
		stopButton = (Button)findViewById(R.id.SmartTerminalStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentFinalActivity = 
						new Intent(SmartTerminalActivity.this, FinalActivity.class);
				startActivity(intentFinalActivity);
				finish();
			}
		});
//	
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub]
				if(msg.what == 0){
					Intent intent = new Intent("taeha.wheelloader.fserieshwtest.SmartTerminalActivity");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					//finish();
					flag = true;
					if(flag == true){
						successButton.setVisibility(View.VISIBLE);
						failButton.setVisibility(View.VISIBLE);
						executeButton.setVisibility(View.INVISIBLE);
						SmartTerminalDes.setText("Please Click the bottom of buttons\ndepending on the result of the test");
					}
				}
			}
		};
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Wifi", isSuccess);
		edit.commit();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// CAN1CommManager
	private CAN1CommManager CAN1Comm = null;	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.d("SmartTermianlActivity", "onResume()");
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
		Log.d("SmartTermianlActivity", "onPause()");
		try {
			CAN1Comm.SetScreenTopFlag(false);

		} catch (NullPointerException e) {
			// TODO: handle exception
		}

	}

	//Communication//////////////////////////////////////
	// Communication Service Start
	private void StartCommService() {
		Intent intent = new Intent(SmartTerminalActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(SmartTerminalActivity.this,CommService.class))){
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
			if(Data == CAN1Comm.ESC){
				Log.d("ESC", "ESCKEY");				
				handler.sendEmptyMessage(0);
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
			Log.d("TAG", "" + Data);
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
