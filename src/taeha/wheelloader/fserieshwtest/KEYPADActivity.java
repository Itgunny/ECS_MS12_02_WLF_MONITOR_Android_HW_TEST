package taeha.wheelloader.fserieshwtest;

import java.util.Random;


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

public class KEYPADActivity extends Activity {
	public static int[] keypadNum = {CAN1CommManager.MENU, CAN1CommManager.ESC, CAN1CommManager.LEFT, CAN1CommManager.RIGHT,
		CAN1CommManager.ENTER, CAN1CommManager.CAMERA, CAN1CommManager.MAINLIGHT, CAN1CommManager.WORKLIGHT, CAN1CommManager.AUTO_GREASE,
		CAN1CommManager.QUICK_COUPLER, CAN1CommManager.RIDE_CONTROL,  CAN1CommManager.WORK_LOAD, CAN1CommManager.BEACON_LAMP, 
		CAN1CommManager.REAR_WIPER, CAN1CommManager.MIRROR_HEAT, CAN1CommManager.AUTOPOSITION, CAN1CommManager.FINEMODULATION,
		CAN1CommManager.MENU_LEFT, CAN1CommManager.MENU_RIGHT, CAN1CommManager.MENU_ENTER, CAN1CommManager.LEFT_RIGHT, CAN1CommManager.LEFT_RIGHT_ENTER};
	RelativeLayout layout;
	TextView textView;
	Button successButton, stopButton, failButton;
	int keyValue;
	ImageView[] imageView = new ImageView[18];
	int[] buttonFlag = new int[18];
	int[] hwbuttonCount = new int[18];
	Handler keyBoardHandler;
	int flag = 0, isSuccess = 0;
	Random randomNumber = new Random();
	Drawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_keypad);
		layout = (RelativeLayout)findViewById(R.id.keypadLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.keypadText);
		textView.setTextColor(Color.WHITE);
		setButtonFlag();
		
		imageView[0] = (ImageView)findViewById(R.id.imageView1);
		imageView[1] = (ImageView)findViewById(R.id.imageView2);
		imageView[2] = (ImageView)findViewById(R.id.imageView3);
		imageView[3] = (ImageView)findViewById(R.id.imageView4);
		imageView[4] = (ImageView)findViewById(R.id.imageView5);
		imageView[5] = (ImageView)findViewById(R.id.imageView6);
		imageView[6] = (ImageView)findViewById(R.id.imageView7);
		imageView[7] = (ImageView)findViewById(R.id.imageView8);
		imageView[8] = (ImageView)findViewById(R.id.imageView9);
		imageView[9] = (ImageView)findViewById(R.id.imageView10);
		imageView[10] = (ImageView)findViewById(R.id.imageView11);
		imageView[11] = (ImageView)findViewById(R.id.imageView12);
		imageView[12] = (ImageView)findViewById(R.id.imageView13);
		imageView[13] = (ImageView)findViewById(R.id.imageView14);
		imageView[14] = (ImageView)findViewById(R.id.imageView15);
		imageView[15] = (ImageView)findViewById(R.id.imageView16);
		imageView[16] = (ImageView)findViewById(R.id.imageView17);		
		imageView[17] = (ImageView)findViewById(R.id.imageView18);
		
		stopButton = (Button)findViewById(R.id.keypadStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentKEPADTwoActivity = 
						new Intent(KEYPADActivity.this, FinalActivity.class);
				startActivity(intentKEPADTwoActivity);
				finish();
			}
		});
		failButton = (Button)findViewById(R.id.keypadFail);
		failButton.setTextColor(Color.WHITE);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentKEPADTwoActivity = 
					new Intent(KEYPADActivity.this, KEYPADTwoActivity.class);
				startActivity(intentKEPADTwoActivity);
				finish();
			}
		});
		successButton = (Button)findViewById(R.id.keypadNext);
		successButton.setTextColor(Color.BLACK);
		successButton.setEnabled(false);
		successButton.setText("Success");
		successButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentKEPADTwoActivity = 
					new Intent(KEYPADActivity.this, KEYPADTwoActivity.class);
				startActivity(intentKEPADTwoActivity);
				finish();
			}
		});
		
		for(int i = 0; i < hwbuttonCount.length; i++){
			hwbuttonCount[i] = 0;
		}
		keyBoardHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				changeImageView(flag);
				if(completeKeyTest() > 17){
					successButton.setTextColor(Color.WHITE);
					successButton.setEnabled(true);

				}
				else{
					successButton.setTextColor(Color.BLACK);
					successButton.setEnabled(false);	
				}
				
			}
			
		};

	}
	public void setButtonFlag(){
		for(int i = 0; i < buttonFlag.length; i++){
			this.buttonFlag[i] = 0;
		}
	}
	public int completeKeyTest(){
		int sum = 0;
		for(int i = 0; i < buttonFlag.length; i++ ){
			sum += buttonFlag[i];
		}
		return sum;
	}
	public void changeImageView(int flag){
		switch(flag){
		case 1:
			if(hwbuttonCount[2] % 2 == 0){
				imageView[2].setImageResource(R.drawable.keypad1_03_n);
				this.buttonFlag[2] = 0;
			}
			else{
				imageView[2].setImageResource(R.drawable.keypad1_03_s);
				this.buttonFlag[2] = 1;
			}
			break;
		case 2:
			if(hwbuttonCount[3] % 2 == 0){
				imageView[3].setImageResource(R.drawable.keypad1_04_n);
				this.buttonFlag[3] = 0;
			}
			else{
				imageView[3].setImageResource(R.drawable.keypad1_04_s);
				this.buttonFlag[3] = 1;
			}
			break;
		case 3:
			if(hwbuttonCount[4] % 2 == 0){
				imageView[4].setImageResource(R.drawable.keypad1_05_n);
				this.buttonFlag[4] = 0;
			}
			else{
				imageView[4].setImageResource(R.drawable.keypad1_05_s);
				this.buttonFlag[4] = 1;
			}
			break;
		case 4:
			if(hwbuttonCount[5] % 2 == 0){
				imageView[5].setImageResource(R.drawable.keypad1_06_n);
				this.buttonFlag[5] = 0;
			}
			else{
				imageView[5].setImageResource(R.drawable.keypad1_06_s);
				this.buttonFlag[5] = 1;
			}
			break;		
		case 5:
			if(hwbuttonCount[6] % 2 == 0){
				imageView[6].setImageResource(R.drawable.keypad1_07_n);
				this.buttonFlag[6] = 0;
			}
			else{
				imageView[6].setImageResource(R.drawable.keypad1_07_s);
				this.buttonFlag[6] = 1;
			}
			break;
		case 6:
			if(hwbuttonCount[7] % 2 == 0){
				imageView[7].setImageResource(R.drawable.keypad1_08_n);
				this.buttonFlag[7] = 0;
			}
			else{
				imageView[7].setImageResource(R.drawable.keypad1_08_s);
				this.buttonFlag[7] = 1;
			}
			break;
		case 7:
			if(hwbuttonCount[8] % 2 == 0){
				imageView[8].setImageResource(R.drawable.keypad1_09_n);
				this.buttonFlag[8] = 0;
			}
			else{
				imageView[8].setImageResource(R.drawable.keypad1_09_s);
				this.buttonFlag[8] = 1;
			}
			break;
		case 8:
			if(hwbuttonCount[9] % 2 == 0){
				imageView[9].setImageResource(R.drawable.keypad1_10_n);
				this.buttonFlag[9] = 0;
			}
			else{
				imageView[9].setImageResource(R.drawable.keypad1_10_s);
				this.buttonFlag[9] = 1;
			}
			break;
		case 9:
			if(hwbuttonCount[10] % 2 == 0){
				imageView[10].setImageResource(R.drawable.keypad1_11_n);
				this.buttonFlag[10] = 0;
			}
			else{
				imageView[10].setImageResource(R.drawable.keypad1_11_s);
				this.buttonFlag[10] = 1;
			}
			break;
		case 10:
			if(hwbuttonCount[11] % 2 == 0){
				imageView[11].setImageResource(R.drawable.keypad1_12_n);
				this.buttonFlag[11] = 0;
			}
			else{
				imageView[11].setImageResource(R.drawable.keypad1_12_s);
				this.buttonFlag[11] = 1;
			}
			break;
		case 11:
			if(hwbuttonCount[12] % 2 == 0){
				imageView[12].setImageResource(R.drawable.keypad1_13_n);
				this.buttonFlag[12] = 0;
			}
			else{
				imageView[12].setImageResource(R.drawable.keypad1_13_s);
				this.buttonFlag[12] = 1;
			}
			break;
		case 12:
			if(hwbuttonCount[13] % 2 == 0){
				imageView[13].setImageResource(R.drawable.keypad1_14_n);
				this.buttonFlag[13] = 0;
			}
			else{
				imageView[13].setImageResource(R.drawable.keypad1_14_s);
				this.buttonFlag[13] = 1;
			}
			break;
		case 13:
			if(hwbuttonCount[14] % 2 == 0){
				imageView[14].setImageResource(R.drawable.keypad1_15_n);
				this.buttonFlag[14] = 0;
			}
			else{
				imageView[14].setImageResource(R.drawable.keypad1_15_s);
				this.buttonFlag[14] = 1;
			}break;
		case 14:
			if(hwbuttonCount[15] % 2 == 0){
				imageView[15].setImageResource(R.drawable.keypad1_16_n);
				this.buttonFlag[15] = 0;
			}
			else{
				imageView[15].setImageResource(R.drawable.keypad1_16_s);
				this.buttonFlag[15] = 1;
			}
			break;
		case 15:
			if(hwbuttonCount[16] % 2 == 0){
				imageView[16].setImageResource(R.drawable.keypad1_17_n);
				this.buttonFlag[16] = 0;
			}
			else{
				imageView[16].setImageResource(R.drawable.keypad1_17_s);
				this.buttonFlag[16] = 1;
			}
			break;
		case 16:
			if(hwbuttonCount[17] % 2 == 0){
				imageView[17].setImageResource(R.drawable.keypad1_18_n);
				this.buttonFlag[17] = 0;
			}
			else{
				imageView[17].setImageResource(R.drawable.keypad1_18_s);
				this.buttonFlag[17] = 1;
			}
			break;
		case 17:
			if(hwbuttonCount[1] % 2 == 0){
				imageView[1].setImageResource(R.drawable.keypad1_02_n);
				this.buttonFlag[1] = 0;
			}
			else{
				imageView[1].setImageResource(R.drawable.keypad1_02_s);
				this.buttonFlag[1] = 1;
			}
			break;
		case 18:
			if(hwbuttonCount[0] % 2 == 0){
				imageView[0].setImageResource(R.drawable.keypad1_01_n);
				this.buttonFlag[0] = 0;
			}
			else{
				imageView[0].setImageResource(R.drawable.keypad1_01_s);
				this.buttonFlag[0] = 1;
			}
			break;
		}
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Keypad", isSuccess);
		edit.commit();
	}
	// CAN1CommManager
	private CAN1CommManager CAN1Comm = null;
	
	String TAG = "KEYPADActivity";

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG,"onResume");
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
		Log.d(TAG,"onPause");
		try {
			CAN1Comm.SetScreenTopFlag(false);

		} catch (NullPointerException e) {
			// TODO: handle exception
		}

	}

	// Communication//////////////////////////////////////
	// Communication Service Start
	private void StartCommService() {
		Intent intent = new Intent(KEYPADActivity.this, CommService.class);
		// Loacal Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()), serConn,
				Context.BIND_AUTO_CREATE);
	}

	// Communication Service Stop
	private void stopCommService() {
		unbindService(serConn);
		if (stopService(new Intent(KEYPADActivity.this, CommService.class))) {
		} else {
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
			Log.d(TAG,"onServiceConnected");
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

	
	
	///////////
	// Service Callback
	ICAN1CommManagerCallback mCallback = new ICAN1CommManagerCallback.Stub() {
		@Override
		// key를 누를때마다 정보를 받아옴.
		public void KeyButtonCallBack(int Data) throws RemoteException {
			// TODO Auto-generated method stub
			Log.d(TAG, ""+Data);
			
			if(Data == CAN1Comm.LEFT){
				flag = 1;
				hwbuttonCount[2]++;
			}
			if(Data == CAN1Comm.RIGHT){
				flag = 2;
				hwbuttonCount[3]++;
			}
			if(Data == CAN1Comm.ENTER){
				flag = 3;
				hwbuttonCount[4]++;
			}
			if(Data == CAN1Comm.CAMERA){
				flag = 4;
				hwbuttonCount[5]++;
			}
			if(Data == CAN1Comm.MAINLIGHT){
				flag = 5;
				hwbuttonCount[6]++;
			}
			if(Data == CAN1Comm.WORKLIGHT){
				flag = 6;
				hwbuttonCount[7]++;
			}
			if(Data == CAN1Comm.AUTO_GREASE){
				flag = 7;
				hwbuttonCount[8]++;
			}
			if(Data == CAN1Comm.QUICK_COUPLER){
				flag = 8;
				hwbuttonCount[9]++;
			}
			if(Data == CAN1Comm.RIDE_CONTROL){
				flag = 9;
				hwbuttonCount[10]++;
			}
			if(Data == CAN1Comm.WORK_LOAD){
				flag = 10;
				hwbuttonCount[11]++;
			}
			if(Data == CAN1Comm.BEACON_LAMP){
				flag = 11;
				hwbuttonCount[12]++;
			}
			if(Data == CAN1Comm.REAR_WIPER){
				flag = 12;
				hwbuttonCount[13]++;
			}
			if(Data == CAN1Comm.MIRROR_HEAT){
				flag = 13;
				hwbuttonCount[14]++;
			}
			if(Data == CAN1Comm.AUTOPOSITION){
				flag = 14;
				hwbuttonCount[15]++;
			}
			if(Data == CAN1Comm.FINEMODULATION){
				flag = 15;
				hwbuttonCount[16]++;
			}
			if(Data == CAN1Comm.FN){
				flag = 16;
				hwbuttonCount[17]++;
			}
			if(Data==CAN1Comm.ESC){
				flag = 17;
				hwbuttonCount[1]++;
			}
			if(Data==CAN1Comm.MENU){
				flag = 18;
				hwbuttonCount[0]++;
			}
			keyBoardHandler.sendMessage(keyBoardHandler.obtainMessage(flag));
			//keyBoardHandler.sendMessage(keyBoardHandler.obtainMessage(0));
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
