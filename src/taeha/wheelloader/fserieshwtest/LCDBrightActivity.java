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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class LCDBrightActivity extends Activity implements OnClickListener{
	RelativeLayout layout;
	TextView LcdTextView;
	TextView textViewBright;
	Button nextButton, stopButton, failButton;
	Button plusButton, minusButton;
	Button[] brightnessButton = new Button[8];
	int[] brightnessflag = new int[8];
	SeekBar seekbar;
	int brightness = 7;
	int isSuccess = 0;
	public Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lcdbright);
		
		layout = (RelativeLayout)findViewById(R.id.brightLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		brightnessButton[0] = (Button)findViewById(R.id.brightOne);
		brightnessButton[1] = (Button)findViewById(R.id.brightTwo);
		brightnessButton[2] = (Button)findViewById(R.id.brightThree);
		brightnessButton[3] = (Button)findViewById(R.id.brightFour);
		brightnessButton[4] = (Button)findViewById(R.id.brightFive);
		brightnessButton[5] = (Button)findViewById(R.id.brightSix);
		brightnessButton[6] = (Button)findViewById(R.id.brightSeven);
		brightnessButton[7] = (Button)findViewById(R.id.brightEight);
		
		textViewBright = (TextView)findViewById(R.id.brightStep);
	
		InitButton();
		
		
		LcdTextView = (TextView)findViewById(R.id.brightText);
		LcdTextView.setTextColor(Color.WHITE);
		
		plusButton = (Button)findViewById(R.id.brightPlus);
		minusButton = (Button)findViewById(R.id.brightMinus);
		plusButton.setOnClickListener(this);
		minusButton.setOnClickListener(this);
		
		nextButton = (Button)findViewById(R.id.brightNext);
		nextButton.setText("Success");
		nextButton.setTextColor(Color.BLACK);
		nextButton.setEnabled(false);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, 8);
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentEEPROMActivity = 
					new Intent(LCDBrightActivity.this, SerialFlashActivity.class);
				startActivity(intentEEPROMActivity);
				finish();
			}
		});
		
		failButton = (Button)findViewById(R.id.brightFail);
		failButton.setTextColor(Color.WHITE);
		failButton.setOnClickListener(new OnClickListener(	) {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, 8);
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentEEPROMActivity = 
					new Intent(LCDBrightActivity.this, SerialFlashActivity.class);
				startActivity(intentEEPROMActivity);
				finish();
			}
		});
		stopButton = (Button)findViewById(R.id.brightStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, 8);
				SavePref();
				Intent intentEEPROMActivity = 
						new Intent(LCDBrightActivity.this, FinalActivity.class);
				startActivity(intentEEPROMActivity);
				finish();
			}
		});
		seekbar = (SeekBar)findViewById(R.id.brightSeekbar);
		seekbar.setMax(brightness);
		seekbar.setProgress(brightness);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				Log.d("TAG", ""+progress);
				brightness = progress;
				CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, brightness + 1);
				textViewBright.setText(Integer.toString(brightness+1));
			}
		});
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what == 0){
					if(completeBrightTest() == 16){
						nextButton.setTextColor(Color.WHITE);
						nextButton.setEnabled(true);
					}else{
						nextButton.setTextColor(Color.BLACK);
						nextButton.setEnabled(false);
					}
				}
			}
		};
	}
	
	public void InitButton(){
		for(int i = 0; i < brightnessButton.length; i++){
			brightness = i;
			brightnessButton[i].setTextColor(Color.WHITE);
			brightnessButton[i].setOnClickListener(this);
		}
	}
	public int completeBrightTest(){
		int sum = 0;
		for(int i = 0; i < brightnessflag.length; i++){
			sum += brightnessflag[i];
		}
		return sum;
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Bright", isSuccess);
		edit.commit();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.brightPlus){
			brightness++;
			if(brightness > 7){
				brightness = 7;
			}
			CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, brightness);
			seekbar.setProgress(brightness);
			textViewBright.setText(Integer.toString(brightness+1));
		}
		else if(v.getId() == R.id.brightMinus){
			brightness--;
			if(brightness < 0){
				brightness = 0;
			}
			CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, brightness);
			textViewBright.setText(Integer.toString(brightness+1));
		}else if(v.getId() == R.id.brightOne){
			SetBright(0);
		}else if(v.getId() == R.id.brightTwo){
			SetBright(1);
		}else if(v.getId() == R.id.brightThree){
			SetBright(2);
		}else if(v.getId() == R.id.brightFour){
			SetBright(3);
		}else if(v.getId() == R.id.brightFive){
			SetBright(4);
		}else if(v.getId() == R.id.brightSix){			
			SetBright(5);
		}else if(v.getId() == R.id.brightSeven){
			SetBright(6);
		}else if(v.getId() == R.id.brightEight){
			SetBright(7);
		}

	}
	public void SetBright(int step){
		textViewBright.setText(Integer.toString(step+1));
		seekbar.setProgress(step);			
		CAN1Comm.TxCMDToMCU(CAN1CommManager.CMD_LCD, step+1);
		
		if(brightnessflag[step] == 0){
			brightnessflag[step] = 1;
			brightnessButton[step].setBackgroundResource(R.drawable.menu_numpad_small_selected);
		}else if(brightnessflag[step] == 1){
			brightnessflag[step] = 2;
			brightnessButton[step].setBackgroundResource(R.drawable.menu_numpad_small_blue);
		}
		
		
		handler.sendEmptyMessage(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
		Intent intent = new Intent(LCDBrightActivity.this,CommService.class);
		// Local Service
		startService(intent);
		// Remote Service
		bindService(new Intent(CommService.class.getName()),serConn,Context.BIND_AUTO_CREATE);
	}
	
	// Communication Service Stop
	private void stopCommService(){
		unbindService(serConn);
		if(stopService(new Intent(LCDBrightActivity.this,CommService.class))){
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
