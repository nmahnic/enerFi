package com.nicomahnic.dadm.enerfi.core.base

import android.content.Intent
import android.content.res.Configuration
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import com.nicomahnic.dadm.enerfi.ui.activities.MainActivity
import java.util.*


open class BaseActivity : AppCompatActivity() {

    companion object {
        var dLocale: Locale = Locale("es")
    }

    init {
        updateConfig(this)
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        if(dLocale==Locale("") ) // Do nothing if dLocale is null
            return

        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }
}