package com.krystian.chessclock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.krystianrymonlipinski.chessclock.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClockActivity : AppCompatActivity() {

    val navController: NavController by lazy { setNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)
    }

    private fun setNavController() : NavController {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        return navHostFragment.navController
    }

}