package com.example.gramakhata.ui

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.gramakhata.viewmodel.KhataViewModel

@Composable
fun LoginScreen(
    viewModel: KhataViewModel,
    onLoginSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as FragmentActivity

    val executor = ContextCompat.getMainExecutor(context)

    val biometricPrompt = remember {
        BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewModel.loginWithBiometric()
                    onLoginSuccess()
                }
            }
        )
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Use fingerprint to login")
            .setNegativeButtonText("Cancel")
            .build()
    }

    val biometricManager = BiometricManager.from(context)
    val canUseBiometric =
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS

    // ✅ ANIMATION STATE (added)
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(true) {
        scale.animateTo(1f, animationSpec = tween(800))
    }

    // ---------------- UI ----------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ✅ ANIMATED ICON (replaced only this part)
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Welcome to Grama Khata", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Enter PIN or use fingerprint")

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            label = { Text("4-Digit PIN") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPin = !showPin }) {
                    Icon(
                        if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val success = viewModel.loginWithPin(pin)
                if (success) onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (canUseBiometric) {
            OutlinedButton(
                onClick = { biometricPrompt.authenticate(promptInfo) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login with Fingerprint")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { showResetDialog = true }) {
            Text("Forgot PIN?")
        }
    }

    if (showResetDialog) {
        var newPin by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetPin(newPin)
                    showResetDialog = false
                }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Reset PIN") },
            text = {
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text("New PIN") }
                )
            }
        )
    }
} 