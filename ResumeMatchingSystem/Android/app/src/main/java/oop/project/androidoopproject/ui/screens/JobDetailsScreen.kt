package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import oop.project.androidoopproject.model.UserRole
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.ApplicationViewModel
import oop.project.androidoopproject.ui.viewmodel.JobViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Long,
    session: SessionManager,
    navController: NavController,
    jobVm: JobViewModel        = viewModel(),
    appVm: ApplicationViewModel = viewModel()
) {
    val jobState  by jobVm.jobDetailState.collectAsStateWithLifecycle()
    val applyState by appVm.applyState.collectAsStateWithLifecycle()

    var showApplyDialog by remember { mutableStateOf(false) }
    var coverLetter     by remember { mutableStateOf("") }
    var applyDone       by remember { mutableStateOf(false) }

    LaunchedEffect(jobId) { jobVm.getJobById(jobId) }

    LaunchedEffect(applyState) {
        if (applyState is UiState.Success) {
            applyDone = true
            appVm.resetApplyState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Job Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        when (val s = jobState) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error   -> Box(Modifier.fillMaxSize().padding(padding)) { ErrorBanner(s.message) }
            is UiState.Success -> {
                val job = s.data
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CompanyAvatar(job.companyInitial(), size = 56)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(job.title ?: "Untitled", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                            Text(job.companyName ?: "", fontSize = 14.sp, color = TextSecondary)
                            Text(job.companyLocation ?: "", fontSize = 13.sp, color = TextSecondary)
                        }
                    }

                    // Tags
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        job.location?.let { Tag(it, TagBlueBg, NavyPrimary) }
                        job.shiftType?.let { Tag(it.label(), TagAmberBg, Color(0xFF92400E)) }
                        job.jobType?.let { Tag(it, TagBlueBg, NavyPrimary) }
                    }

                    // Salary & experience
                    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Salary", fontSize = 12.sp, color = TextSecondary)
                                Text(job.salaryText(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SuccessGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Experience", fontSize = 12.sp, color = TextSecondary)
                                Text("${job.minExperienceYears ?: 0}+ years", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Applicants", fontSize = 12.sp, color = TextSecondary)
                                Text("${job.totalApplicants ?: 0}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            }
                        }
                    }

                    // Description
                    SectionHeader("Description")
                    Text(job.description ?: "No description provided.", fontSize = 14.sp, color = TextPrimary, lineHeight = 22.sp)

                    // Required skills
                    if (job.requiredSkillsList().isNotEmpty()) {
                        SectionHeader("Required Skills")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            job.requiredSkillsList().forEach { Tag(it, TagBlueBg, NavyPrimary) }
                        }
                    }

                    // Preferred skills
                    if (job.preferredSkillsList().isNotEmpty()) {
                        SectionHeader("Preferred Skills")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            job.preferredSkillsList().forEach { Tag(it, TagGreenBg, SuccessGreen) }
                        }
                    }

                    // Apply button (seeker only)
                    if (session.getUserRole() == UserRole.JOB_SEEKER) {
                        if (applyDone) {
                            Card(
                                shape  = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
                            ) {
                                Text(
                                    "Application submitted successfully!",
                                    modifier = Modifier.padding(16.dp),
                                    color = SuccessGreen, fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            if (applyState is UiState.Error) ErrorBanner((applyState as UiState.Error).message)
                            PrimaryButton(
                                text      = "Apply Now",
                                isLoading = applyState is UiState.Loading,
                                onClick   = { showApplyDialog = true }
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }

    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title   = { Text("Apply for this Job") },
            text    = {
                Column {
                    Text("Add a cover letter (optional):")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = coverLetter,
                        onValueChange = { coverLetter = it },
                        placeholder   = { Text("Why are you a great fit?") },
                        modifier      = Modifier.fillMaxWidth().height(120.dp),
                        shape         = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        appVm.applyForJob(session.getToken() ?: "", jobId, coverLetter)
                        showApplyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                ) { Text("Submit Application") }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = false }) { Text("Cancel") }
            }
        )
    }
}
