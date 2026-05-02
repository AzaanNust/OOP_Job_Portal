package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import oop.project.androidoopproject.model.ApplicationResponse
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.ApplicationViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicantsScreen(
    jobId: Long,
    jobTitle: String,
    session: SessionManager,
    navController: NavController,
    appVm: ApplicationViewModel = viewModel()
) {
    val token = session.getToken() ?: ""
    val state by appVm.applicantsState.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) { appVm.loadApplicants(token, jobId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = jobTitle,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium // Ensures clean typography
                    )
                }
                         },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor        = NavyPrimary,
                    titleContentColor     = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when (val s = state) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Error   -> ErrorBanner(s.message)
                is UiState.Success -> {
                    Text(
                        "${s.data.size} applicant(s) — sorted by match score",
                        fontSize = 13.sp, color = TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    if (s.data.isEmpty()) {
                        EmptyState("No applicants yet for this job.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(s.data) { app ->
                                ApplicantCard(
                                    app       = app,
                                    onAdvance = { appVm.advanceApplication(token, app.id) { appVm.loadApplicants(token, jobId) } },
                                    onReject  = { appVm.rejectApplication(token, app.id)  { appVm.loadApplicants(token, jobId) } }
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ApplicantCard(
    app: ApplicationResponse,
    onAdvance: () -> Unit,
    onReject: () -> Unit
) {
    val isTerminal = app.stage?.name == "HIRED" || app.stage?.name == "REJECTED"

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(app.seekerName ?: "Unknown", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                    Text(app.seekerEmail ?: "", fontSize = 12.sp, color = TextSecondary)
                }
                StageBadge(app.stage)
            }
            Spacer(Modifier.height(10.dp))
            MatchProgressBar(app.matchPercent())

            if (app.requiredMissing().isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("Missing required: ${app.requiredMissing().joinToString(", ") { it.removeSuffix(" ⚠ required") }}",
                    fontSize = 12.sp, color = DangerRed)
            }

            if (!isTerminal) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAdvance,
                        shape   = RoundedCornerShape(8.dp),
                        colors  = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) { Text("Advance", fontSize = 13.sp) }

                    OutlinedButton(
                        onClick = onReject,
                        shape   = RoundedCornerShape(8.dp),
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) { Text("Reject", fontSize = 13.sp) }
                }
            }
        }
    }
}
