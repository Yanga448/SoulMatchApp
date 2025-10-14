package com.example.soulmatchapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soulmatchapp.databinding.ActivityProfileDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class ProfileDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDetailsBinding
    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 100

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.uploadImageButton.setOnClickListener { pickImageFromGallery() }
        binding.nextButton.setOnClickListener { saveUserProfile() }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.profileImage.setImageURI(selectedImageUri)
        }
    }

    private fun saveUserProfile() {
        val twoTruths = binding.twoTruthsAndLie.text.toString().trim()
        val idealWeekend = binding.idealWeekend.text.toString().trim()

        val interests = mutableListOf<String>()
        if (binding.musicInterest.isChecked) interests.add("Music")
        if (binding.fitnessInterest.isChecked) interests.add("Fitness")
        if (binding.travelInterest.isChecked) interests.add("Travel")

        val currentUser = auth.currentUser
        if (currentUser != null) {

            // Convert image to Base64 string if selected
            var imageBase64: String? = null
            selectedImageUri?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // compress to save space
                val bytes = baos.toByteArray()
                imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            }

            val userData = hashMapOf(
                "uid" to currentUser.uid,
                "twoTruthsAndLie" to twoTruths,
                "idealWeekend" to idealWeekend,
                "interests" to interests,
                "profileCompleted" to true,
                "profileImage" to imageBase64 // store image as Base64 string
            )

            db.collection("users")
                .document(currentUser.uid)
                .update(userData as Map<String, Any>)
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
                }

        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
