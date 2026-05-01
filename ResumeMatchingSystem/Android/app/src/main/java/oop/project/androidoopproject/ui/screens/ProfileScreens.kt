package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import oop.project.androidoopproject.model.ShiftType
import oop.project.androidoopproject.model.UpdateEmployerRequest
import oop.project.androidoopproject.model.UpdateSeekerRequest
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.ProfileViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

// ── Seeker Profile Screen ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerProfileScreen(
    session: SessionManager,
    navController: NavController,
    vm: ProfileViewModel = viewModel()
) {
    val token        = session.getToken() ?: ""
    val profileState by vm.seekerProfile.collectAsStateWithLifecycle()
    val updateState  by vm.updateState.collectAsStateWithLifecycle()

    var fullName      by remember { mutableStateOf("") }
    var phone         by remember { mutableStateOf("") }
    var location      by remember { mutableStateOf("") }
    var summary       by remember { mutableStateOf("") }
    var selectedShift by remember { mutableStateOf<ShiftType?>(null) }
    var shiftExpanded by remember { mutableStateOf(false) }
    var initialized   by remember { mutableStateOf(false) }
    var showSuccess   by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.loadSeekerProfile(token) }

    LaunchedEffect(profileState) {
        if (profileState is UiState.Success && !initialized) {
            val p = (profileState as UiState.Success).data
            fullName      = p.fullName ?: ""
            phone         = p.phoneNumber ?: ""
            location      = p.preferredLocation ?: ""
            summary       = p.profileSummary ?: ""
            selectedShift = p.preferredShift
            initialized   = true
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UiState.Success) { showSuccess = true; vm.resetUpdateState() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = NavyPrimary,
                    titleContentColor          = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (profileState) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error   -> ErrorBanner((profileState as UiState.Error).message)
                else -> {
                    Card(
                        shape  = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Text(
                            "ℹ️ Years of experience and skills can be set in My Resume tab.",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp, color = NavyPrimary
                        )
                    }

                    Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

                    PF("Full Name", fullName, { fullName = it })
                    PF("Phone Number", phone, { phone = it })
                    PF("Preferred City (for job search)", location, { location = it })
                    PF("Profile Summary / Bio", summary, { summary = it }, singleLine = false)

                    // Preferred Shift — used for job search filters
                    Text("Preferred Shift (used for job search)", fontSize = 13.sp, color = TextSecondary)
                    ExposedDropdownMenuBox(expanded = shiftExpanded, onExpandedChange = { shiftExpanded = it }) {
                        OutlinedTextField(
                            value         = selectedShift?.label() ?: "No preference",
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Preferred Shift") },
                            trailingIcon  = { Icon(Icons.Default.ArrowDropDown, null) },
                            modifier      = Modifier.fillMaxWidth().menuAnchor(),
                            shape         = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(expanded = shiftExpanded, onDismissRequest = { shiftExpanded = false }) {
                            DropdownMenuItem(text = { Text("No preference") }, onClick = { selectedShift = null; shiftExpanded = false })
                            ShiftType.entries.forEach { s ->
                                DropdownMenuItem(text = { Text(s.label()) }, onClick = { selectedShift = s; shiftExpanded = false })
                            }
                        }
                    }

                    if (updateState is UiState.Error) ErrorBanner((updateState as UiState.Error).message)
                    if (showSuccess) {
                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))) {
                            Text("Profile updated!", modifier = Modifier.padding(12.dp), color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    PrimaryButton(
                        text      = "Save Changes",
                        isLoading = updateState is UiState.Loading,
                        enabled   = fullName.isNotBlank(),
                        onClick   = {
                            showSuccess = false
                            vm.updateSeekerProfile(token, UpdateSeekerRequest(
                                fullName          = fullName.ifBlank { null },
                                phoneNumber       = phone.ifBlank { null },
                                preferredLocation = location.ifBlank { null },
                                preferredShift    = selectedShift,
                                profileSummary    = summary.ifBlank { null }
                                // totalExperienceYears goes in Resume, not Profile
                            ))
                        }
                    )
                }
            }
        }
    }
}

// ── Employer Profile Screen ───────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerProfileScreen(
    session: SessionManager,
    navController: NavController,
    vm: ProfileViewModel = viewModel()
) {
    val token        = session.getToken() ?: ""
    val profileState by vm.employerProfile.collectAsStateWithLifecycle()
    val updateState  by vm.updateState.collectAsStateWithLifecycle()

    var fullName     by remember { mutableStateOf("") }
    var phone        by remember { mutableStateOf("") }
    var companyName  by remember { mutableStateOf("") }
    var industry     by remember { mutableStateOf("") }
    var website      by remember { mutableStateOf("") }
    var description  by remember { mutableStateOf("") }
    var companySize  by remember { mutableStateOf("") }
    var initialized  by remember { mutableStateOf(false) }
    var showSuccess  by remember { mutableStateOf(false) }
    var sizeExpanded by remember { mutableStateOf(false) }

    val sizeOptions = listOf("1-10", "11-50", "51-200", "201-500", "500+")

    LaunchedEffect(Unit) { vm.loadEmployerProfile(token) }

    LaunchedEffect(profileState) {
        if (profileState is UiState.Success && !initialized) {
            val p = (profileState as UiState.Success).data
            fullName    = p.fullName ?: ""
            phone       = p.phoneNumber ?: ""
            companyName = p.companyName ?: ""
            industry    = p.industry ?: ""
            website     = p.companyWebsite ?: ""
            description = p.companyDescription ?: ""
            companySize = p.companySize ?: ""
            initialized = true
        }
    }

    LaunchedEffect(updateState) {
        if (updateState is UiState.Success) { showSuccess = true; vm.resetUpdateState() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Company Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = NavyPrimary,
                    titleContentColor          = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (profileState) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error   -> ErrorBanner((profileState as UiState.Error).message)
                else -> {
                    Text("Personal Information", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    PF("Full Name", fullName, { fullName = it })
                    PF("Phone Number", phone, { phone = it })

                    HorizontalDivider()
                    Text("Company Information", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

                    Card(
                        shape  = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Text(
                            "ℹ️ Company city is set per job listing — a company can operate in multiple cities.",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp, color = NavyPrimary
                        )
                    }

                    PF("Company Name *", companyName, { companyName = it })
                    PF("Industry (e.g. Information Technology)", industry, { industry = it })
                    PF("Company Website URL", website, { website = it })
                    PF("Company Description", description, { description = it }, singleLine = false)

                    ExposedDropdownMenuBox(expanded = sizeExpanded, onExpandedChange = { sizeExpanded = it }) {
                        OutlinedTextField(
                            value         = companySize.ifBlank { "Select Company Size" },
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Company Size") },
                            trailingIcon  = { Icon(Icons.Default.ArrowDropDown, null) },
                            modifier      = Modifier.fillMaxWidth().menuAnchor(),
                            shape         = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(expanded = sizeExpanded, onDismissRequest = { sizeExpanded = false }) {
                            sizeOptions.forEach { sz ->
                                DropdownMenuItem(text = { Text(sz) }, onClick = { companySize = sz; sizeExpanded = false })
                            }
                        }
                    }

                    if (updateState is UiState.Error) ErrorBanner((updateState as UiState.Error).message)
                    if (showSuccess) {
                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))) {
                            Text("Profile updated!", modifier = Modifier.padding(12.dp), color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    PrimaryButton(
                        text      = "Save Changes",
                        isLoading = updateState is UiState.Loading,
                        enabled   = companyName.isNotBlank(),
                        onClick   = {
                            showSuccess = false
                            vm.updateEmployerProfile(token, UpdateEmployerRequest(
                                fullName           = fullName.ifBlank { null },
                                phoneNumber        = phone.ifBlank { null },
                                companyName        = companyName.ifBlank { null },
                                industry           = industry.ifBlank { null },
                                companyWebsite     = website.ifBlank { null },
                                companyDescription = description.ifBlank { null },
                                companySize        = companySize.ifBlank { null }
                                // No companyLocation — city is per job listing
                            ))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PF(label: String, value: String, onValueChange: (String) -> Unit, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().then(if (!singleLine) Modifier.height(100.dp) else Modifier),
        singleLine = singleLine, shape = RoundedCornerShape(10.dp)
    )
}