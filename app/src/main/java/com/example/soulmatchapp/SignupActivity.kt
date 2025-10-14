package com.example.soulmatchapp


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.soulmatchapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupSpinners()
        setupClickListeners()
    }

    private fun setupSpinners() {
        // Gender Spinner
        val genderOptions = arrayOf("Select Gender", "Male", "Female", "Non-binary", "Other", "Prefer not to say")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter

        // Looking For Spinner
        val lookingForOptions = arrayOf("Looking for", "Male", "Female", "Both", "Anyone")
        val lookingForAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lookingForOptions)
        lookingForAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLookingFor.adapter = lookingForAdapter
    }

    private fun setupClickListeners() {
        // Date of Birth picker
        binding.inputDOB.setOnClickListener {
            showDatePickerDialog()
        }

        // Sign Up button
        binding.signButton2.setOnClickListener {
            registerUser()
        }

        // Login button
        binding.loginButton2.setOnClickListener {
            navigateToLogin()
        }

        // Login text
        binding.tvLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.inputDOB.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set max date to today (no future dates)
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun registerUser() {
        val email = binding.signEmail.text.toString().trim()
        val password = binding.signPassword.text.toString().trim()
        val confirmPassword = binding.confirmPasswordInput.text.toString().trim()
        val name = binding.inputName.text.toString().trim()
        val gender = binding.spinnerGender.selectedItem.toString()
        val dob = binding.inputDOB.text.toString().trim()
        val lookingFor = binding.spinnerLookingFor.selectedItem.toString()

        // Validation
        if (!validateInputs(email, password, confirmPassword, name, gender, dob, lookingFor)) {
            return
        }

        // Show loading state
        binding.signButton2.isEnabled = false
        binding.signButton2.text = "Creating Account..."

        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Update user profile with name
                    updateUserProfile(name)
                    // Save user data to Firestore
                    saveUserToFirestore(name, email, gender, dob, lookingFor)
                } else {
                    // Registration failed
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    resetButtonState()
                }
            }
    }

    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        gender: String,
        dob: String,
        lookingFor: String
    ): Boolean {
        if (name.isEmpty()) {
            binding.inputName.error = "Full name is required"
            binding.inputName.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            binding.signEmail.error = "Email is required"
            binding.signEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signEmail.error = "Please enter a valid email"
            binding.signEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.signPassword.error = "Password is required"
            binding.signPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.signPassword.error = "Password should be at least 6 characters"
            binding.signPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordInput.error = "Passwords do not match"
            binding.confirmPasswordInput.requestFocus()
            return false
        }

        if (gender == "Select Gender") {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
            binding.spinnerGender.requestFocus()
            return false
        }

        if (dob.isEmpty()) {
            binding.inputDOB.error = "Date of birth is required"
            binding.inputDOB.requestFocus()
            return false
        }

        if (lookingFor == "Looking for") {
            Toast.makeText(this, "Please select who you're looking for", Toast.LENGTH_SHORT).show()
            binding.spinnerLookingFor.requestFocus()
            return false
        }

        return true
    }

    private fun updateUserProfile(name: String) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Profile update failed but continuing: ${task.exception?.message}")
            }
        }
    }

    private fun saveUserToFirestore(name: String, email: String, gender: String, dob: String, lookingFor: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userData = hashMapOf(
                "uid" to user.uid,
                "name" to name,
                "email" to email,
                "gender" to gender,
                "dateOfBirth" to dob,
                "lookingFor" to lookingFor,
                "createdAt" to System.currentTimeMillis(),
                "profileCompleted" to true,
                "isActive" to true
            )

            db.collection("users")
                .document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to Main Activity
                    val intent = Intent(this, ProfileDetailsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                    resetButtonState()

                    // Optional: Delete the auth user if Firestore save fails
                    user.delete().addOnCompleteListener {
                        // User deleted due to Firestore failure
                    }
                }
        } ?: run {
            Toast.makeText(this, "User creation failed", Toast.LENGTH_SHORT).show()
            resetButtonState()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun resetButtonState() {
        binding.signButton2.isEnabled = true
        binding.signButton2.text = "Sign Up"
    }
}