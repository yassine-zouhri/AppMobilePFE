package com.example.tracking_app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tracking_app.database.DataBaseHelper
import com.example.tracking_app.database.models.AgentEntity
import com.example.tracking_app.exception.InvalidAccessTokenException
import com.example.tracking_app.exception.NoAccessToAPIException
import com.example.tracking_app.models.AccessToken
import com.example.tracking_app.models.Agent
import com.example.tracking_app.models.FCMuserToken
import com.example.tracking_app.models.NetworkResponseState
import com.example.tracking_app.network.ApiHelper
import com.example.tracking_app.network.models.AgentInfoResponse
import com.example.tracking_app.network.models.AgentResponse
import com.example.tracking_app.network.toAgent
import com.example.tracking_app.network.toAgentEntity
import com.example.tracking_app.network.toModelAccessToken
import com.example.tracking_app.utils.JWTUtils
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class AuthenticationRepo(
    private val apiHelper: ApiHelper,
    private val dataBaseHelper: DataBaseHelper
) {



    private val _accessTokenResponse = MutableLiveData<NetworkResponseState<AccessToken>>()
    val accessTokenResponse: LiveData<NetworkResponseState<AccessToken>>
        get() = _accessTokenResponse

    private var moshi = Moshi.Builder().build()
    private var adapter = moshi.adapter(AgentResponse::class.java)

    suspend fun authenticate(userName: String?, password: String?) {

        try {

            UserLogin(userName,password)

        } catch (ex: InvalidAccessTokenException) {
            _accessTokenResponse.value = NetworkResponseState.Error(ex)
        } catch (ex: NoAccessToAPIException) {
            _accessTokenResponse.value = NetworkResponseState.Error(ex)
        }

    }

    suspend fun UserLogin(userName: String?, password: String?){
        try{
            if (!userName.isNullOrEmpty() && !password.isNullOrEmpty()) {

                _accessTokenResponse.value = NetworkResponseState.Loading(null)

                val accessToken = apiHelper.auth(userName, password)

                val agentResponse =
                    adapter.fromJson(JWTUtils.getPayloadDecoded(accessToken.accessToken))
                        ?: throw Exception()

                dataBaseHelper.insertAgent(agentResponse.user.toAgentEntity())


                _accessTokenResponse.value =
                    NetworkResponseState.Success(accessToken.toModelAccessToken())

                var databaseRef= FirebaseDatabase.getInstance("https://geo-app1-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("agentStatut")
                var currentAgentId : Long = dataBaseHelper.getAgent().AgentId
                var agentstatut =  hashMapOf<String, Any?>()
                agentstatut["id"] = currentAgentId
                agentstatut["lastConnected"] =  Date()
                agentstatut["isNotified"] =  false
                databaseRef.child(currentAgentId.toString()).setValue(agentstatut)
            }
        }catch (ex : HttpException){
            throw InvalidAccessTokenException()
        }catch (ex : IOException){
            throw NoAccessToAPIException()
        }
    }

    suspend fun GetCurrentUser(): Agent = dataBaseHelper.getAgent()?.toAgent()

    suspend fun SentTokenApp(fCMuserToken : FCMuserToken) = apiHelper.SendTokenApp(fCMuserToken)

    suspend fun DeleteTokenFCM( AgentID : Long ) = apiHelper.deleteFCMToken(AgentID)
}