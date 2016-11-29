package taeha.wheelloader.fserieshwtest;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import android.R.integer;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	
	//파일 이름 정규화
	public static String fileName = "";
	public static String ILLEGAL_EXP = "^[a-zA-Z0-9-\\s]*$";

//	// ++, 150401 cjg
//	//Content Provider
//	public static final String 	AUTHORITY    = "taeha.wheelloader.fseries_monitor.main";
//	
//	/** ContentProvider �젣怨� �겢�옒�뒪�뿉�꽌 諛쏆쓣 uri.getPathSegments()瑜� �벑濡앺빐 以��떎 
//	 * 	<< content://" + AUTHORITY + PATH_GET>> �떎�쓬遺��꽣 getPathSegments[0] = PATH_GET, 
//	 * [1], [2], [3]... �닚�쑝濡� �굹媛꾨떎.
//	 */
//	public static final String  PATH_GET = "/AUTH_GET";
//	public static final String  PATH_UPDATE = "/AUTH_UPDATE";
//	
//	/** CotentProvider �젒洹쇱쓣 �쐞�븳 ContentResolver 媛앹껜瑜� �깮�꽦�븷 �븣 �꽔�뼱 二쇰뒗 留ㅺ컻蹂��닔�뿉
//	 *  URI瑜� �궗�슜 �븳�떎. 
//	 */
//	public static final Uri 	CONTENT_URI  = 
//			Uri.parse("content://" + AUTHORITY + PATH_GET);
//	
//	public static final Uri 	CONTENT_URI2  = 
//			Uri.parse("content://" + AUTHORITY + PATH_UPDATE);
//	// --, 150401 cjg
//	
	public static boolean LotPass = false;
	//레이아웃 정의
	RelativeLayout layout;
	TextView textView;
	TextView mainButton;
	EditText serialEditText;
	int countone = 1;
	int SoundState = 0;
	String LotNumber;
	int lastCount  = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		//removeFileName();
		
		//Setup UI
		layout = (RelativeLayout)findViewById(R.id.mainLayout);
		layout.setBackgroundColor(Color.rgb(0, 0, 0));	
		
		textView = (TextView)findViewById(R.id.mainText);
		textView.setTextColor(Color.WHITE);
		
		mainButton = (TextView)findViewById(R.id.mainStart);
		mainButton.setTextColor(Color.WHITE);
		
		serialEditText = (EditText)findViewById(R.id.mainEditText);
		serialEditText.setBackgroundColor(Color.rgb(255, 255, 255));
		serialEditText.setTextColor(Color.BLACK);
		
		ContentResolver cr = getContentResolver();
		// ContentProviderDataA �뼱�뵆由ъ��씠�뀡 insert() 硫붿꽌�뱶�뿉 �젒洹�
//		Uri uri = cr.insert(CONTENT_URI, new ContentValues());
		
//		// ContentProviderDataA �뼱�뵆由ъ��씠�뀡 �뿉�꽌 由ы꽩諛쏆� Data媛� �뀑�똿 �븯湲�
//		List<String> authValues = uri.getPathSegments();
//		String serviceType = authValues.get(0);
//		final String authkey = authValues.get(1);

		
		//if(!authkey.equals("-")){
			/*serialEditText.setText(authkey);
			serialEditText.setFocusable(false);*/
		
		serialEditText.setText("");
		serialEditText.setFocusable(true);
		serialEditText.setFocusableInTouchMode(false);
		getLotName();
		
		

		mainButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(serialEditText.getText().length() == 0){
					//File 이름  입력 안했을 경우
					Toast.makeText(getApplicationContext(), "Please Enter a LOT number", Toast.LENGTH_SHORT).show();
				}
				else {
					fileName = serialEditText.getText().toString();
					Log.d(TAG, fileName);
					if(isValidFileName(fileName)){
						Log.i("onClick", "CallSubActivity");
						LotPass = false;
						Log.d("LOT_PASS", "LOTPASS : " + LotPass);
						//Preference 초기화
						removeAllPreferences();

						Intent intentTimeSetActivity = 
								new Intent(MainActivity.this, TimeSetActivity.class);
						startActivity(intentTimeSetActivity);
						finish();
					}
					else{
						//File 이름이 유효하지 않은 경우.
						Toast.makeText(getApplicationContext(), "File name is not Valid.", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		mainButton.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Lot Pass~~");
				if(serialEditText.getText().length() == 0){
					serialEditText.setText("LOT-PASS");
				
					fileName = serialEditText.getText().toString();
					Log.d(TAG, fileName);
					if(isValidFileName(fileName)){
						Log.i("onClick", "CallSubActivity");
						LotPass = true;
						Log.d("LOT_PASS", "LOTPASS : " + LotPass);
						//Preference 초기화
						removeAllPreferences();
						Intent intentTimeSetActivity = 
								new Intent(MainActivity.this, TimeSetActivity.class);
						startActivity(intentTimeSetActivity);
						finish();
					}
					else{
						//File 이름이 유효하지 않은 경우.
						Toast.makeText(getApplicationContext(), "File name is not Valid.", Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		});
		

		serialEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				StringTokenizer st = new StringTokenizer(s.toString());
				try{
					if((st.countTokens() == 5) && (s.toString().charAt(s.toString().length()-1) == 0x0a)
							|| (st.countTokens() == 6) && (s.toString().charAt(s.toString().length()-LotNumber.length()-1) == 0x0a))
					lastCount++;
				
					if((st.countTokens() == 5) && (lastCount == 2) && (s.toString().charAt(s.toString().length()-1) == 0x0a)
							|| (st.countTokens() == 6) && (lastCount == 2) && (s.toString().charAt(s.toString().length()-LotNumber.length()-1) == 0x0a))
					{
						
						int tokenCount = 0;
						while(st.hasMoreTokens()){
							tokenCount++;
							String str2 = st.nextToken().toString();
							Log.d(TAG, "String Token Count : " + tokenCount + "String Count : " + st.countTokens() + " String : " + s.toString());
							if(tokenCount == 3){	
								LotNumber = str2;
								serialEditText.setText(LotNumber);
								Log.d(TAG, "last Count : " + lastCount);
								lastCount = 0;
								break;
							}
						}	
					}
				}catch(NullPointerException e){
						e.printStackTrace();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	//file 이름을 패턴을 통하여 유효성 판단.
	public boolean isValidFileName(String fileName){
		if(fileName == null || fileName.trim().length() == 0)
			return false;
		return Pattern.compile(ILLEGAL_EXP).matcher(fileName).find();
	}
	private void getLotName(){
		SharedPreferences sp = getSharedPreferences("LotNumber", 0);
		SharedPreferences.Editor prefEditor = sp.edit();
		if(sp.getString("fileName", "fileName") != null){
			serialEditText.setText(sp.getString("fileName", fileName));
		}else{
			serialEditText.setText("");
		}
	}
	//시작할때 preferences 지워주기
	private void removeAllPreferences(){
		SharedPreferences pref = getSharedPreferences("HWTest", 0);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.clear();
		editor.commit();
	}
	private void removeFileName(){
		SharedPreferences pref = getSharedPreferences("LotNumber", 0);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.clear();
		editor.commit();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
}
