package com.example.hobbittracker.presentation.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbittracker.R
import com.example.hobbittracker.domain.entity.User
import com.example.hobbittracker.domain.usecase.auth.*
import com.example.hobbittracker.domain.utils.Result
import com.example.hobbittracker.presentation.home.HomeActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInFacebookUseCase: SignInFacebookUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val currentUserUseCase: CurrentUserUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val tag = this.javaClass.simpleName

    private var callbackManager: CallbackManager? = null

    private lateinit var oneTapClient: SignInClient
    private var blockOneTapUI: Boolean = false
    private var isGoogleLogin: Boolean = true

    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _currentUserMLD = MutableLiveData<User>(User())
    val currentUserLD: LiveData<User>
        get() = _currentUserMLD

    //Email
    fun register(
        name: String,
        email: String,
        password: String,
        activity: Activity
    ) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = registerUseCase(name, email, password)) {
                    is Result.Success -> {
                        _toast.value = activity.getString(R.string.registration_successful)
                        login(email, password, activity)
                    }
                    is Result.Error -> {
                        _toast.value = result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = activity.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun login(email: String, password: String, activity: Activity) {
        launchDataLoad {
            viewModelScope.launch {
                when (val result = loginUseCase(email, password)) {
                    is Result.Success -> {
                        _currentUserMLD.value = result.data
                        if (activity is LoginActivity)
                            _toast.value = activity.getString(R.string.login_successful)
                        startHomeActivity(activity = activity)
                    }
                    is Result.Error -> {
                        _toast.value = result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = activity.getString(R.string.request_canceled)
                    }
                }
            }
        }
    }

    // Google
    fun registerWithGoogle(activity: Activity) {
        signInWithGoogle(activity, false)
    }

    fun loginWithGoogle(activity: Activity) {
        signInWithGoogle(activity, true)
    }

    //Facebook
    fun signInWithFacebook(activity: Activity) {
        launchDataLoad {
            callbackManager = CallbackManager.Factory.create()

            /* implementation on standard widget
            loginButton = findViewById(R.id.login_button)
            loginButton.setReadPermissions(listOf("email", "public_profile"))
            loginButton.registerCallback(
                callbackManager, object : FacebookCallback<LoginResult> { ...
             */

            val loginManagerInstance = LoginManager.getInstance()
            loginManagerInstance.logInWithReadPermissions(
                activity,
                listOf("email", "public_profile")
            )

            loginManagerInstance.registerCallback(
                callbackManager, object : FacebookCallback<LoginResult> {
                    @SuppressLint("NullSafeMutableLiveData")
                    override fun onSuccess(result: LoginResult) {
                        viewModelScope.launch {
                            when (val res = signInFacebookUseCase(
                                result.accessToken.token
                            )) {
                                is Result.Success -> {
                                    _currentUserMLD.value = res.data
                                    startHomeActivity(activity)
                                }
                                is Result.Error -> {
                                    Log.e(tag, res.exception.message.toString())
                                    _toast.value = res.exception.message
                                }
                                is Result.Canceled -> {
                                    _toast.value =
                                        activity.getString(R.string.request_canceled)
                                }
                            }
                        }
                    }

                    override fun onError(error: FacebookException) {
                        Log.e(tag, "Result.Error - ${error.message}")
                        _toast.value = error.message
                    }

                    override fun onCancel() {
                        Log.d(tag, "Result.Canceled")
                        _toast.value =
                            activity.applicationContext.getString(R.string.request_canceled)
                    }
                })
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    suspend fun getCurrentUser(): User? {
        val user = currentUserUseCase()
        if (user != null) _currentUserMLD.value = user
        return user
    }

    fun logOutUser() {
        viewModelScope.launch {
            logoutUseCase()
//            _currentUserMLD.postValue()
        }
    }

    fun resetPassword(email: String, activity: Activity) {
        viewModelScope.launch {
            when (val result = resetPasswordUseCase(email)) {
                is Result.Success -> {
                    _toast.value = activity.getString(R.string.reset_password_successful)
                }
                is Result.Error -> {
                    _toast.value = result.exception.message
                }
                is Result.Canceled -> {
                    _toast.value = activity.getString(R.string.request_canceled)
                }
            }
        }
    }


    fun onToastShown() {
        _toast.value = null
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                Log.e(tag, error.message.toString())
                _toast.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }


    private fun startHomeActivity(activity: Activity) {
        activity.startActivity(Intent(activity, HomeActivity::class.java))
    }

    fun startActivity(
        activity: Activity,
        cls: Class<*>,
        clearTasks: Boolean = false,
        finish: Boolean = false
    ) {
        val i = Intent(activity, cls)
        if (clearTasks) {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                .or(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(i)
        if (finish) activity.finish()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activity: Activity) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_ONE_TAP) {
            handleGoogleAccessToken(data, activity)
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private fun handleGoogleAccessToken(
        data: Intent?,
        activity: Activity
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            credential.googleIdToken?.let { idToken ->
                launchDataLoad {
                    viewModelScope.launch {
                        when (val result = signInGoogleUseCase(idToken, null)) {
                            is Result.Success -> {
                                _currentUserMLD.value = result.data
                                _toast.value = if (isGoogleLogin)
                                    activity.getString(R.string.login_successful)
                                else activity.getString(R.string.registration_successful)
                                startHomeActivity(activity)
                            }
                            is Result.Error -> {
                                Log.e(tag, result.exception.message.toString())
                                _toast.value = result.exception.message
                            }
                            is Result.Canceled -> {
                                Log.e(tag, result.exception?.message.toString())
                                _toast.value = activity.getString(R.string.request_canceled)
                            }
                        }
                    }
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.e(tag, "One-tap dialog was closed.")
                    _toast.value = activity.getString(R.string.request_canceled)
                    // Don't re-prompt the user.
//                    showOneTapUI = false
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    _toast.value = "One-tap encountered a network error."
                    // Try again or just ignore.
                }
                else -> {
                    _toast.value = "Couldn't get credential from result." +
                            " (${e.localizedMessage})"
                }
            }
        } catch (exception: Exception) {
            _toast.value += "<br/>" +
                    if (isGoogleLogin)
                        activity.getString(R.string.login_failed)
                    else
                        activity.getString(R.string.registration_failed)
        } finally {
            blockOneTapUI = false
        }
    }

    private fun signInWithGoogle(activity: Activity, isLogin: Boolean = false) {
        // if one tap blocked the user is already trying to log in
        if (blockOneTapUI) return

        // block the one tap while google sign in process is not over
        blockOneTapUI = true

        isGoogleLogin = isLogin

        oneTapClient = Identity.getSignInClient(activity)

        val signUpRequest = AuthService.createGoogleSignInRequest(
            activity.getString(R.string.default_web_client_id),
            isLogin
        )

        // If the user hasn't already declined to use One Tap sign-in
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(activity) { result ->
                try {
                    startIntentSenderForResult(
                        activity,
                        result.pendingIntent.intentSender,
                        REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    blockOneTapUI = false
                    Log.e(tag, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }.addOnFailureListener(activity) {
                it.localizedMessage?.let { e ->
                    Log.e(tag, e)
                    // No saved credentials found. Show error
                    _toast.value = e
                    blockOneTapUI = false
                }
            }

    }

    companion object {
        private const val REQ_ONE_TAP = 12345
    }
}