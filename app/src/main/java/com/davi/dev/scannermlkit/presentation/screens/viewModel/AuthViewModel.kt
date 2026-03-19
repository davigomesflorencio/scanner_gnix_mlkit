package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davi.dev.scannermlkit.BuildConfig
import com.davi.dev.scannermlkit.supabase
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.util.UUID

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object AccountCreateSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            try {
                val session = supabase.auth.currentSessionOrNull()
                if (session != null) {
                    _authState.value = AuthState.Success
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking session", e)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error signing out", e)
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email.trim()
                    this.password = password
                }
                _authState.value = AuthState.Success
            } catch (e: AuthRestException) {
                Log.e("AuthViewModel", "AuthRestException during sign in: ${e.error} - ${e.description}", e)
                val message = when (e.error) {
                    "invalid_credentials" -> "Incorrect email or password."
                    "email_not_confirmed" -> "Email not confirmed. Check your inbox."
                    else -> e.description ?: "Sign in failed: Check your credentials."
                }
                _authState.value = AuthState.Error(message)
            } catch (e: RestException) {
                Log.e("AuthViewModel", "RestException during sign in", e)
                _authState.value = AuthState.Error("Sign in failed: Check your connection.")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during sign in", e)
                _authState.value = AuthState.Error(e.message ?: "An error occurred during sign in.")
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email.trim()
                    this.password = password
                    data = buildJsonObject {
                        put("full_name", name)
                    }
                }
                _authState.value = AuthState.AccountCreateSuccess
            } catch (e: AuthRestException) {
                Log.e("AuthViewModel", "AuthRestException during sign up: ${e.error} - ${e.description}", e)
                val message = when (e.error) {
                    "user_already_exists" -> "This email is already in use."
                    else -> e.description ?: "Account creation failed: ${e.message}"
                }
                _authState.value = AuthState.Error(message)
            } catch (e: RestException) {
                Log.e("AuthViewModel", "RestException during sign up", e)
                _authState.value = AuthState.Error("Account creation failed: Check your connection.")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during sign up", e)
                _authState.value = AuthState.Error(e.message ?: "An error occurred during account creation.")
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val credentialManager = CredentialManager.create(context)

            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val serverClientId = BuildConfig.googleClientId

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(false)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                supabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }

                _authState.value = AuthState.Success
            } catch (e: NoCredentialException) {
                val errorMsg = "No Google account found on the device."
                Log.w("AuthViewModel", "NoCredentialException: No credentials available.")
                _authState.value = AuthState.Error(errorMsg)
            } catch (e: GetCredentialCancellationException) {
                val message = e.message ?: ""
                if (message.contains("[16]") || message.contains("reauth failed")) {
                    val errorMsg = "Configuration error: Check if SHA-1 and package name are correct in Google Cloud Console."
                    Log.e("AuthViewModel", "Error 16: Account reauth failed.")
                    _authState.value = AuthState.Error(errorMsg)
                } else {
                    Log.i("AuthViewModel", "User cancelled the sign-in flow")
                    _authState.value = AuthState.Idle
                }
            } catch (e: GetCredentialException) {
                Log.e("AuthViewModel", "GetCredentialException: ${e.message}", e)
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("AuthViewModel", "GoogleIdTokenParsingException", e)
                _authState.value = AuthState.Error("Failed to process Google token")
            } catch (e: AuthRestException) {
                Log.e("AuthViewModel", "AuthRestException during Google sign in", e)
                _authState.value = AuthState.Error("Server authentication failed: ${e.description ?: e.error}")
            } catch (e: RestException) {
                Log.e("AuthViewModel", "RestException", e)
                _authState.value = AuthState.Error("Server authentication failed")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception", e)
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
