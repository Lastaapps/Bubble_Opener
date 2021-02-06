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

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

/**Contains the methods to log Firebase events*/
class FirebaseEvents(context: Context) {

    companion object {
        private val TAG = FirebaseEvents::class.simpleName
    }

    fun homeShortcutAdded() = post("home_shortcut", "home_shortcut_created", "Home shortcut added")
    fun notificationFromMain() = post("bubble_opened", "open_main", "Opened from the MainActivity")
    fun notificationFromShortcut() =
        post("bubble_opened", "open_shortcut", "Opened from a shortcut")

    fun buttonRate() = post("button_click", "button_rate", "Rate")
    fun buttonShare() = post("button_click", "button_share", "Share")
    fun buttonFacebook() = post("button_click", "button_facebook", "Facebook")
    fun buttonLastaRepo() = post("button_click", "button_lasta_repo", "Lasta Apps repo")
    fun buttonPlayStore() = post("button_click", "button_play_store", "Play store")
    fun buttonTelegram() = post("button_click", "button_telegram", "Telegram")

    private val firebase = FirebaseAnalytics.getInstance(context)

    private fun post(group: String, id: String, name: String) {

        Log.i(TAG, "Posting $group, $id, $name")

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        //firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        firebase.logEvent(group, bundle)
    }
}

