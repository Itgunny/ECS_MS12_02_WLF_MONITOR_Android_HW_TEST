package taeha.wheelloader.fserieshwtest;

import java.io.File;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class USBMemoryActivity extends Activity {
	RelativeLayout layout;
	Button successButton, stopButton;
	TextView USBText ,tisConnected;
	ImageView usbImage;
	IntentFilter intentFilter;
	BroadcastReceiver usbReceiver;
	public static boolean isConnected = false;
	int isSuccess = 0;
	String fileContent;
	Drawable draw;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_usbmemory);
		
		layout = (RelativeLayout)findViewById(R.id.usbLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		USBText = (TextView)findViewById(R.id.usbText);
		USBText.setTextColor(Color.WHITE);
		
		tisConnected = (TextView)findViewById(R.id.isConnectedText);
		tisConnected.setTextColor(Color.WHITE);
		
		usbImage = (ImageView)findViewById(R.id.usbImage);
		
		successButton = (Button)findViewById(R.id.usbNext);
		stopButton = (Button)findViewById(R.id.usbStop);
		
		intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");
		
		isConnectedUsb("/mnt/usb");	

		successButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isSuccess == 1){
						SavePref();
						Log.i("onClick", "CallSubActivity");
						Intent intentTouchActivity = 
							new Intent(USBMemoryActivity.this, TouchActivity.class);
						startActivity(intentTouchActivity);
						finish();	
					}
					else{
						SavePref();
						Log.i("onClick", "CallSubActivity");
						Intent intentTouchActivity = 
							new Intent(USBMemoryActivity.this, TouchActivity.class);
						startActivity(intentTouchActivity);
						finish();	
					}			
				}
		});
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentTouchActivity = 
						new Intent(USBMemoryActivity.this, FinalActivity.class);
				startActivity(intentTouchActivity);
				finish();					
			}
		});
		usbReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
					isSuccess = 1;
					isConnected = true;
					tisConnected.setBackgroundResource(R.drawable.usb_connected);
					successButton.setText("Next");
				}
				if(action.equals(Intent.ACTION_MEDIA_EJECT)){
					isSuccess = 2;
					isConnected = false;
					tisConnected.setBackgroundResource(R.drawable.usb_disconnected);
					successButton.setText("Fail");

				}
			}
			
		};
		registerReceiver(usbReceiver, intentFilter);
	}
	//save
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("USB", isSuccess);
		edit.commit();
	}

	public boolean isConnectedUsb(String filePath){
		File file = new File(filePath);
		if(file != null){
			if(file.length() > 0){
				isConnected = true;
				isSuccess = 1;
				tisConnected.setBackgroundResource(R.drawable.usb_connected);	
			}else{
				isSuccess = 2;
				isConnected = false;
				tisConnected.setBackgroundResource(R.drawable.usb_disconnected);
				successButton.setText("Fail");
			}
		}
		return isConnected;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(usbReceiver);
		usbReceiver = null;
	}
}
