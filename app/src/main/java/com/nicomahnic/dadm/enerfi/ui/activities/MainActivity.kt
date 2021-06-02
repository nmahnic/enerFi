package com.nicomahnic.dadm.enerfi.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.nicomahnic.dadm.enerfi.R

class MainActivity : AppCompatActivity() {

    private lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        prefs.getString("language","es")?.let{
            Log.d("NM",it)
            language = it
        }
    }

}