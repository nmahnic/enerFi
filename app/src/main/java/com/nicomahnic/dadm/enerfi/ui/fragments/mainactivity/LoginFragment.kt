package com.nicomahnic.dadm.enerfi.ui.fragments.mainactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.data.entities.UserEntity
import com.nicomahnic.dadm.enerfi.databinding.FragmentLoginBinding
import com.nicomahnic.dadm.enerfi.domain.UserDao
import com.nicomahnic.dadm.enerfi.ui.activities.SecondActivity
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(R.layout.fragment_login) {

    val authUser: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var binding: FragmentLoginBinding
    private var db: AppDatabase? = null
    private var userDao: UserDao? = null

    var btnUser: Boolean = false
    var btnPasswd: Boolean = false

    lateinit var v: View

    companion object {
        private const val RC_SIGN_IN = 342
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("NM",prefs.getString("language","es")!!)

        googleLogin()

        v = view

        binding.edtUser.apply { addTextChangedListener(userWatcher) }

        binding.edtPasswd.apply { addTextChangedListener(passwdWatcher) }

        binding.btnEnter.isEnabled = false
    }

    private fun getLanguageNameByCode(code: String) : String{
        val tempLocale = Locale(code)
        return tempLocale.getDisplayLanguage(tempLocale)
    }

    private fun isAuth() {
        if (authUser.currentUser != null) {
            val sendIntent = Intent(context, SecondActivity::class.java)
            sendIntent.putExtra("name", authUser.currentUser!!.displayName)
            sendIntent.putExtra("imgUri", authUser.currentUser!!.photoUrl.toString())
            startActivity(sendIntent)
            requireActivity().finish()
        }
    }

    private fun googleLogin() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        binding.btnLogin.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                isAuth()
            } else {
                Log.d("NM", "Error Autenticación Firebase: ${response!!.error!!.errorCode}")
            }
        }
    }

    private val userWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            println("afterTextChanged -> $s")

            btnUser = s.toString().isNotEmpty()
            binding.btnEnter.isEnabled = btnUser && btnPasswd

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val passwdWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            println("afterTextChanged -> $s")

            btnPasswd = s.toString().isNotEmpty()
            binding.btnEnter.isEnabled = btnUser && btnPasswd

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onStart() {
        super.onStart()

        db = AppDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()

        if (userDao?.loadAllPersons()?.size == 0) {
            userDao?.insertPerson(
                UserEntity(
                    id = 0,
                    name = "Mash",
                    password = "1234",
                    img = "https://yt3.ggpht.com/ytc/AAUvwnigE2XbXLQMkJNuNnJEYvdUixTSMUQWTT_qLoxh6tQ=s900-c-k-c0x00ffffff-no-rj"
                )
            )
            userDao?.insertPerson(
                UserEntity(
                    id = 0,
                    name = "Luli",
                    password = "1234",
                    img = "https://media-exp1.licdn.com/dms/image/C4D35AQHLjJeP2QR5FQ/profile-framedphoto-shrink_200_200/0/1613432312275?e=1621058400&v=beta&t=o0dW6MV15_aRm1X7Uh42a_UzmWrjkpkUAzUs0RszqLo"
                )
            )
        }
        val usersList = userDao?.loadAllPersons()
        Log.d("NM", "userList = ${usersList}")


        binding.btnEnter.setOnClickListener {
            val validUser = usersList!!.find { it!!.name == binding.edtUser.text.toString() }

            validateUser(validUser)?.let {
                val sendIntent = Intent(context, SecondActivity::class.java)
                sendIntent.putExtra("name", validUser!!.name)
                sendIntent.putExtra("imgUri", validUser!!.img)
                startActivity(sendIntent)
                requireActivity().finish()
            }
        }
    }

    private fun validateUser(validUser: UserEntity?): Boolean? {
        validUser?.let { user ->
            if (user.password == binding.edtPasswd.text.toString()) user.checked = true
            if (user.checked) {
                return true
            } else {
                Snackbar.make(v, "Password no valida", Snackbar.LENGTH_SHORT).show()
            }
        } ?: run {
            Snackbar.make(v, "Usuario no registrado", Snackbar.LENGTH_SHORT).show()
        }
        return null
    }
}