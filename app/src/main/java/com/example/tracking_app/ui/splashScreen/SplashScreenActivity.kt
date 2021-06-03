package com.example.tracking_app.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tracking_app.R
import com.example.tracking_app.ui.login.LoginActivity
import com.example.tracking_app.ui.main.MainActivity
import com.example.tracking_app.ui.main.MainActivityModel

class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler : Handler

    private lateinit var viewModel : SplashScreenActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        viewModel = SplashScreenActivityViewModel()
        handler = Handler()
        handler.postDelayed({
            if(viewModel.navigateEvent == true){
                val navIntent = Intent(this, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                finish()
            }else{
                var intent = Intent(this , MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        },3000)



    }
}