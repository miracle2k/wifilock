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

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class ToggleActivity extends Activity {

	static public boolean weArePreCupcake() {
		return (VERSION.SDK.equals("1") | VERSION.SDK.equals("2"));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.finish();

		// Pre-Cupcake, we are holding a WifiLock. After Cupcake,
		// we are just toggling the built-in setting.
		if (weArePreCupcake()) {
			enableBootReceiverAndService();
			WifiLockService.toggle(this, true);
		}
		else
		{
			this.toggle();
		}
	}

	/**
	 * Beginning with Cupcake, we don't need those any longer for
	 * functioning, so they are disabled by default. The first time
	 * WifiLock is started on a pre-Cupcake device, they are enabled
	 * if needed.
	 */
	public void enableBootReceiverAndService() {
		Log.v(WifiLockService.LOG_TAG, "Running on a pre-Cupcake device, "+
				"ensuring that service and bootreceiver components are active.");
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(
                new ComponentName(this, BootReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
		pm.setComponentEnabledSetting(
                new ComponentName(this, WifiLockService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
	}

	// TODO: Considering the phasing out of the service use, stuff like
	// LOG_TAG and makeToast() shouldn't be defined inside the
	// WifiLockService class.
	private void toggle() {
		ContentResolver cr = getContentResolver();

		int current = Settings.System.WIFI_SLEEP_POLICY_DEFAULT;
		try {
			current = Settings.System.getInt(cr, Settings.System.WIFI_SLEEP_POLICY);
		} catch (SettingNotFoundException e) {
			// pass over it, assume default
			Log.w(WifiLockService.LOG_TAG, "Setting could not be read, "+
					"assuming WIFI_SLEEP_POLICY_DEFAULT");
		}

		if (current == Settings.System.WIFI_SLEEP_POLICY_DEFAULT) {
			Log.i(WifiLockService.LOG_TAG, "Changing Wifi sleep policy to NEVER");
			Settings.System.putInt(getContentResolver(),
					Settings.System.WIFI_SLEEP_POLICY,
					Settings.System.WIFI_SLEEP_POLICY_NEVER);
			WifiLockService.showToast(this, R.string.setting_to_never);
		}
		else {
			Log.i(WifiLockService.LOG_TAG, "Changing Wifi sleep policy to DEFAULT");
			Settings.System.putInt(getContentResolver(),
					Settings.System.WIFI_SLEEP_POLICY,
					Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
			WifiLockService.showToast(this, R.string.setting_to_default);
		}
	}
}