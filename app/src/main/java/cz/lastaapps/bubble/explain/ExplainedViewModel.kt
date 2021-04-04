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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.bubble.core.App
import cz.lastaapps.bubble.firebase.FirebaseEvents
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExplainedViewModel : ViewModel() {

    val readingTimeLeft = MutableStateFlow(10)
    val buttonsEnabled = readingTimeLeft.map { it <= 0 }

    //countdown - buttons are disabled and OK button shows time left
    private var countDownJob: Job? = null
    fun startCountdown() {
        if (countDownJob == null) {
            countDownJob = viewModelScope.launch {
                while (readingTimeLeft.value > 0) {
                    delay(1000)
                    readingTimeLeft.emit(readingTimeLeft.value - 1)
                }
            }
        }
    }

    fun onOkClicked(context: Context) {
        ExplainRepo.setShown(true)
        FirebaseEvents(context).explainOK()
    }

    fun onUninstallClicked(context: Context) {
        ExplainRepo.setShown(true)
        FirebaseEvents(App.context).explainUninstall()

        //opens app info screen
        val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        context.startActivity(i)
    }
}