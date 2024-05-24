package com.jovanovicdima.eventradar.network

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.jovanovicdima.eventradar.data.User
import java.io.ByteArrayOutputStream

object Firebase {

    fun createAccount(
        email: String,
        password: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        image: ImageBitmap,
        successCallback: () -> Unit,
        failureCallback: () -> Unit
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Create account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("Register", "SUCCESSFUL")
                bindUsernameToEmail(email, username, fullName, phoneNumber, image, successCallback, failureCallback)
            }
            .addOnFailureListener { exception ->
                Log.e("Auth", exception.toString())
                failureCallback()
            }

    }

    private fun bindUsernameToEmail(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        image: ImageBitmap,
        successCallback: () -> Unit,
        failureCallback: () -> Unit
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Bind username to email
        Firebase.firestore.collection("usernames").document(username).set(
            hashMapOf(
                "email" to email
            )
        ).addOnSuccessListener {
            Log.d("UsernameToPassword", "SUCCESSFUL")
            uploadImageToFirestore(email, username, fullName, phoneNumber, image, successCallback, failureCallback)
        }.addOnFailureListener {// Revert registration process if anything fails
            // Delete account if bind fails
            auth.currentUser!!.delete()
            failureCallback()
        }
    }

    private fun uploadImageToFirestore(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        image: ImageBitmap,
        successCallback: () -> Unit,
        failureCallback: () -> Unit
    )  {
        val ref = FirebaseStorage.getInstance().reference.child("images/${FirebaseAuth.getInstance().currentUser!!.uid}.jpg")

        val stream = ByteArrayOutputStream()
        image.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream)

        ref.putBytes(stream.toByteArray())
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Handle the success (e.g., save the URL or update UI)
                    Log.d("FirebaseStorage", "Upload successful, URL: $downloadUrl")
                    writeAdditionalDataOnRegistration(email, username, fullName, phoneNumber, downloadUrl, successCallback, failureCallback)
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure
                Log.e("FirebaseStorage", "Upload failed", exception)
                Firebase.firestore.collection("usernames").document(username).delete()
                // Delete account
                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                auth.currentUser!!.delete()
                failureCallback()
            }
    }

    private fun writeAdditionalDataOnRegistration(
        email: String,
        username: String,
        fullName: String,
        phoneNumber: String,
        profilePictureURL: String,
        successCallback: () -> Unit,
        failureCallback: () -> Unit
    ) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        // Write additional user data to the database
        val user = User()
        user.email = email
        user.username = username
        user.fullName = fullName
        user.phoneNumber = phoneNumber
        user.image = profilePictureURL
        Firebase.firestore.collection("users").document(auth.currentUser!!.uid).set(user).addOnSuccessListener {
            Log.d("writeAdditionalData", "SUCCESSFUL")
            successCallback()
        }.addOnFailureListener { // Revert registration process if anything fails
            // Remove profile picture from firestore
            FirebaseStorage.getInstance().reference.child("images/${FirebaseAuth.getInstance().currentUser!!.uid}.jpg").delete()
            // Unbind username from email
            Firebase.firestore.collection("usernames").document(username).delete()
            // Delete account
            auth.currentUser!!.delete()
            failureCallback()
        }
    }

    fun login(
        usernameOrEmail: String,
        password: String,
        successCallback: () -> Unit,
        failureCallback: () -> Unit,
    ) {
        Log.d("FIREBASE", FirebaseApp.getInstance().toString())
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(usernameOrEmail, password)
            .addOnSuccessListener {
                Log.i("Login", "Login successful")
                successCallback()
            }
            .addOnFailureListener { exception ->
                Log.e("Login", exception.toString() )
                failureCallback()
            }
    }
}

fun getCurrentUser() : String? {
    return FirebaseAuth.getInstance().currentUser?.uid
}

fun getUser(uid: String, callback: (User?) -> Unit) {
    Firebase.firestore.collection("users").document(uid).get().addOnSuccessListener { document ->
        if (document.exists()) {
            callback(document.toObject(User::class.java))
        }
    }
}