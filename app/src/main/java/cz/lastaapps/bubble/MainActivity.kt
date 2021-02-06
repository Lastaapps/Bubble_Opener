/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of TheBubble Opener.
 *
 *     TheBubble Opener is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     TheBubble Opener is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with TheBubble Opener.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package cz.lastaapps.bubble

import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.helper.widget.Flow
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity


/**Shows the main content of the app*/
class MainActivity : FeedbackActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName

        private const val SHORTCUT_ID = "home_screen_shortcut"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Creating activity")

        setContentView(R.layout.activity_main)

        if (!PolicyManager(this).hasAgreed()) {
            PolicyDialog(this).show()
        }

        //shows a bubble
        findViewById<Button>(R.id.button).setOnClickListener {
            Log.i(TAG, "Button clicked")

            Notifier.notify(this)

            FirebaseEvents(this).notificationFromMain()
        }

        //when the bubbles are disabled, it seems that they can be always reopened from a notification
        findViewById<Flow>(R.id.warning_flow).setOnClickListener {
            Log.i(TAG, "Warning clicked")

            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(intent)

            Toast.makeText(this, R.string.warning_redirect, Toast.LENGTH_LONG).show()
        }

        //places a shortcut on the home screen
        findViewById<TextView>(R.id.shortcut).apply {

            setOnClickListener {
                Log.i(TAG, "Creating shortcut")
                createShortcut(this@MainActivity)
            }

            //hides home shortcut option if disabled
            visibility =
                if (ShortcutManagerCompat.isRequestPinShortcutSupported(this@MainActivity)) View.VISIBLE else View.GONE
        }

        //shows license notices
        findViewById<TextView>(R.id.licenses).setOnClickListener {
            Log.i(TAG, "Opening open source licenses view")

            OssLicensesMenuActivity.setActivityTitle(getString(R.string.licenses_title))
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        //updates bubbles disabled warning visibility
        findViewById<Flow>(R.id.warning_flow).visibility = View.GONE //seems to work all the time
        //if (Notifier.canBubble(this) == CanBubbleStates.CANNOT) View.VISIBLE else View.GONE
    }

    /**Puts a shortcut to the home screen*/
    private fun createShortcut(context: Context) {
        //Build the shortcut
        val info = ShortcutInfoCompat.Builder(context, SHORTCUT_ID)
            .setIcon(IconCompat.createWithResource(context, R.mipmap.persons_face))
            .setShortLabel(context.getString(R.string.pinned_shortcut_text))
            .setIntent(Intent(context, DummyShowNotificationActivity::class.java).apply {
                action = Intent.ACTION_VIEW
            })
            .build()

        FirebaseEvents(context).homeShortcutAdded()

        ShortcutManagerCompat.requestPinShortcut(this, info, null)
    }
}