package com.example.tracking_app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tracking_app.database.DataBaseHelper
import com.example.tracking_app.models.DatabaseEntityState
import java.io.IOException

class ManageAgentRepo(private val dataBaseHelper: DataBaseHelper) {


    private val _clearAllDataStatus = MutableLiveData<DatabaseEntityState<Boolean>>()
    val clearAllDataStatus: LiveData<DatabaseEntityState<Boolean>>
        get() = _clearAllDataStatus


    suspend fun clearAllData() {

        try {

            dataBaseHelper.clearAgent()
            _clearAllDataStatus.value = DatabaseEntityState.Success(true)

        } catch (ex: IOException) {
            _clearAllDataStatus.value = DatabaseEntityState.Error(ex)
        }
    }
}