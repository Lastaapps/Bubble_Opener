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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.webkit.WebView
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.*


class PolicyManager(context: Context) {

    companion object {
        private val TAG = PolicyManager::class.simpleName

        private const val SP_NAME = "PRIVACY_POLICY"
        private const val SP_DATE = "DATE"
        private val POLICY_CREATED =
            ZonedDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.MIDNIGHT, ZoneId.of("UTC"))
    }

    /**Saves now to storage representing that user agreed*/
    fun agreed() {
        sp.edit().putLong(SP_DATE, ZonedDateTime.now().toInstant().epochSecond).apply()
    }

    /**@return If user has agreed*/
    fun hasAgreed(): Boolean {
        val seconds = sp.getLong(SP_DATE, -1)
        if (seconds < 0) return false
        val date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.of("UTC"))

        return date >= POLICY_CREATED
    }

    /**SharedPreferences with agreed info*/
    private val sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
}

/**Asks user to agree the privacy policy*/
@SuppressLint("InflateParams")
class PolicyDialog : BottomSheetDialogFragment() {

    companion object {
        val TAG get() = PolicyDialog::class.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "Creating policy dialog")

        val root = LayoutInflater.from(context).inflate(R.layout.privacy_policy, null, false)

        val readPolicy = root.findViewById<TextView>(R.id.read_policy)
        val haveRead = root.findViewById<CheckBox>(R.id.have_read)!!
        val agree = root.findViewById<Button>(R.id.agree)!!

        //shows policy
        readPolicy.setOnClickListener {
            requireContext().startActivity(Intent(context, ShowPolicesActivity::class.java))
        }

        //enables/disables agree button
        haveRead.isChecked = false
        agree.isEnabled = haveRead.isChecked

        haveRead.setOnCheckedChangeListener { _, state: Boolean ->
            agree.isEnabled = state
        }

        //agrees to the policy
        agree.setOnClickListener {
            PolicyManager(requireContext()).agreed()
            dismiss()
        }

        isCancelable = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val bottomSheet =
                    requireDialog().findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
            }
        })
    }
}

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