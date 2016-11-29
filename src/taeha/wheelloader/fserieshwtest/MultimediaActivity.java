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

public class MultimediaActivity extends Activity {
	RelativeLayout layout;
	TextView textView;
	Button successButton, stopButton, testButton, failButton;
	Button speakerButton, AUXButton;
	int SoundState = 0, isSuccess = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_multimedia);
		layout = (RelativeLayout)findViewById(R.id.multiLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.multiText);
		textView.setTextColor(Color.WHITE);
		
		stopButton = (Button)findViewById(R.id.multiStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentMiraCastActivity = 
						new Intent(MultimediaActivity.this, SmartTerminalActivity.class);
				startActivity(intentMiraCastActivity);
				finish();
			}
		});
		failButton = (Button)findViewById(R.id.multiFail);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentMiraCastActivity = 
					new Intent(MultimediaActivity.this, SmartTerminalActivity.class);
				startActivity(intentMiraCastActivity);
				finish();
			}
		});
		
		successButton = (Button)findViewById(R.id.multiNext);
		successButton.setText("Success");
		successButton.setTextColor(Color.BLACK);
		successButton.setEnabled(false);
		successButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentMiraCastActivity = 
					new Intent(MultimediaActivity.this, SmartTerminalActivity.class);
				startActivity(intentMiraCastActivity);
				finish();
			}
		});
		speakerButton = (Button)findViewById(R.id.speakerButton);
		speakerButton.setBackgroundResource(R.drawable.menu_mode_fan_manual_btn_selected);
		speakerButton.setTextColor(Color.BLACK);
		speakerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClickInternal();
				AUXButton.setBackgroundResource(R.drawable.menu_mode_fan_manual_btn_normal);
				AUXButton.setTextColor(Color.WHITE);
				speakerButton.setBackgroundResource(R.drawable.menu_mode_fan_manual_btn_selected);
				speakerButton.setTextColor(Color.BLACK);
			}
		});
		AUXButton = (Button)findViewById(R.id.AUXButton);
		AUXButton.setBackgroundResource(R.drawable.menu_mode_fan_manual_btn_normal);
		AUXButton.setTextColor(Color.WHITE);
		AUXButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClickExternal();
				successButton.setTextColor(Color.WHITE);
				successButton.setEnabled(true);
				AUXButton.setBackgroundResource(R.drawable.menu_mode_fan_manual_btn_selected);
				AUXButton.setTextColor(Color.BLACK);
				speakerButton.setBackgroundResource(R.drawable.menu_mode_fan_manual_btn_normal);
				speakerButton.setTextColor(Color.WHITE);
			}
		});
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Multi", isSuccess);
		edit.commit();
	}
	public void ClickInternal(){	// Internal Speaker
		SoundState = 0;
		try {
			CAN1Comm.LineOutfromJNI(SoundState);
		} catch (NullPointerException e) {
			// TODO: handle exception
			Log.e("TAG","NullPointerException");
		}
		catch (Throwable t) {
			// TODO: handle exception
			Log.e("TAG","Load Library Error");
		}
	}	
	public void ClickExternal(){	// AUX
		SoundState = 1;
		try {
			CAN1Comm.LineOutfromJNI(SoundState);
		} catch (NullPointerException e) {
			// TODO: handle exception
			Log.e("TAG","NullPointerException");
		}
		catch (Throwable t) {
			// TODO: handle exception
			Log.e("TAG","Load Library Error");
		}
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
		Intent intent = new Intent(MultimediaActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(MultimediaActivity.this,CommService.class))){
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


