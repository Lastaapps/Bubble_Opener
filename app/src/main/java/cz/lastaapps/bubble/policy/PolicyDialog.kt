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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import cz.lastaapps.bubble.R
import cz.lastaapps.bubble.core.ExpandedBottomSheetDialogFragment

/**Asks user to agree the privacy policy*/
class PolicyDialog : ExpandedBottomSheetDialogFragment() {

    companion object {
        val TAG get() = PolicyDialog::class.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "Creating policy dialog")

        @SuppressLint("InflateParams")
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
}
