package com.example.kalku.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.UserEntity
import com.example.kalku.utils.PasswordUtils
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val userDao = AppDatabase.getDatabase(application).userDao()

    fun login(email: String, password: String) {
        when {
            email.isBlank() -> {
                _loginResult.value = LoginResult.Error("Email tidak boleh kosong!")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _loginResult.value = LoginResult.Error("Format email tidak valid!")
                return
            }
            password.isBlank() -> {
                _loginResult.value = LoginResult.Error("Password tidak boleh kosong!")
                return
            }
            password.length < 8 -> {
                _loginResult.value = LoginResult.Error("Password minimal 8 karakter!")
                return
            }
        }

        viewModelScope.launch {
            val user = userDao.getUserByEmail(email.trim())
            if (user == null || !PasswordUtils.verify(password, user.password)) {
                _loginResult.value = LoginResult.Error("Email atau password salah!")
                return@launch
            }

            // Akun lama yang masih menyimpan password teks biasa diperbarui otomatis.
            val securedUser = if (!PasswordUtils.isHashed(user.password)) {
                val updatedUser = user.copy(password = PasswordUtils.hash(password))
                userDao.updateUser(updatedUser)
                updatedUser
            } else {
                user
            }

            _loginResult.value = LoginResult.Success(
                message = "Login berhasil! Selamat datang, ${securedUser.fullName}.",
                user = securedUser
            )
        }
    }
}

sealed class LoginResult {
    data class Success(
        val message: String,
        val user: UserEntity
    ) : LoginResult()

    data class Error(val message: String) : LoginResult()
}
