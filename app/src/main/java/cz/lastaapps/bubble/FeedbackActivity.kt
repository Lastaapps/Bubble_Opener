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

import android.app.Activity
import android.app.ApplicationErrorReport
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

/**Adds send feedback option to the option menu*/
open class FeedbackActivity : SplashActivity() {

    companion object {
        private val TAG = FeedbackActivity::class.simpleName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feedback_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_feedback -> {
                Log.i(TAG, "Opening feedback activity")

                try {
                    startActivity(prepareFeedbackIntent(this))
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to open the feedback activity ${e.message}")
                    Toast.makeText(this, R.string.feedback_nothing_found, Toast.LENGTH_LONG).show()

                    e.printStackTrace()
                }
            }
            R.id.menu_policy -> {
                startActivity(Intent(this, ShowPolicesActivity::class.java))
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}

/**source: https://stackoverflow.com/questions/16644933/launch-feedbackactivity-in-my-application-like-in-android-hangouts
 *
 * creates intent, which opens send feedback activity*/
private fun prepareFeedbackIntent(activity: Activity): Intent {
    val errorReport = ApplicationErrorReport()
    errorReport.packageName = activity.packageName
    errorReport.type = ApplicationErrorReport.TYPE_NONE
    /*errorReport.installerPackageName = activity.packageManager.getInstallerPackageName(
        localApplicationErrorReport.packageName
    )*/
    return Intent(Intent.ACTION_APP_ERROR)
        .addCategory(Intent.CATEGORY_DEFAULT)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Intent.EXTRA_BUG_REPORT, errorReport)
}

