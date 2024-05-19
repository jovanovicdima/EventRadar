package com.jovanovicdima.eventradar.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.jovanovicdima.eventradar.LoginActivity

object Firebase {

    fun createAccount(
        email: String,
        password: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        callback: () -> Unit
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Create account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("Register", "SUCCESSFUL")
                bindUsernameToEmail(email, username, fullName, phoneNumber, callback)
            }
            .addOnFailureListener { exception ->
                Log.e("Auth", exception.toString())
                throw exception
            }

    }

    private fun bindUsernameToEmail(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        callback: () -> Unit
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Bind username to email
        Firebase.firestore.collection("usernames").document(username).set(
            hashMapOf(
                "email" to email
            )
        ).addOnSuccessListener {
            Log.d("UsernameToPassword", "SUCCESSFUL")
            writeAdditionalDataOnRegistration(email, username, fullName, phoneNumber, callback)
        }.addOnFailureListener {// Revert registration process if anything fails
            // Delete account if bind fails
            auth.currentUser!!.delete()
        }
    }

    private fun writeAdditionalDataOnRegistration(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        callback: () -> Unit
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Write additional user data to the database
        Firebase.firestore.collection("users").document(auth.currentUser!!.uid).set(
            hashMapOf(
                "username" to username,
                "email" to email,
                "fullName" to fullName,
                "phoneNumber" to phoneNumber
            )
        ).addOnSuccessListener {
            Log.d("UsernameToPassword", "SUCCESSFUL")
            callback()
        }.addOnFailureListener { // Revert registration process if anything fails
            // Unbind username from email
            Firebase.firestore.collection("usernames").document(username).delete()
            // Delete account
            auth.currentUser!!.delete()
        }
    }

    fun login(
        usernameOrEmail: String,
        password: String,
        callback: () -> Unit,
    ) {
        Log.d("FIREBASE", FirebaseApp.getInstance().toString())
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(usernameOrEmail, password)
            .addOnSuccessListener {
                Log.i("Login", "Login successful")
                callback()
            }
            .addOnFailureListener { exception ->
                Log.e("Login", exception.toString() )
                throw exception
            }
    }
}