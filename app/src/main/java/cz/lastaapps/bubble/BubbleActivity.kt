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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import cz.lastaapps.bubble.core.FeedbackActivity
import cz.lastaapps.bubble.firebase.FirebaseEvents
import cz.lastaapps.common.Communication
import cz.lastaapps.common.PlayStoreReview

/**The content of a bubble*/
class BubbleActivity : FeedbackActivity() {

    companion object {
        private val TAG get() = BubbleActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Creating activity")

        setContentView(R.layout.bubble_layout)

        //various buttons actions

        findViewById<Button>(R.id.rate).setOnClickListener {
            Log.i(TAG, "Button clicked Rate")

            PlayStoreReview.doInAppReview(this)

            FirebaseEvents(this).buttonRate()
        }
        findViewById<Button>(R.id.share).setOnClickListener {
            Log.i(TAG, "Button clicked Share")

            share()

            FirebaseEvents(this).buttonShare()
        }
        findViewById<ImageView>(R.id.facebook).setOnClickListener {
            Log.i(TAG, "Button clicked Facebook")

            Communication.openFacebook(this)

            FirebaseEvents(this).buttonFacebook()
        }
        findViewById<ImageView>(R.id.github).setOnClickListener {
            Log.i(TAG, "Button clicked Github repo")

            Communication.openProjectsGithub(this, "bubble_opener")

            FirebaseEvents(this).buttonLastaRepo()
        }
        findViewById<ImageView>(R.id.playStore).setOnClickListener {
            Log.i(TAG, "Button clicked Play store")

            Communication.openPlayStore(this)

            FirebaseEvents(this).buttonPlayStore()
        }
        findViewById<ImageView>(R.id.telegram).setOnClickListener {
            Log.i(TAG, "Button clicked Telegram")

            Communication.openTelegram(this)

            FirebaseEvents(this).buttonTelegram()
        }

    }

    private fun share() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_message) + " https://play.google.com/store/apps/details?id=cz.lastaapps.bubble"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

}

