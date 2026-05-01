package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import oop.project.androidoopproject.ui.NavRoutes
import oop.project.androidoopproject.ui.components.ErrorBanner
import oop.project.androidoopproject.ui.components.PrimaryButton
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.AuthViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

@Composable
fun LoginScreen(
    session: SessionManager,
    navController: NavController,
    vm: AuthViewModel = viewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val state by vm.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            val role = (state as UiState.Success).data.role.name
            navController.navigate(
                if (role == "EMPLOYER") NavRoutes.EMPLOYER_DASHBOARD else NavRoutes.SEEKER_DASHBOARD
            ) { popUpTo(NavRoutes.LOGIN) { inclusive = true } }
            vm.resetStates()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyPrimary)))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Job Portal", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Resume Matching System", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
            Spacer(Modifier.height(40.dp))

            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Sign In", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Email") },
                        modifier      = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Password") },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon  = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    if (showPass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    if (state is UiState.Error) {
                        ErrorBanner((state as UiState.Error).message)
                    }

                    PrimaryButton(
                        text      = "Sign In",
                        onClick   = { vm.login(email, password, session) },
                        enabled   = email.isNotBlank() && password.isNotBlank(),
                        isLoading = state is UiState.Loading
                    )

                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Text("Don't have an account? ", fontSize = 14.sp, color = TextSecondary)
                        Text(
                            "Register",
                            fontSize  = 14.sp,
                            color     = NavyPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier  = Modifier.clickable {
                                navController.navigate(NavRoutes.REGISTER)
                            }
                        )
                    }
                }
            }
        }
    }
}
