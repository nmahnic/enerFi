package com.nicomahnic.tests.sender

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Parcelable
import java.util.*

//Filter out Intents you don"t want to show from a IntentChooser dialog.
// For example your own app, competing apps or just apps you have a share integration by SDK already :)
// Based on https://gist.github.com/mediavrog/5625602

object CustomSenderIntent {
    /**
     * Creates a chooser that only shows installed apps that are allowed by the whitelist.
     *
     * @param pm PackageManager instance.
     * @param target The intent to share.
     * @param whitelist A list of package names that are allowed to show.
     * @return Updated intent, to be passed to [android.content.Context.startActivity].
     */
    fun create(pm: PackageManager, target: Intent, whitelist: String): Intent {
        val dummy = Intent(target.action)
        dummy.type = target.type
        val resQuery = pm.queryIntentActivities(dummy, 0)

        val resInfo = resQuery.find{it -> whitelist.contains(it.activityInfo.packageName)}
        val info = HashMap<String, String>()
        val targetedShareIntent = target.clone() as Intent

        resInfo?.let{ ri ->
            info["packageName"] = ri.activityInfo!!.packageName
            info["className"] = ri.activityInfo.name
            info["simpleName"] = ri.activityInfo.loadLabel(pm).toString()

            info.also{ mi ->
                targetedShareIntent.setPackage(mi["packageName"])
                targetedShareIntent.setClassName(mi["packageName"]!!, mi["className"]!!)
            }
        }

        val chooserIntent = Intent.createChooser(targetedShareIntent, "")
        val targetedIntentsParcelable = arrayOf<Parcelable>()
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntentsParcelable)
        return chooserIntent

    }
}