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
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.play.core.review.ReviewManagerFactory
import cz.lastaapps.common.Communication

/**The content of a bubble*/
class BubbleActivity : FeedbackActivity() {

    companion object {
        private val TAG = BubbleActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Creating activity")

        setContentView(R.layout.bubble_layout)

        //various buttons actions

        findViewById<Button>(R.id.rate).setOnClickListener {
            Log.i(TAG, "Button clicked Rate")

            rate()

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

    private fun rate() {
        val manager = ReviewManagerFactory.create(this)

        //redirects to the play store, required under LOLLIPOP 5.0 and when Google play
        //API fails
        val oldRequest = {
            val url = "https://play.google.com/store/apps/details?id=cz.lastaapps.bubble"
            val uri = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        //version check
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            oldRequest()
            return
        }*/

        //Google play in app review
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { result ->
            if (result.isSuccessful) {

                val reviewInfo = result.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    Toast.makeText(
                        this,
                        R.string.thanks_review,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                oldRequest()
            }
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

