package com.example.githubprofile.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubprofile.R
import com.example.githubprofile.databinding.ActivityMainBinding
import com.example.githubprofile.models.GithubUser
import com.example.githubprofile.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        mainViewModel.githubUser.observe(this) { user ->
            if (user != null) {
                setUserData(user)
            } else {
                showErrorState()
            }
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.btnSearchUserLogin.setOnClickListener {
            performSearch()
        }

        binding.etSearchUserLogin.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val userLogin = binding.etSearchUserLogin.text.toString().trim()
        if (userLogin.isNotEmpty()) {
            mainViewModel.searchUser(userLogin)
        } else {
            Toast.makeText(this, "Masukkan nama user GitHub", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUserData(githubUser: GithubUser) {
        val userInfo = buildString {
            append("Name: ${githubUser.name ?: "-"}\n")
            append("Username: ${githubUser.login}\n")
            append("Bio: ${githubUser.bio ?: "-"}\n")
            append("Company: ${githubUser.company ?: "-"}\n")
            append("Location: ${githubUser.location ?: "-"}\n")
            append("Repositories: ${githubUser.publicRepos}\n")
            append("Followers: ${githubUser.followers} | Following: ${githubUser.following}")
        }

        binding.tvUser.text = userInfo
        Glide.with(applicationContext)
            .load(githubUser.avatarUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.baseline_broken_image_24)
            )
            .into(binding.imgUser)
    }

    private fun showErrorState() {
        binding.tvUser.text = "User Tidak Ditemukan"
        Glide.with(applicationContext)
            .load(R.drawable.baseline_broken_image_24)
            .into(binding.imgUser)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
