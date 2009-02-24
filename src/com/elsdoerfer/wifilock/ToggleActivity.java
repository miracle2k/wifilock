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