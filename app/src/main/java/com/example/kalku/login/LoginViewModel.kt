package com.example.kalku.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kalku.data.local.AppDatabase
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun login(email: String, password: String) {
        if (email.isEmpty()) {
            _loginResult.value = LoginResult.Error("Email tidak boleh kosong!")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginResult.value = LoginResult.Error("Format email tidak valid!")
            return
        }

        if (password.isEmpty()) {
            _loginResult.value = LoginResult.Error("Password tidak boleh kosong!")
            return
        }

        if (password.length < 8) {
            _loginResult.value = LoginResult.Error("Password minimal 8 karakter!")
            return
        }

        viewModelScope.launch {
            val user = userDao.getUserByEmail(email)
            if (user != null && user.password == password) {
                _loginResult.value = LoginResult.Success(
                    message = "Login berhasil! Selamat datang, ${user.fullName}.",
                    userId = user.id,
                    fullName = user.fullName,
                    email = user.email
                )
            } else {
                _loginResult.value = LoginResult.Error("Email atau password salah!")
            }
        }
    }
}

sealed class LoginResult {
    data class Success(
        val message: String,
        val userId: Int,
        val fullName: String,
        val email: String
    ) : LoginResult()

    data class Error(val message: String) : LoginResult()
}
