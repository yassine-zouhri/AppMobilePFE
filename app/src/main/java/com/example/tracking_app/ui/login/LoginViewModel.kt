package com.example.tracking_app.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tracking_app.R
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkService
import com.example.tracking_app.repository.AuthenticationRepo
import com.example.tracking_app.utils.GeoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.tracking_app.database.DatabaseBuilder.getDatabaseInstance
import com.example.tracking_app.models.Agent
import com.example.tracking_app.models.FCMuserToken
import com.example.tracking_app.network.toAgent
import com.example.tracking_app.ui.main.MainActivity
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus

class LoginViewModel(private val context: Context) : ViewModel() {

    private val _errorIdentifier = MutableLiveData<String?>()
    val errorIdentifier: LiveData<String?>
        get() = _errorIdentifier

    private val _errorPassword = MutableLiveData<String?>()
    val errorPassword: LiveData<String?>
        get() = _errorPassword

    private val _navigateEvent = MutableLiveData(false)
    val navigateEvent: LiveData<Boolean>
        get() = _navigateEvent

    private val viewModelJob = Job()
    private val sharedPref: SharedPreferences = GeoApplication.getUserPreferences()

    private val authenticationRepo =
        AuthenticationRepo(
            ApiHelperImpl(getNetworkService()),
            DataBaseHelperImpl(getDatabaseInstance())
        )

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val authenticationResponse = authenticationRepo.accessTokenResponse

    var rememberMeStatus = MutableLiveData(false)
    var loginIdentifier = MutableLiveData<String>()
    var loginPassword = MutableLiveData<String>()




    init {
        initUserCredentials()
        if (SessionManagerUtil.isSessionActive() == SessionStatus.Valid){
            navigationEventStart()}
    }

    fun saveRememberMeStatus(isChecked: Boolean) {
        if (isChecked)
            with(sharedPref.edit()) {
                putString(
                    context.getString(R.string.rememberMeIdentifier),
                    loginIdentifier.value
                )
                apply()
            }
        else
            sharedPref.edit().clear().apply()
    }

    fun login() {

        if (isFieldsValid()) {
            viewModelScope.launch {
                authenticationRepo.authenticate(
                    loginIdentifier.value,
                    loginPassword.value
                )
            }
        }
    }

    fun navigationEventStart() {
        _navigateEvent.value = true
    }

    fun navigationEventDone() {
        _navigateEvent.value = false
    }

    private fun initUserCredentials() {

        val registeredIdentifier =
            sharedPref.getString(context.getString(R.string.rememberMeIdentifier), "")

        if (!registeredIdentifier.isNullOrEmpty())
            rememberMeStatus.value = true

        loginIdentifier.value = registeredIdentifier
    }

    private fun isFieldsValid(): Boolean {

        _errorPassword.value = validatePassword(loginPassword.value)
        _errorIdentifier.value = validateUserName(loginIdentifier.value)

        return _errorPassword.value == null && _errorIdentifier.value == null
    }

    private fun validatePassword(password: String?): String? {

        if (password.isNullOrEmpty())
            return context.getString(R.string.empty_input_message)

        return null
    }

    private fun validateUserName(userName: String?): String? {

        if (userName.isNullOrEmpty())
            return context.getString(R.string.empty_input_message)

        return null
    }

    fun SendMyTokenApp(token: String){
        viewModelScope.launch {
            val IdAgent : Long = authenticationRepo.GetCurrentUser().IdAgent
            val fCMuserToken : FCMuserToken = FCMuserToken(IdAgent,token)
            authenticationRepo.SentTokenApp(fCMuserToken)
        }
    }

}