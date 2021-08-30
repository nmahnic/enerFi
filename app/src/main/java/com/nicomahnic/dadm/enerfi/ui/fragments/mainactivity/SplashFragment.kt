package com.nicomahnic.dadm.enerfi.ui.fragments.mainactivity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.databinding.FragmentSplashBinding

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding: FragmentSplashBinding
    private val SPLASH_TIME_OUT:Long = 2000 // 2 sec

    lateinit var v: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)

        v = view
        after(SPLASH_TIME_OUT) {
            val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            v.findNavController().navigate(action)
        }

    }

    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler(Looper.getMainLooper()).postDelayed({
                process()
            }, delay)
        }
    }

}