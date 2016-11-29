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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TouchActivity extends Activity implements OnClickListener {
	RelativeLayout layout, layout2;
	TextView touchText, touchDesTextView, touchPageText;
	ImageButton[] imageButton = new ImageButton[5];
	Button stopButton, nextButton, failButton;
	static int buttoncount = 5;
	static int flag = 1;
	int isSuccess = 0;
	int isTouchSuccess = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_touch);
		
		layout = (RelativeLayout)findViewById(R.id.touchLayout);
		layout2 = (RelativeLayout)findViewById(R.id.touchTitleLayout);
		
		layout.setBackgroundColor(Color.BLACK);
		
		touchText = (TextView)findViewById(R.id.touchText);
		touchText.setTextColor(Color.WHITE);
		
		touchDesTextView = (TextView)findViewById(R.id.touchDesText);
		touchDesTextView.setTextColor(Color.WHITE);
		
		touchPageText = (TextView)findViewById(R.id.result);
		
		imageButton[0] = (ImageButton)findViewById(R.id.ImageButton01);
		imageButton[1] = (ImageButton)findViewById(R.id.ImageButton02);
		imageButton[2] = (ImageButton)findViewById(R.id.ImageButton03);
		imageButton[3] = (ImageButton)findViewById(R.id.ImageButton04);
		imageButton[4] = (ImageButton)findViewById(R.id.ImageButton05);
		
		
		
		for(int i = 0; i < imageButton.length; i++){
			imageButton[i].setBackgroundColor(Color.BLACK);
			imageButton[i].setOnClickListener(this);
			imageButton[i].setVisibility(View.INVISIBLE);
		}
		
		
		stopButton = (Button)findViewById(R.id.touchStop);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 0;
				SavePref();
				Intent intentLCDColorActivity = 
						new Intent(TouchActivity.this, FinalActivity.class);
				startActivity(intentLCDColorActivity);
				finish();
			}
		});
		failButton = (Button)findViewById(R.id.touchFail);
		failButton.setVisibility(View.INVISIBLE);
		failButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSuccess = 2;
				SavePref();
				Intent intentLCDColorActivity = 
						new Intent(TouchActivity.this, LCDColorActivity.class);
				startActivity(intentLCDColorActivity);
				finish();
				
			}
		});
		nextButton = (Button)findViewById(R.id.touchNext);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(buttoncount < 1){
					isTouchSuccess = 1;
				} else{
					isTouchSuccess = 2;
				}
				if(isTouchSuccess == 1){
					isSuccess = 1;
					SavePref();
					Log.i("onClick", "CallSubActivity");
					Intent intentLCDColorActivity = 
							new Intent(TouchActivity.this, LCDColorActivity.class);
					startActivity(intentLCDColorActivity);
					finish();
				}else if (isTouchSuccess == 2){
					nextButton.setVisibility(View.INVISIBLE);
					failButton.setVisibility(View.VISIBLE);
					layout2.setBackgroundColor(Color.BLACK);
					stopButton.setVisibility(View.INVISIBLE);
					imageButton[0].setVisibility(View.VISIBLE);
					touchPageText.setVisibility(View.INVISIBLE);
				}
			}
		});
		
	}
	public void SavePref(){
		SharedPreferences SharePref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor edit = SharePref.edit();
		//저장하는값
		edit.putInt("Touch", isSuccess);
		edit.commit();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ImageButton01){
			if(flag == 1){
				imageButton[0].setVisibility(View.INVISIBLE);
				buttoncount--;
				flag++;
				imageButton[1].setVisibility(View.VISIBLE);
			}
		}
		else if(v.getId() == R.id.ImageButton02){
			if(flag == 2){
				imageButton[1].setVisibility(View.INVISIBLE);
				touchDesTextView.setVisibility(View.INVISIBLE);
				buttoncount--;
				flag++;
				imageButton[2].setVisibility(View.VISIBLE);
			}
		}
		else if(v.getId() == R.id.ImageButton03){
			if(flag == 3){
				imageButton[2].setVisibility(View.INVISIBLE);
				touchDesTextView.setVisibility(View.VISIBLE);
				buttoncount--;
				flag++;
				imageButton[3].setVisibility(View.VISIBLE);
				
			}
		}
		else if(v.getId() == R.id.ImageButton04){
			if(flag == 4){
				
				imageButton[3].setVisibility(View.INVISIBLE);
				buttoncount--;
				flag++;
				
				imageButton[4].setVisibility(View.VISIBLE);
				
			}
		}
		else if(v.getId() == R.id.ImageButton05){
			if(flag == 5){
				imageButton[4].setVisibility(View.INVISIBLE);
				buttoncount--;
				flag++;
			}
		}
		if(buttoncount < 1){
			isTouchSuccess = 1;
			nextButton.setVisibility(View.VISIBLE);
			nextButton.setText("Next");
			failButton.setVisibility(View.INVISIBLE);
			layout2.setBackgroundResource(R.drawable.menu_title_bg);
			stopButton.setVisibility(View.VISIBLE);
			touchDesTextView.setBackgroundResource(R.drawable.success);
			touchDesTextView.setText("");
			touchPageText.setVisibility(View.VISIBLE);
		} else{
			isTouchSuccess = 2;
		}
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
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//return super.onTouchEvent(event);
		return false;
	}

	
}
