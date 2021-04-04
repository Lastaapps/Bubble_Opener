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
import cz.lastaapps.bubble.core.App

object ExplainRepo {

    private const val SP_NAME = "EXPLAINED"
    private const val KEY_SHOWN = "SHOWN"

    private fun getSP() = App.context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun isShown(): Boolean = getSP().getBoolean(KEY_SHOWN, false)
    fun setShown(value: Boolean) = getSP().edit().putBoolean(KEY_SHOWN, value).apply()

    /**
     * @return if an explain dialog should be shown at the startup
     * */
    fun shouldShow() = !isShown()

}