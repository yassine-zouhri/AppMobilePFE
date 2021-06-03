package com.example.tracking_app.ui.login
import com.google.android.gms.tasks.OnCompleteListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.tracking_app.R
import com.example.tracking_app.databinding.ActivityLoginBinding
import com.example.tracking_app.models.FCMuserToken
import com.example.tracking_app.models.NetworkResponseState
import com.example.tracking_app.ui.main.MainActivity
import com.example.tracking_app.ui.splashScreen.SplashScreenActivity

import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.isNetworkAvailable
import com.example.tracking_app.utils.showErrorSnackbar
import com.example.tracking_app.utils.showWarningSnackbar
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private  val TAG = "LoginActivity"
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding:  ActivityLoginBinding

    private lateinit var navController: NavController
    private lateinit var listener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, LoginViewModelFactory(this))
            .get(LoginViewModel::class.java)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_login
        )

        binding.apply {
            lifecycleOwner = this@LoginActivity
            loginViewModel = viewModel
        }

        viewModel.navigateEvent.observe(this,Observer {

            if (it) {
                val navIntent = Intent(this, SplashScreenActivity::class.java)

                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val duration  : Long = 2
                navIntent.putExtra("duration", duration);
                startActivity(navIntent)
                viewModel.navigationEventDone()
            }
        })

        viewModel.authenticationResponse.observe(this,Observer  {

            when (it) {

                is NetworkResponseState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }


                is NetworkResponseState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    SessionManagerUtil.startUserSession(it.data)
                    viewModel.navigationEventStart()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        val token = task.result
                        viewModel.SendMyTokenApp(token.toString())
                    })
                }

                is NetworkResponseState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    showErrorSnackbar(
                        binding.root, this,it.exception.message!!
                    )
                }
            }
        })



        binding.btnLogin.setOnClickListener {

            if (isNetworkAvailable(this)) {
                viewModel.saveRememberMeStatus(binding.rememberMe.isChecked)
                viewModel.login()
            } else
                showWarningSnackbar(binding.root, this, getString(R.string.no_internet_message))
        }


    }
}

