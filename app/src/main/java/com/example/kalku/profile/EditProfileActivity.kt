package com.example.kalku.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.R
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.databinding.ActivityEditProfileBinding
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var selectedPhotoUri = ""

    private val photoPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@registerForActivityResult
        runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        selectedPhotoUri = uri.toString()
        binding.ivPhoto.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnChoosePhoto.setOnClickListener { photoPicker.launch(arrayOf("image/*")) }
        binding.btnRemovePhoto.setOnClickListener {
            selectedPhotoUri = ""
            binding.ivPhoto.setImageResource(R.drawable.ic_person)
        }
        binding.btnSave.setOnClickListener { saveProfile() }
        loadProfile()
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val session = SessionManager(this@EditProfileActivity)
            val user = AppDatabase.getDatabase(this@EditProfileActivity).userDao().getUserById(session.getUserId())
            if (user == null) {
                Toast.makeText(this@EditProfileActivity, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            binding.etFullName.setText(user.fullName)
            binding.etBusinessName.setText(user.businessName)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone)
            binding.etAddress.setText(user.address)
            binding.etBusinessDescription.setText(user.businessDescription)
            selectedPhotoUri = user.photoUri
            if (selectedPhotoUri.isNotBlank()) {
                runCatching { binding.ivPhoto.setImageURI(Uri.parse(selectedPhotoUri)) }
                    .onFailure { binding.ivPhoto.setImageResource(R.drawable.ic_person) }
            }
        }
    }

    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val businessName = binding.etBusinessName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim().lowercase()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val description = binding.etBusinessDescription.text.toString().trim()
        val session = SessionManager(this)

        when {
            fullName.length < 3 -> binding.etFullName.error = "Nama minimal 3 karakter"
            businessName.length < 2 -> binding.etBusinessName.error = "Nama usaha harus diisi"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.etEmail.error = "Email tidak valid"
            phone.isNotBlank() && phone.length < 8 -> binding.etPhone.error = "Nomor telepon tidak valid"
            description.length > 250 -> binding.etBusinessDescription.error = "Deskripsi maksimal 250 karakter"
            else -> lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(this@EditProfileActivity).userDao()
                if (dao.getOtherUserByEmail(email, session.getUserId()) != null) {
                    binding.etEmail.error = "Email sudah digunakan"
                    return@launch
                }

                val user = dao.getUserById(session.getUserId())
                if (user == null) {
                    Toast.makeText(this@EditProfileActivity, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val updated = user.copy(
                    fullName = fullName,
                    businessName = businessName,
                    email = email,
                    phone = phone,
                    address = address,
                    businessDescription = description,
                    photoUri = selectedPhotoUri
                )
                dao.updateUser(updated)
                session.updateProfile(updated)
                Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
