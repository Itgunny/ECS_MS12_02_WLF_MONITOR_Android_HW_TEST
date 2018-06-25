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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KEYPADTwoActivity extends Activity{
	RelativeLayout layout;
	TextView textView;
	Button successButton, stopButton, failButton;
	ImageView[] imageView = new ImageView[7];
	int longFlag = 0;
	Handler keyBoardHandler;
	int[] buttonFlag = new int[4];
	int[] hwButtonCount = new int[4];
	int count = 0, isSuccess = 0;
	Drawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_keypadtwo);
		layout = (RelativeLayout)findViewById(R.id.keypadtwoLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.keypadtwoText);
		textView.setTextColor(Color.WHITE);
		
		imageView[0] = (ImageView)findViewById(R.id.doubleKeyView1);
		imageView[1] = (ImageView)findViewById(R.id.doubleKeyView2);
		imageView[2] = (ImageView)findViewById(R.id.doubleKeyView3);
		imageView[3] = (ImageView)findViewById(R.id.doubleKeyView4);
		imageView[4] = (ImageView)findViewById(R.id.doubleKeyView5);
		imageView[5] = (ImageView)findViewById(R.id.doubleKeyView6);
		imageView[6] = (ImageView)findViewById(R.id.doubleKeyView7);
		
		
		stopButton = (Button)findViewById(R.id.kepadtwoStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentCAMActivity = 
						new Intent(KEYPADTwoActivity.this, FinalActivity.class);
				startActivity(intentCAMActivity);
				finish();
			}
		});
		
		failButton = (Button)findViewById(R.id.keypadtwoFail);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentCAMActivity = 
					new Intent(KEYPADTwoActivity.this, CAMActivity.class);
				startActivity(intentCAMActivity);
				finish();
			}
		});
		
		successButton = (Button)findViewById(R.id.keypadtwoNext);
		successButton.setTextColor(Color.BLACK);
		successButton.setEnabled(false);
		successButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentCAMActivity = 
					new Intent(KEYPADTwoActivity.this, CAMActivity.class);
				startActivity(intentCAMActivity);
				finish();
			}
		});
		
		keyBoardHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(completeKeyTest() > 3){
					successButton.setTextColor(Color.WHITE);
					successButton.setEnabled(true);
				}
				else{
					successButton.setEnabled(false);
					successButton.setTextColor(Color.BLACK);
				}
					changeImageView(longFlag);
					
			}
		};
		
	}
	public int completeKeyTest(){
		int sum = 0;
		for(int i = 0; i < buttonFlag.length; i++ ){
			sum += buttonFlag[i];
		}
		Log.d("Sum", "SUm : " + sum);
		return sum;
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("KeypadLong", isSuccess);
		edit.commit();
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void changeImageView(int longFlag){
		switch(longFlag){
		case 1:
			if(hwButtonCount[0] % 2 == 1){
				imageView[0].setImageResource(R.drawable.keypad2_01_s);
				this.buttonFlag[0] = 1;
			}
			else{
				imageView[0].setImageResource(R.drawable.keypad2_01_n);
				this.buttonFlag[0] = 0;
			}
			break;
		case 2:
			if(hwButtonCount[1] % 2 == 1){
				imageView[1].setImageResource(R.drawable.keypad2_02_s);
				this.buttonFlag[1] = 1;
			}
			else{
				imageView[1].setImageResource(R.drawable.keypad2_02_n);
				this.buttonFlag[1] = 0;
			}
			break;
		case 3:
			if(hwButtonCount[2] % 2 == 1){
				imageView[2].setImageResource(R.drawable.keypad2_03_1_s);
				imageView[3].setImageResource(R.drawable.keypad2_03_2_s);
				this.buttonFlag[2] = 1;
			}
			else{
				imageView[2].setImageResource(R.drawable.keypad2_03_1_n);
				imageView[3].setImageResource(R.drawable.keypad2_03_2_n);
				this.buttonFlag[2] = 0;
			}			
			break;
		case 4:
			if(hwButtonCount[3] % 2 == 1){
				imageView[4].setImageResource(R.drawable.keypad2_04_1_s);
				imageView[5].setImageResource(R.drawable.keypad2_04_2_s);
				imageView[6].setImageResource(R.drawable.keypad2_04_3_s);
				this.buttonFlag[3] = 1;
			}
			else{
				imageView[4].setImageResource(R.drawable.keypad2_04_1_n);
				imageView[5].setImageResource(R.drawable.keypad2_04_2_n);
				imageView[6].setImageResource(R.drawable.keypad2_04_3_n);
				this.buttonFlag[3] = 0;
			}
			break;
		}
	}

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
		Intent intent = new Intent(KEYPADTwoActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(KEYPADTwoActivity.this,CommService.class))){
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
			if(Data == CAN1Comm.LONG_ENTER){
				longFlag = 1;
				hwButtonCount[0]++;
			}
			if(Data == 16908288){
				longFlag = 2;
				hwButtonCount[1]++;
			}
			if(Data == CAN1Comm.LONG_LEFT_RIGHT)
			{
				longFlag = 3;
				hwButtonCount[2]++;
			}
			if(Data == CAN1Comm.LONG_LEFT_RIGHT_ENTER){
				longFlag = 4;
				hwButtonCount[3]++;
			}
			keyBoardHandler.sendMessage(keyBoardHandler.obtainMessage(longFlag));
			
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
