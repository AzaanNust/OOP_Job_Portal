package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import oop.project.androidoopproject.model.RegisterEmployerRequest
import oop.project.androidoopproject.model.RegisterSeekerRequest
import oop.project.androidoopproject.ui.NavRoutes
import oop.project.androidoopproject.ui.components.ErrorBanner
import oop.project.androidoopproject.ui.components.PrimaryButton
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.AuthViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

@Composable
fun RegisterScreen(
    session: SessionManager,
    navController: NavController,
    vm: AuthViewModel = viewModel()
) {
    var isSeeker    by remember { mutableStateOf(true) }
    var fullName    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var phone       by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var industry    by remember { mutableStateOf("") }
    var showPass    by remember { mutableStateOf(false) }

    val state by vm.registerState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            val role = (state as UiState.Success).data.role.name
            navController.navigate(
                if (role == "EMPLOYER") NavRoutes.EMPLOYER_DASHBOARD else NavRoutes.SEEKER_DASHBOARD
            ) { popUpTo(NavRoutes.LOGIN) { inclusive = true } }
            vm.resetStates()
        }
    }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(NavyDark, NavyPrimary)))) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                if (isSeeker) "You can set your city & experience in Profile after registering"
                else "Company city can be set per job posting",
                fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Role toggle
            Row(
                Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)).padding(4.dp)
            ) {
                listOf("Job Seeker" to true, "Employer" to false).forEach { (label, seeker) ->
                    val selected = isSeeker == seeker
                    Box(
                        Modifier.weight(1f)
                            .background(if (selected) OrangeAccent else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { isSeeker = seeker }.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) { Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceWhite), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    RegField("Full Name *", fullName, { fullName = it })
                    RegField("Email *", email, { email = it }, KeyboardType.Email)
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password *") }, modifier = Modifier.fillMaxWidth(),
                        singleLine = true, shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { showPass = !showPass }) {
                                Text(if (showPass) "Hide" else "Show", fontSize = 12.sp)
                            }
                        }
                    )
                    RegField("Phone Number (optional)", phone, { phone = it }, KeyboardType.Phone)

                    if (!isSeeker) {
                        RegField("Company Name *", companyName, { companyName = it })
                        RegField("Industry (e.g. Information Technology)", industry, { industry = it })
                    }

                    // Info box
                    Card(
                        shape  = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Text(
                            if (isSeeker)
                                "ℹ️ After registering, go to My Profile to set your preferred city, shift, and years of experience. Add skills & experience in My Resume."
                            else
                                "ℹ️ After registering, go to Company Profile to add your industry, website, and company details.",
                            modifier = Modifier.padding(12.dp),
                            fontSize  = 12.sp,
                            color     = NavyPrimary
                        )
                    }

                    if (state is UiState.Error) ErrorBanner((state as UiState.Error).message)

                    PrimaryButton(
                        text      = "Create Account",
                        isLoading = state is UiState.Loading,
                        enabled   = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                                && (isSeeker || companyName.isNotBlank()),
                        onClick   = {
                            if (isSeeker) {
                                vm.registerSeeker(
                                    RegisterSeekerRequest(
                                        fullName    = fullName.trim(),
                                        email       = email.trim(),
                                        password    = password,
                                        phoneNumber = phone.ifBlank { null }
                                    ), session
                                )
                            } else {
                                vm.registerEmployer(
                                    RegisterEmployerRequest(
                                        fullName    = fullName.trim(),
                                        email       = email.trim(),
                                        password    = password,
                                        companyName = companyName.trim(),
                                        phoneNumber = phone.ifBlank { null },
                                        industry    = industry.ifBlank { null }
                                    ), session
                                )
                            }
                        }
                    )

                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Text("Already have an account? ", fontSize = 14.sp, color = TextSecondary)
                        Text("Sign In", fontSize = 14.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { navController.popBackStack() })
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}