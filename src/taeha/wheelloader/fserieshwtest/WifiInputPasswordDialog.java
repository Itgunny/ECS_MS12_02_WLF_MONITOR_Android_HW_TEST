package taeha.wheelloader.fserieshwtest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WifiInputPasswordDialog extends Dialog implements android.view.View.OnClickListener{
	TextView WifiName;
	TextView WifiStatus;
	EditText passwordEdit;
	Button	 passwordButton;
	WifiManager wifi;
	WifiInfo wifiInfo;
	ScanResult sR;
	public static String password = "";

	public WifiInputPasswordDialog(Context context, ScanResult _sR) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		
		setContentView(R.layout.wifi_password_dialog);

		passwordEdit = (EditText) findViewById(R.id.passwordDL);
		passwordEdit.setBackgroundColor(Color.WHITE);
		
		passwordButton = (Button)findViewById(R.id.passwordButton);
		passwordButton.setOnClickListener(this);
		
		sR = _sR;
		
		WifiName = (TextView)findViewById(R.id.WifiName);
		WifiName.setText(_sR.SSID.toString());
		
		WifiStatus = (TextView)findViewById(R.id.WifidlStatus);
		WifiStatus.setText(_sR.capabilities.toString());
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == passwordButton){
			password = passwordEdit.getText().toString();
			if(sR.capabilities.contains("WEP") || sR.capabilities.contains("WPA")){
				if(password == ""){
					Toast.makeText(getContext(), "please Input the Wifi password.", 1000).show();
				}else{
					dismiss();
				}
			}
			else{
				dismiss();
			}
				
		}
	}
	

}
