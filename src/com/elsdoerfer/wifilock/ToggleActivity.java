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
import android.os.Bundle;

public class ToggleActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.finish();
		WifiLockService.toggle(this, true);
	}
}