/*
        WifiLock: keep Wi-Fi active when Phone goes to sleep. 
        Copyright (C) 2009 Michael Elsdörfer <http://elsdoerfer.name>

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
