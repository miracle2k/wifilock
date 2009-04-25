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

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
	 * Toast object used to show the toggle messages. By
	 * making this global we can reuse an existing object
	 * (or cancel it) when the user is toggling quickly.
	 */
	static private Toast cachedToastObj = null;

	/**
	 * The WifiLock the service holds while running.
	 */
	protected WifiManager.WifiLock lock = null;

	static public String LOG_TAG = "WifiLock";

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

		Log.v(LOG_TAG, "WifiLock service creation completed");
	}

	@Override
	public void onDestroy() {
		Log.v(LOG_TAG, "WifiLock service about to shutdown");

		serviceRunning = false;
		lock.release();

		super.onDestroy();
		Log.v(LOG_TAG, "WifiLock service shutdown completed");
	}

	/**
	 * Static control functions.
	 */

	public static void start(Context context, boolean showToast) {
		Intent svc = new Intent(context, WifiLockService.class);
		Log.d(LOG_TAG, "before startService");
		context.startService(svc);
		if (showToast)
			showToast(context, R.string.service_enabled);
		enableBootReceiver(context, true);
		Log.d(LOG_TAG, "after startService");
	}

	public static void stop(Context context, boolean showToast) {
		Intent svc = new Intent(context, WifiLockService.class);
		Log.d(LOG_TAG, "before stopService");
		context.stopService(svc);
		if (showToast)
			showToast(context, R.string.service_disabled);
		enableBootReceiver(context, false);
		Log.d(LOG_TAG, "after stopService");
	}

	public static void toggle(Context context, boolean showToast) {
		if (serviceRunning)
			stop(context, showToast);
		else
			start(context, showToast);
	}

	private static void showToast(Context context, int resid) {
		if (cachedToastObj == null)
			cachedToastObj = Toast.makeText(context, resid, Toast.LENGTH_LONG);
		else {
			cachedToastObj.setText(resid);
		}
		cachedToastObj.show();
	}

	static class QueuedToggleThread extends Thread {
		AtomicInteger wanted = new AtomicInteger();
		private Context _context;
		public QueuedToggleThread(Context context) {
			_context = context;
		}
		public void run() {
			Log.d(LOG_TAG, "BootReceiver toggle thread started");
			PackageManager pm = _context.getPackageManager();
			int switchTo = wanted.getAndSet(0);
			while (switchTo != 0) {
				boolean enable = switchTo == 1;
				pm.setComponentEnabledSetting(
					new ComponentName(_context, BootReceiver.class),
					enable
						? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
						: PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
				Log.d(LOG_TAG, "BootReceiver "+
						(enable?"enabled":"disabled"));
				switchTo = wanted.getAndSet(0);
			}
			Log.d(LOG_TAG, "BootReceiver toggle thread ended");
		}
	};

	static Thread switcher = null;
	private static void enableBootReceiver(Context context, boolean enable) {

		if (switcher != null && switcher.isAlive())
			((QueuedToggleThread)switcher).wanted.set(enable ? 1 : 2);
		else {
			switcher = new QueuedToggleThread(context);
			((QueuedToggleThread)switcher).wanted.set(enable ? 1 : 2);

			switcher.start();
		}
	}

}
