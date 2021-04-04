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

package cz.lastaapps.bubble.explain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cz.lastaapps.bubble.R
import cz.lastaapps.bubble.core.ExpandedBottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest

/**
 * Explains to an user, how to use the app and
 * */
class ExplainDialog : ExpandedBottomSheetDialogFragment() {

    companion object {
        val TAG get() = ExplainDialog::class.simpleName

        private const val CONFIRM_REQUIRED = "CONFIRM_REQUIRED"

        fun createRequestBundle(requested: Boolean) =
            Bundle().apply { putBoolean(CONFIRM_REQUIRED, requested) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //if a countdown to fore an user to read the message will be enabled
        val confirmRequired = arguments?.getBoolean(CONFIRM_REQUIRED) ?: false

        val root = inflater.inflate(R.layout.explain_dialog, container, false)
        val ok = root.findViewById<Button>(R.id.ok_timed)
        val uninstall = root.findViewById<Button>(R.id.uninstall)

        val viewModel by activityViewModels<ExplainedViewModel>()

        //closes dialog
        ok.setOnClickListener {
            viewModel.onOkClicked(requireContext())
            dismiss()
        }

        if (confirmRequired) {

            //opens apps info to simply uninstall the all if the system implementation
            uninstall.setOnClickListener {
                viewModel.onUninstallClicked(requireContext())
            }

            lifecycleScope.launchWhenResumed {
                viewModel.readingTimeLeft.collectLatest {
                    ok.text =
                        if (it > 0)
                            getString(R.string.ok_timed_any, it)
                        else
                            getString(R.string.ok_timed_none)
                }
            }

            lifecycleScope.launchWhenResumed {
                viewModel.buttonsEnabled.collectLatest {
                    ok.isEnabled = it
                    uninstall.isEnabled = it

                    //dialog cannot be dismissed
                    isCancelable = it
                }
            }

            viewModel.startCountdown()


        } else {
            uninstall.visibility = View.GONE
            ok.text = getString(R.string.ok_timed_none)
        }

        return root
    }
}