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

import android.content.Context
import java.time.*

class PolicyManager(context: Context) {

    companion object {
        private val TAG get() = PolicyManager::class.simpleName

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
