package com.example.githubprofile.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubprofile.helpers.Config
import com.example.githubprofile.models.GithubUser
import com.example.githubprofile.services.GithubUserService
import com.example.githubprofile.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    companion object {
        val TAG: String = MainViewModel::class.java.simpleName
    }

    private val _githubUser = MutableLiveData<GithubUser?>()
    val githubUser: LiveData<GithubUser?> = _githubUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        searchUser(Config.DEFAULT_USER_LOGIN)
    }

    fun searchUser(query: String) {
        _isLoading.value = true
        Log.d(TAG, "searchUser start for: $query")
        val githubUserService: GithubUserService = ServiceBuilder.buildService(GithubUserService::class.java)
        val requestCall: Call<GithubUser> = githubUserService.getUser(query)

        requestCall.enqueue(object : Callback<GithubUser> {
            override fun onResponse(call: Call<GithubUser>, response: Response<GithubUser>) {
                _isLoading.value = false
            //Jika berhasil
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d(TAG, "onResponse success: $result")
                    _githubUser.value = result
                } else {
                    Log.d(TAG, "onResponse failed with code: ${response.code()}")
                    _githubUser.value = null
                }
            }
            //Jika gagal
            override fun onFailure(call: Call<GithubUser>, t: Throwable) {
                _isLoading.value = false
                Log.d(TAG, "onFailure: ${t.message}")
                _githubUser.value = null
            }
        })
    }
}
