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
import com.davi.dev.scannermlkit.supabase
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
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

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val credentialManager = CredentialManager.create(context)

            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val serverClientId = "174909366998-f8b915l6vl2djslndqsas5athj7hprab.apps.googleusercontent.com"

//             GetGoogleIdOption for modern One Tap experience
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setNonce(hashedNonce)
                .setAutoSelectEnabled(false)
                .build()

//            val googleIdOption = GetSignInWithGoogleOption.Builder(
//                serverClientId = serverClientId
//            )
//                .setNonce(hashedNonce)
//                .build()

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
                val errorMsg = "Nenhuma conta Google encontrada no dispositivo."
                Log.w("AuthViewModel", "NoCredentialException: No credentials available. This usually means the user has no Google accounts signed in on this device.")
                _authState.value = AuthState.Error(errorMsg)
            } catch (e: GetCredentialCancellationException) {
                val message = e.message ?: ""
                if (message.contains("[16]") || message.contains("reauth failed")) {
                    val errorMsg = "Erro de configuração: Verifique se o SHA-1 e o nome do pacote estão corretos no Google Cloud Console."
                    Log.e("AuthViewModel", "Error 16: Account reauth failed. Configuration issue likely.")
                    _authState.value = AuthState.Error(errorMsg)
                } else {
                    Log.i("AuthViewModel", "User cancelled the sign-in flow")
                    _authState.value = AuthState.Idle
                }
            } catch (e: GetCredentialException) {
                Log.e("AuthViewModel", "GetCredentialException: ${e.message}", e)
                _authState.value = AuthState.Error(e.message ?: "Falha na autenticação")
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("AuthViewModel", "GoogleIdTokenParsingException", e)
                _authState.value = AuthState.Error("Falha ao processar o token do Google")
            } catch (e: RestException) {
                Log.e("AuthViewModel", "RestException", e)
                _authState.value = AuthState.Error("Falha na autenticação com o servidor")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception", e)
                _authState.value = AuthState.Error(e.message ?: "Ocorreu um erro desconhecido")
            }
        }
    }
}
