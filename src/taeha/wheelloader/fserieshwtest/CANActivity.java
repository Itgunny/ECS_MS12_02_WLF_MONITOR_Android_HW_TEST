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

public class CANActivity extends Activity{
	RelativeLayout layout;
	TextView textView, desTextView;
	Button successButton, stopButton, failButton, txButton;
	int isSuccess = 0;
	int checkCanData = 0, buttonFlag = 0;
	boolean canTestFlag = true;
	Handler canHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_can);
		layout = (RelativeLayout)findViewById(R.id.canLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.canText);
		textView.setTextColor(Color.WHITE);
		
		desTextView = (TextView)findViewById(R.id.canDesText);
		desTextView.setTextColor(Color.WHITE);
		
		
		stopButton = (Button)findViewById(R.id.canStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentBuzzerActivity = 
						new Intent(CANActivity.this, FinalActivity.class);
				startActivity(intentBuzzerActivity);
				finish();
			}
		});
		txButton = (Button)findViewById(R.id.canTx);
		txButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				

				canHandler.sendEmptyMessage(0);
				buttonFlag++;
					if(canTestFlag == true){
						isSuccess = 1;
						SavePref();
						Log.i("onClick", "CallSubActivity");
						if(buttonFlag == 2){
							CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_ON);
							Intent intentBuzzerActivity = 
								new Intent(CANActivity.this, BuzzerActivity.class);
							startActivity(intentBuzzerActivity);
							finish();
						}
					}
					else{
						if(buttonFlag == 2){
							isSuccess = 2;
							SavePref();
							Log.i("onClick", "CallSubActivity");
							CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_BUZ, CAN1Comm.BUZZER_ON);
							Intent intentBuzzerActivity = 
									new Intent(CANActivity.this, BuzzerActivity.class);
							startActivity(intentBuzzerActivity);
							finish();
						}
					}
				}
		});
		canHandler = new Handler(){
			public void handleMessage(Message msg){
				CAN1Comm.Set_MessageType_PGN61184_21(21);
				CAN1Comm.TxCANToMCU(21);
				checkCanData = CAN1Comm.Get_RecvSeedFlag_PGN61184_22();
				Log.d("CAN", "" + checkCanData);
				canHandler.sendEmptyMessageDelayed(0, 200);
				if(checkCanData == 1){
					canTestFlag = true;
					desTextView.setBackgroundResource(R.drawable.success);
					desTextView.setText("");
					txButton.setText("Next");
				}else{
					canTestFlag = false;
					desTextView.setBackgroundResource(R.drawable.fail);
					desTextView.setText("");
					txButton.setText("Fail");
				}
			}
		};
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Can", isSuccess);
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
		Intent intent = new Intent(CANActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(CANActivity.this,CommService.class))){
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
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopCommService();
		canHandler.removeMessages(0);
	}
}
