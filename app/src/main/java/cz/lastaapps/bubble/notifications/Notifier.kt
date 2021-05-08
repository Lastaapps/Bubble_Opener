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

package cz.lastaapps.bubble.notifications

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import cz.lastaapps.bubble.BubbleActivity
import cz.lastaapps.bubble.MainActivity
import cz.lastaapps.bubble.R
import kotlin.random.Random
import kotlin.reflect.KClass

/**Post notification, which can be transformed into a bubble*/
object Notifier {

    private val TAG get() = Notifier::class.simpleName

    //notification properties
    private const val channelId = "channel"
    private const val notificationId = 42069
    private const val shortcutId = "notification_shortcut"
    private fun getChannelName(context: Context) =
        context.getString(R.string.notification_channel_name)


    /**Posts a bubble notification*/
    fun notify(context: Context) {
        Log.i(TAG, "Posting a notification")

        initChannel(context)
        createNotification(context)
    }

    /**@return if bubbles are available*/
    fun canBubble(context: Context): CanBubbleStates {

        initChannel(context)
        createShortcut(context)

        val old = NotificationManagerCompat.from(context)
            .getNotificationChannelCompat(channelId, shortcutId)

        return when (old?.canBubble()) {
            null -> CanBubbleStates.UNINITIALIZED
            true -> CanBubbleStates.CAN
            else -> CanBubbleStates.CANNOT
        }
    }

    /**Creates notification channel*/
    private fun initChannel(context: Context) {
        val old = NotificationManagerCompat.from(context)
            .getNotificationChannelCompat(channelId, shortcutId)
        if (old != null) return

        NotificationChannelCompat.Builder(
            channelId,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName(getChannelName(context))
            .setShowBadge(false)
            .setSound(null, null)
            .setVibrationEnabled(false)
            .setLightColor(0)
            .build()
            .also {
                NotificationManagerCompat.from(context).apply {
                    createNotificationChannel(it)
                }
            }
    }

    /**creates and posts a bubble notification*/
    private fun createNotification(context: Context) {

        val icon = getIcon(context)
        val person = getPerson(context)

        createShortcut(context)

        @SuppressLint("ObsoleteSdkInt")
        //metadata for a bubble
        val bubbleDataBuilder = NotificationCompat.BubbleMetadata.Builder(
            createPending(context, createIntent(context, BubbleActivity::class)), icon
        )

        val bubbleData: NotificationCompat.BubbleMetadata =
            with(bubbleDataBuilder) {
                setAutoExpandBubble(true)
                setSuppressNotification(true)
                setDesiredHeight(1024)
                build()
            }

        //text shows in a bubbles bubble or in a notification
        val message = NotificationCompat.MessagingStyle.Message(
            context.getString(R.string.notification_message),
            System.currentTimeMillis(),
            person
        )

        //actual notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setStyle(NotificationCompat.MessagingStyle(person).addMessage(message))
            .setShortcutId(shortcutId)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(
                createPending(context, createIntent(context, MainActivity::class))
            )
            .setSmallIcon(R.drawable.icon_foreground)
            .setBubbleMetadata(bubbleData)
            .addPerson(person)
            .setShowWhen(false)
            .setWhen(System.currentTimeMillis())
            .build()

        //posts the notification
        NotificationManagerCompat.from(context)
            .notify(notificationId, notification)

    }

    /**
     * Register system person shortcut
     * */
    private fun createShortcut(context: Context) {

        val icon = getIcon(context)
        val person = getPerson(context)

        //shortcut to a specific person
        val shortcut = ShortcutInfoCompat.Builder(context, shortcutId)
            .setLongLived(true)
            .setLocusId(LocusIdCompat(shortcutId))
            .setIntent(createIntent(context, DummyShowNotificationActivity::class))
            .setShortLabel(context.getString(R.string.notification_label))
            .setIcon(icon)
            .setPerson(person)
            //.setCategories(setOf(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION)) //for the share feature
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }

    /**
     * @return Icon of the notification person
     * */
    private fun getIcon(context: Context): IconCompat {
        //the main icon, should be adaptive
        @Suppress("UnnecessaryVariable")
        @SuppressLint("ObsoleteSdkInt")
        val icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            IconCompat.createWithResource(context, R.mipmap.persons_face)
        else
            IconCompat.createWithResource(context, R.mipmap.persons_face_foreground)

        return icon
    }

    /**
     * @return person object to be registered in system and used in a notification
     * */
    private fun getPerson(context: Context) = Person.Builder()
        .setName(context.getString(R.string.notification_name))
        .setIcon(getIcon(context))
        .build()

    /**@return PendingIntent for the Intent given*/
    private fun createPending(context: Context, intent: Intent) =
        PendingIntent.getActivity(
            context,
            Random.nextInt(10000) + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    /**@return Intent for the class given*/
    private fun <T : Activity> createIntent(context: Context, kClass: KClass<T>) =
        Intent(context, kClass.java)
            .setAction(Intent.ACTION_VIEW)

}

enum class CanBubbleStates {
    UNINITIALIZED, CAN, CANNOT,
}