package taeha.wheelloader.fserieshwtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FinalActivity extends Activity {
	
	String fileContent;
	RelativeLayout layout;
	TextView textView;
	TextView textViewCompleteMsg;
	TextView[] resultEachTextView = new TextView[13]; 
	Button keyigOFFButton;
	int isSuccess = 0;
	boolean textViewFlag = true;
	public static final String STRSAVEPATH = "/mnt/usb/HWFMonitorTest/";
	public static String STRFILEPATH = MainActivity.fileName + ".txt";
	public static String STRFILEPATH2 = MainActivity.fileName + ".png";
	public boolean isConnected;
	File dir;
	File file;
	BroadcastReceiver usbReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_final);
		
		layout = (RelativeLayout)findViewById(R.id.finalLayout);
		layout.setBackgroundColor(Color.BLACK);
		
		textView = (TextView)findViewById(R.id.finalText);
		textView.setTextColor(Color.WHITE);
		
		textViewCompleteMsg = (TextView)findViewById(R.id.textview_finalmessage);
		
		InitTextViewSet();		
		
		resultEachTextView[0].setText(MainActivity.fileName);
		
		
		isSuccessAndFailFileName();
		isTimeFileName();
		
		STRFILEPATH = MainActivity.fileName + ".txt";
		STRFILEPATH2 = MainActivity.fileName + ".png";
		

		loadTextViewSet();
		
		Log.d("TAG", "FileName : " + STRFILEPATH2);
		dir = makeDirectory(STRSAVEPATH);
		file = makeFile(dir, (STRSAVEPATH + STRFILEPATH2));
		fileContent = loadFileSet();
		
		keyigOFFButton = (Button)findViewById(R.id.KeyIgButton);
		keyigOFFButton.setText("SAVE FILE");
		keyigOFFButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 1;
				if(isConnectedUsb("/mnt/usb")){
					try{
						screenshot(getWindow().getDecorView());
						Runtime runtime = Runtime.getRuntime();
						Process process;
						try{
							String cmd = "sync";
							process = runtime.exec(cmd);
						}catch(Exception e){
							e.fillInStackTrace();
						}
						invisibleText();
						
						textViewCompleteMsg.setVisibility(View.VISIBLE);
						
//						//resultEachTextView[0].setX(175);
//						resultEachTextView[0].setY(125);
//						resultEachTextView[0].setText();
//						resultEachTextView[0].setTextColor(Color.WHITE);
//						resultEachTextView[0].setTextSize(40.0f);
//						resultEachTextView[0].setGravity(Gravity.CENTER);
						keyigOFFButton.setTextColor(Color.WHITE);
						keyigOFFButton.setText("COMPLETE");
						keyigOFFButton.setEnabled(false);
					}catch(Exception e){
						e.fillInStackTrace();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Please Connect to USB", 1000).show();
				}
			}
		});
		usbReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
					isConnected = true;
				}
				if(action.equals(Intent.ACTION_MEDIA_EJECT)){
					isConnected = false;
				}
			}
			
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");
		registerReceiver(usbReceiver, intentFilter);
	}
	public void screenshot(View view) throws Exception{
		view.setDrawingCacheEnabled(true);
		Bitmap screenshot = view.getDrawingCache();
		String fileName = STRFILEPATH2;
		try{
			File f = new File(dir, fileName);
			f.createNewFile();
			
			OutputStream outStream = new FileOutputStream(f);

			screenshot.compress(Bitmap.CompressFormat.PNG, 100, outStream);

			outStream.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		view.setDrawingCacheEnabled(false);
	}
	private void InitTextViewSet(){
		 resultEachTextView[0] = (TextView)findViewById(R.id.result);
		 resultEachTextView[1] = (TextView)findViewById(R.id.result1);
		 resultEachTextView[2] = (TextView)findViewById(R.id.result2);
		 resultEachTextView[3] = (TextView)findViewById(R.id.result3);
		 resultEachTextView[4] = (TextView)findViewById(R.id.result4);
		 resultEachTextView[5] = (TextView)findViewById(R.id.result5);
		 resultEachTextView[6] = (TextView)findViewById(R.id.result6);
		 resultEachTextView[7] = (TextView)findViewById(R.id.result7);
		 resultEachTextView[8] = (TextView)findViewById(R.id.result8);
		 resultEachTextView[9] = (TextView)findViewById(R.id.result9);
		 resultEachTextView[10] = (TextView)findViewById(R.id.result10);
		 resultEachTextView[11] = (TextView)findViewById(R.id.result11);
		 resultEachTextView[12] = (TextView)findViewById(R.id.result12);
		 
		 for(int i = 0; i < resultEachTextView.length; i++){
			 resultEachTextView[i].setTextColor(Color.GREEN);
		 }
	}
	public void loadTextViewSet(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		
		
		resultEachTextView[0].append(" " + new String(SharePref.getString("Timeset","")));
		resultEachTextView[1].setText(new String(isSuccessFail("USB")));
		resultEachTextView[2].setText(new String(isSuccessFail("Touch")));
		resultEachTextView[3].setText(new String(isSuccessFail("Color")));
		resultEachTextView[4].setText(new String(isSuccessFail("Bright")));
		resultEachTextView[5].setText(new String(isSuccessFail("Serial")));
		resultEachTextView[6].setText(new String(isSuccessFail("Can")));
		resultEachTextView[7].setText(new String(isSuccessFail("Buzzer")));
		resultEachTextView[8].setText(new String(isSuccessFail("Keypad")));
		resultEachTextView[9].setText(new String(isSuccessFail("KeypadLong")));
		resultEachTextView[10].setText(new String(isSuccessFail("Cam")));
		resultEachTextView[11].setText(new String(isSuccessFail("Multi")));
		resultEachTextView[12].setText(new String(isSuccessFail("Wifi")));
		for(int i = 0; i < resultEachTextView.length; i++){
			if (resultEachTextView[i].getText().toString().contains("Fail")
					|| resultEachTextView[i].getText().toString().contains("Stop")){
				resultEachTextView[i].setTextColor(Color.RED);
			}
		}
	}
	public boolean isConnectedUsb(String filePath){
		File file = new File(filePath);
		if(file != null){
			if(file.length() > 0){
				isConnected = true;				
			}else{
				isConnected = false;
			}
		}
		return isConnected;
	}
	public void invisibleText(){
		for(int i = 0; i < resultEachTextView.length; i++){
			resultEachTextView[i].setVisibility(View.INVISIBLE);
		}
	}
	private File makeDirectory(String dir_path){
		File dir = new File(dir_path);
		Log.i("TAG",""+dir_path);
		if (!dir.exists())
		{
			dir.mkdirs();
			Log.i( "TAG" , "!dir.exists" );
		}else{
			Log.i( "TAG" , "dir.exists" );
		}
		return dir;
	}
	private boolean writeFile(File file , byte[] file_content){
		boolean result;
		FileOutputStream fos;
		if(file!=null&&file.exists()&&file_content!=null){
			try {
				fos = new FileOutputStream(file);
				try {
					fos.write(file_content);
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			result = true;
		}else{
			result = false;
		}
		return result;
	}

	private File makeFile(File dir , String file_path){
		File file = null;
		boolean isSuccess = false;
		Log.i( "TAG" , "!file.exists" );
		if(dir.isDirectory()){
			file = new File(file_path);
			if(file!=null&&!file.exists()){
				Log.i( "TAG" , "!file.exists" );
				try {
					isSuccess = file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					Log.i("TAG", "颇老积己 咯何 = " + isSuccess);
				}
			}else{
				Log.i( "TAG" , "file.exists" );
			}
		}
		return file;
	}

	public String loadFileSet(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		String date = new String(SharePref.getString("Timeset",""));
		String usb = new String(isSuccessFail("USB"));
		String touch = new String(isSuccessFail("Touch")); 
		String color = new String(isSuccessFail("Color")); 
		String bright = new String(isSuccessFail("Bright"));
		String serial = new String(isSuccessFail("Serial"));
		String can = new String(isSuccessFail("Can"));
		String buzzer = new String(isSuccessFail("Buzzer"));
		String keypad = new String(isSuccessFail("Keypad"));
		String keypadLong = new String(isSuccessFail("KeypadLong"));
		String cam = new String(isSuccessFail("Cam"));
		String multi = new String(isSuccessFail("Multi"));
		String mira = new String(isSuccessFail("Wifi"));
		String str = date + usb + touch + color + bright + serial + can + buzzer + keypad + keypadLong + cam + multi + mira + "\n\n";
		return str;
	}
	public void isSuccessAndFailFileName(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		int[] a = {SharePref.getInt("USB", 0), SharePref.getInt("Touch", 0), SharePref.getInt("Color", 0), SharePref.getInt("Bright", 0),
				   SharePref.getInt("Serial", 0), SharePref.getInt("Can", 0), SharePref.getInt("Buzzer", 0),
				   SharePref.getInt("Keypad", 0), SharePref.getInt("KeypadLong", 0), SharePref.getInt("Cam", 0), SharePref.getInt("Multi", 0),
				   SharePref.getInt("Wifi", 0)};
		int countFail = 0;
		for(int i = 0; i < a.length; i++){
			if(a[i] == 0 || a[i] == 2){
				countFail++;
			}
			if(countFail > 0){
				textView.setText("FAIL");
				textView.setTextColor(Color.RED);
			}else{
				textView.setText("SUCCESS");
				textView.setTextColor(Color.GREEN);
			}
		}
		if(textView.getText() == "FAIL"){
			MainActivity.fileName = MainActivity.fileName.concat("_FAIL");
		}else if(textView.getText() == "SUCCESS"){
			MainActivity.fileName = MainActivity.fileName.concat("_SUCCESS");
		}
	}
	public void isTimeFileName(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		String timeFileName = SharePref.getString("Timeset","");
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < timeFileName.length(); i++){
			if(Character.isDigit(timeFileName.charAt(i))){
				sb.append(timeFileName.charAt(i));
			}
		}
		timeFileName = sb.toString();
		Log.d("TIMEFILENAME", "" + timeFileName);
		MainActivity.fileName = MainActivity.fileName.concat("_"+timeFileName);
		
	}
	public String isSuccessFail(String str){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		if(SharePref.getInt(str, 0) == 0){
			str = "\n" + str + " : " + "Stop"; 
		}
		else if(SharePref.getInt(str, 0) == 1){
			str = "\n" + str + " : " + "Success"; 
		}
		else{
			str = "\n" + str + " : " + "Fail";
		}
		
		return str;
	}
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(usbReceiver);
	}

}
