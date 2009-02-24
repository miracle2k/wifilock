package com.elsdoerfer.wifilock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ToggleActivity extends Activity {
    
	static private String LOG_TAG = "WifiLock.Toggle";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Intent svc = new Intent(this, WifiLockService.class);

		if (WifiLockService.serviceRunning) {
			Log.d(LOG_TAG, "before stopService");
			stopService(svc);
			Log.d(LOG_TAG, "after stopService");
		}
		else{
			Log.d(LOG_TAG, "before startService");
			startService(svc);
			Log.d(LOG_TAG, "after startService");
		}
		
		this.finish();
    }
}