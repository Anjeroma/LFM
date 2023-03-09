package com.anjeroma.LFM

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    private lateinit var myuser: TextView
    private lateinit var emailuser: TextView
    private lateinit var pasworduser: TextView
    private lateinit var mybutton: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        myuser = findViewById(R.id.username)
        emailuser = findViewById(R.id.youremail)
        pasworduser = findViewById(R.id.password)
        mybutton = findViewById(R.id.register)
        mybutton.setOnClickListener {
            val userWetu = myuser.text.toString()
            val emailWetu = emailuser.text.toString()
            val paswordWetu = pasworduser.text.toString()
            ingizoLetu(userWetu, emailWetu, paswordWetu)
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
    }

    private fun ingizoLetu(
        userWetu: String,
        emailWetu: String,
        paswordWetu: String
    ) {
        auth.createUserWithEmailAndPassword(emailWetu, paswordWetu)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileupdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(userWetu).build()
                    user?.updateProfile(profileupdate)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            val userData = ArrayList<Any>()
                            userData.add(userWetu)
                            userData.add(emailWetu)
                            userData.add(paswordWetu)
                            val db = FirebaseDatabase.getInstance().reference
                            db.child("users").child(user?.uid ?: "")
                                .setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this, "Registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { exception ->
                                    // If the user already exists in the database, show an error message and redirect to the login page
                                    if (exception.message?.contains("already exists") == true) {
                                        Toast.makeText(
                                            this, "User with the same email already exists",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this, "Registration failed: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                        }

                    }

                } else {
                    Toast.makeText(
                        this, "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
