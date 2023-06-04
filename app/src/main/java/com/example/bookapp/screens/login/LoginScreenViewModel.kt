package com.example.bookapp.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginScreenViewModel: ViewModel() {
//    val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)

    val loading: LiveData<Boolean> = _loading

    fun signInWithWEmailAndPassword(email: String, password: String, home: ()-> Unit)
    = viewModelScope.launch{
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        Log.d("FB", "signInWithWEmailAndPassword:Success: ${task.result}")
                        home()
                    }else if (!task.isSuccessful){
                        Log.d("FB", "signInWithWEmailAndPassword:Failed: ${task.result}")
                    }
                }
        }catch (ex: java.lang.Exception){
            Log.d("FB", "signInWithWEmailAndPassword: ${ex.message}")
        }
    }
    
    fun createUserWithEmailAndPassword(email: String, password: String, home: ()-> Unit){
        if (_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        val displayName = task.result.user!!.email!!.split("@")[0]
                        createUser(displayName)
                        home()
                    }else{
                        Log.d("FB", "createUserWithEmailAndPassword:User Created! ${task.result}")
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(null,userId.toString(), displayName.toString(),
            avatarUrl = "", quote = "Books are great",
            profession = "Android Developer").toMap()

        FirebaseFirestore.getInstance().collection("users")
            .add(user)
    }
}