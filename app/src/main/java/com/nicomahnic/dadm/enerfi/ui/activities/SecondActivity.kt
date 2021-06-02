package com.nicomahnic.dadm.enerfi.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.firebase.ui.auth.AuthUI
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.base.BaseActivity
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.second_activity.*
import java.util.*


class SecondActivity : BaseActivity() {

    private lateinit var navController: NavController
    private var language: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val userName = intent.getStringExtra("name")
        val imgUri = intent.getStringExtra("imgUri")
        userName?.let { name -> User.name = name }

        navController = findNavController(R.id.navHostFragment)
        navView.setupWithNavController(navController)
        navView.getHeaderView(0).btn_logout.setOnClickListener {
            AuthUI.getInstance().signOut(this).addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        navView.getHeaderView(0).txt_nav_header.text = User.name
        Glide.with(this) //1
            .load(imgUri)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .skipMemoryCache(true) //2
            .diskCacheStrategy(DiskCacheStrategy.NONE) //3
            .transform(CircleCrop()) //4
            .into(navView.getHeaderView(0).img_nav_header)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        prefs.getString("language","es")?.let{
            Log.d("NM",it)
            language = it
        }

        dLocale = Locale(language)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_settings -> {
                Log.d("NM","PREFERENCES")
                val settingsIntent = Intent(baseContext, SettingsActivity::class.java)
                settingsIntent.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                startActivity(settingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    object User{
        var name = ""
    }
}