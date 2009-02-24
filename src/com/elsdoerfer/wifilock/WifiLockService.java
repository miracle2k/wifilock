package com.elsdoerfer.wifilock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WifiLockService extends Service {
	
	/**
	 * Global state of this service. Is checked by the 
	 * toggle activity to determine whether to start or
	 * stop the service. 
	 */
	static public boolean serviceRunning = false;
	
	/**
	 * The WifiLock the service holds while running.
	 */
	protected WifiManager.WifiLock lock = null;
		
	static private String LOG_TAG = "WifiLock.Service";

	public WifiLockService() {}

	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	@Override
	public void onCreate() {
		Log.v(LOG_TAG, "WifiLock service about to be created");
		super.onCreate();
		
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		lock = manager.createWifiLock("com.elsdoerfer.wifilock");
		lock.acquire();
		serviceRunning = true;
		
		Toast.makeText(this, R.string.service_enabled, Toast.LENGTH_LONG).show();
		
		Log.v(LOG_TAG, "WifiLock service creation completed");
	}

	@Override
	public void onDestroy() {
		Log.v(LOG_TAG, "WifiLock service about to shutdown");
		
		serviceRunning = false;
		lock.release();
		Toast.makeText(this, R.string.service_disabled, Toast.LENGTH_LONG).show();
		
		super.onDestroy();
		Log.v(LOG_TAG, "WifiLock service shutdown completed");
	}

}
