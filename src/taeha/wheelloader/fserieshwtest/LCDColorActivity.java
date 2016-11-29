package taeha.wheelloader.fserieshwtest;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LCDColorActivity extends Activity{
	RelativeLayout layout, layout2;
	TextView colorDesText, colorTitleText, colorPageText;
	Button stopButton, nextButton, failButton;

	int colorNum = 10;
	int startFlag = 0;
	int isSuccess = 0;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lcdcolor);
		layout = (RelativeLayout)findViewById(R.id.colorLayout);
		layout.setBackgroundColor(Color.BLACK);
		layout2 = (RelativeLayout)findViewById(R.id.colorTitleLayout);
		
		colorTitleText = (TextView)findViewById(R.id.colorText);
		colorTitleText.setTextColor(Color.WHITE);
		colorDesText = (TextView)findViewById(R.id.colorDesText);
		colorDesText.setTextColor(Color.WHITE);
		
		colorPageText = (TextView)findViewById(R.id.result);
		
		stopButton = (Button)findViewById(R.id.ColorStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentLCDBrightActivity = 
						new Intent(LCDColorActivity.this, FinalActivity.class);
				startActivity(intentLCDBrightActivity);
				finish();
			}
		});
		
		failButton = (Button)findViewById(R.id.colorFail);
		failButton.setVisibility(View.INVISIBLE);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Log.i("onClick", "CallSubActivity");
				Intent intentLCDBrightActivity = 
						new Intent(LCDColorActivity.this, LCDBrightActivity.class);
				startActivity(intentLCDBrightActivity);
				finish();
			}
		});
		nextButton = (Button)findViewById(R.id.colorNext);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(startFlag == 0){
					layout2.setVisibility(View.INVISIBLE);
					colorTitleText.setVisibility(View.INVISIBLE);
					colorDesText.setVisibility(View.VISIBLE);
					stopButton.setVisibility(View.INVISIBLE);
					nextButton.setVisibility(View.INVISIBLE);
					colorPageText.setVisibility(View.INVISIBLE);
					colorDesText.setText("RED -> GREEN -> BLUE -> BLACK -> WHITE\nis displayed in color order.\nPlease touch the screen.");
					colorNum = 0;
				}
				else{
					isSuccess = 1;
					SavePref();
					Log.i("onClick", "CallSubActivity");
					Intent intentLCDBrightActivity = 
							new Intent(LCDColorActivity.this, LCDBrightActivity.class);
					startActivity(intentLCDBrightActivity);
					finish();
				}
				
			}
		});
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();	
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(colorNum == 0){
				colorDesText.setVisibility(View.INVISIBLE);
				layout.setBackgroundColor(Color.RED);
				colorNum++;
			}
			else if(colorNum == 1){
				layout.setBackgroundColor(Color.GREEN);
				colorNum++;
			}
			else if(colorNum == 2){
				layout.setBackgroundColor(Color.BLUE);
				colorNum++;
			}
			else if(colorNum == 3){
				layout.setBackgroundColor(Color.BLACK);
				colorNum++;
			}
			else if(colorNum == 4){
				layout.setBackgroundColor(Color.WHITE);
				colorNum++;
			}
			else if(colorNum == 5){
				startFlag++;
				layout.setBackgroundColor(Color.BLACK);
				layout2.setVisibility(View.VISIBLE);
				colorTitleText.setVisibility(View.VISIBLE);
				colorDesText.setVisibility(View.VISIBLE);
				colorDesText.setText("Please Click the bottom of buttons\ndepending on the result of the test");
				colorPageText.setVisibility(View.VISIBLE);
				stopButton.setVisibility(View.VISIBLE);
				nextButton.setVisibility(View.VISIBLE);
				nextButton.setX(500);
				nextButton.setText("Success");
				failButton.setVisibility(View.VISIBLE);
				
			}
			break;
		}
		return true;
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Color", isSuccess);
		edit.commit();
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
}
