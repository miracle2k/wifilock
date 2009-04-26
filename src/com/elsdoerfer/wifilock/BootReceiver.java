package com.elsdoerfer.wifilock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * The receiver will start the lock service on boot, if autostart is
 * enabled. Currently, this will be the case whenever the lock was
 * previously enabled.
 *
 * A setting is used to remember the current state of the lock (enabled /
 * disabled). An alternative route would be to use the package manager
 * to disable the boot receiver directly. While this will lead to a faster
 * boot when the lock is not enabled, there is a drawback:
 * PackageManager.setComponentEnabledSetting() is slooow. In fact, it will
 * slow down the system globally to the extend that even when run in a
 * separate thread, our toast message only displays with a delay, which
 * doesn't make a good impression on users. In other words, we're trading
 * a potentially cleaner solution and faster boot for perceived speed.
 *
 * Another advantage of the current solution might be that the setting
 * probably persists through installing an update. However, this is
 * untested, and a disabled component might persist just as well.
 */
public class BootReceiver extends BroadcastReceiver {

	static String PREFERENCES_FILE = "wifilock";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(WifiLockService.LOG_TAG, "onReceive " + intent);

		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
			if (context.getSharedPreferences(
					PREFERENCES_FILE, Context.MODE_PRIVATE).getBoolean(
							"autostart", false))
				WifiLockService.start(context, false);
	}

	static void enable(Context context, boolean enable) {
		Editor pref = context.getSharedPreferences(
			PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
		pref.putBoolean("autostart", enable);
		pref.commit();
	}

}
