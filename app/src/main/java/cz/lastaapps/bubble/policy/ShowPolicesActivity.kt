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

package cz.lastaapps.bubble.policy

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader


/**Shows the privacy policy*/
class ShowPolicesActivity : AppCompatActivity() {
    companion object {
        private val TAG = ShowPolicesActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Creating policy WebView")

        val webView = WebView(this)

        //loads policy text from assets
        val policy =
            BufferedReader(InputStreamReader(assets.open("privacy_policy.html"))).readText()
        webView.loadData(policy, "text/html", "UTF-8")

        setContentView(webView)
    }
}