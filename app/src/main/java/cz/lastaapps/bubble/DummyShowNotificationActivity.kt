/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of The Bubble Opener.
 *
 *     The Bubble Opener is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     The Bubble Opener is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with The Bubble Opener.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package cz.lastaapps.bubble

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**Only an activity can be opened from a shortcut, so this activity is needed to show a bubble
 * This activity is fully transparent
 */
class DummyShowNotificationActivity : AppCompatActivity() {

    companion object {
        private val TAG = DummyShowNotificationActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Creating activity")

        //posts notification
        Notifier.notify(this)

        //notifies Firebase
        FirebaseEvents(this).notificationFromShortcut()

        finish()
    }
}