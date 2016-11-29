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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CAMActivity extends Activity {
	RelativeLayout layout;
	TextView textView, desTextView;
	Button stopButton, startButton, failButton;
	int camChannel = 0;
	int isSuccess = 0;
	boolean camFlag = false;
	boolean buttonFlag = false;
	public Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cam);
		layout = (RelativeLayout)findViewById(R.id.camLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.camText);
		textView.setTextColor(Color.WHITE);
		
		desTextView = (TextView)findViewById(R.id.camDesText);
		desTextView.setTextColor(Color.WHITE);
		
		stopButton = (Button)findViewById(R.id.camStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentMultiActivity = 
						new Intent(CAMActivity.this, FinalActivity.class);
				startActivity(intentMultiActivity);
				finish();
			}
		});
		failButton = (Button)findViewById(R.id.camFail);
		failButton.setVisibility(View.INVISIBLE);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentMultiActivity = 
					new Intent(CAMActivity.this, MultimediaActivity.class);
				startActivity(intentMultiActivity);
				finish();
			}
		});
		startButton = (Button)findViewById(R.id.camStart);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(camFlag == false){
					CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_CAM, 0);
					camChannel++;	
					startButton.setEnabled(camFlag);
				}else{
					isSuccess = 1;
					SavePref();
					Log.i("onClick", "CallSubActivity");
					Intent intentMultiActivity = 
						new Intent(CAMActivity.this, MultimediaActivity.class);
					startActivity(intentMultiActivity);
					finish();
				}
			}
		});
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 0){
					CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_CAM, 0xff);
					camFlag = true;
					startButton.setEnabled(camFlag);
					startButton.setText("Success");
					startButton.setX(500);
					failButton.setVisibility(View.VISIBLE);
					failButton.setX(100);
					desTextView.setText("");
					desTextView.setText("Please Click the bottom of buttons\ndepending on the result of the test");
				}
			}
			
		};
	}
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(camChannel == 0){
				CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_CAM, 0);
				camChannel++;
			}
			else if(camChannel == 1){
				CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_CAM, 1);
				camChannel++;
			}
			else if(camChannel == 2){
				CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_CAM, 2);
				camChannel++;
			}
			else if(camChannel == 3){
				CAN1Comm.TxCMDToMCU(CAN1Comm.CMD_CAM, 3);
				camChannel++;
				buttonFlag = true;
				if(camChannel==4){
					camChannel = 0;
				}
			}
			break;
		}
		return true;
	}
	
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Cam", isSuccess);
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
		Intent intent = new Intent(CAMActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(CAMActivity.this,CommService.class))){
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
			if(Data == CAN1Comm.CAMERA){
				if(buttonFlag == true){
					handler.sendEmptyMessage(0);
				}
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
