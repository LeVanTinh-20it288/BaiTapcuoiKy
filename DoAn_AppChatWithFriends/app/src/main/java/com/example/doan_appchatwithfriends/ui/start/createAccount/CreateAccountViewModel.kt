package com.example.doan_appchatwithfriends.ui.start.createAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.doan_appchatwithfriends.data.Event
import com.example.doan_appchatwithfriends.data.Result
import com.example.doan_appchatwithfriends.data.db.entity.User
import com.example.doan_appchatwithfriends.data.db.repository.AuthRepository
import com.example.doan_appchatwithfriends.data.db.repository.DatabaseRepository
import com.example.doan_appchatwithfriends.data.model.CreateUser
import com.example.doan_appchatwithfriends.ui.DefaultViewModel
import com.example.doan_appchatwithfriends.util.isEmailValid
import com.example.doan_appchatwithfriends.util.isTextValid
import com.google.firebase.auth.FirebaseUser

class CreateAccountViewModel : DefaultViewModel() {

    private val dbRepository = DatabaseRepository()
    private val authRepository = AuthRepository()
    private val mIsCreatedEvent = MutableLiveData<Event<FirebaseUser>>()

    val isCreatedEvent: LiveData<Event<FirebaseUser>> = mIsCreatedEvent
    val displayNameText = MutableLiveData<String>() // Two way
    val emailText = MutableLiveData<String>() // Two way
    val passwordText = MutableLiveData<String>() // Two way
    val isCreatingAccount = MutableLiveData<Boolean>()

    private fun createAccount() {
        isCreatingAccount.value = true
        val createUser =
            CreateUser(displayNameText.value!!, emailText.value!!, passwordText.value!!)

        authRepository.createUser(createUser) { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {
                mIsCreatedEvent.value = Event(result.data!!)
                dbRepository.updateNewUser(User().apply {
                    info.id = result.data.uid
                    info.displayName = createUser.displayName
                })
            }
            if (result is Result.Success || result is Result.Error) isCreatingAccount.value = false
        }
    }

    fun createAccountPressed() {
        if (!isTextValid(2, displayNameText.value)) {
            mSnackBarText.value = Event("T??n c???a b???n qu?? ng???n")
            return
        }

        if (!isEmailValid(emailText.value.toString())) {
            mSnackBarText.value = Event("Kh??ng ????ng ?????nh d???ng email")
            return
        }
        if (!isTextValid(6, passwordText.value)) {
            mSnackBarText.value = Event("M???t kh???u qu?? ng???n")
            return
        }

        createAccount()
    }
}