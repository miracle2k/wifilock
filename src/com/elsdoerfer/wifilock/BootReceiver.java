package com.elsdoerfer.wifilock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(WifiLockService.LOG_TAG, "onReceive " + intent);

		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
			WifiLockService.start(context, false);
	}

}
