package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import oop.project.androidoopproject.model.JobListingResponse
import oop.project.androidoopproject.model.ShiftType
import oop.project.androidoopproject.ui.NavRoutes
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.JobViewModel
import oop.project.androidoopproject.ui.viewmodel.NotificationViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerDashboardScreen(
    session: SessionManager,
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val jobVm:   JobViewModel          = viewModel()
    val notifVm: NotificationViewModel = viewModel()

    val unreadCount by notifVm.unreadCount.collectAsStateWithLifecycle()
    val token        = session.getToken() ?: ""
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()

    LaunchedEffect(Unit) { notifVm.loadUnreadCount(token) }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(
                    name  = session.getUserName(),
                    email = session.getUserEmail(),
                    role  = "Employer"
                )
                HorizontalDivider()
                NavigationDrawerItem(label = { Text("Company Profile") },
                    icon = { Icon(Icons.Default.Business, null) }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(NavRoutes.EMPLOYER_PROFILE) })
                NavigationDrawerItem(label = { Text("My Job Listings") },
                    icon = { Icon(Icons.Default.Work, null) }, selected = false,
                    onClick = { selectedTab = 0; scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("Post New Job") },
                    icon = { Icon(Icons.Default.AddCircle, null) }, selected = false,
                    onClick = { selectedTab = 1; scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("Notifications") },
                    icon = { Icon(Icons.Default.Notifications, null) }, selected = false,
                    onClick = { selectedTab = 2; scope.launch { drawerState.close() } })
                HorizontalDivider()
                NavigationDrawerItem(label = { Text("Log Out") },
                    icon = { Icon(Icons.Default.Logout, null) }, selected = false,
                    onClick = {
                        session.clearSession()
                        navController.navigate(NavRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                    })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title  = { Text("Job Portal — Employer") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor             = NavyPrimary,
                        titleContentColor          = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = NavyPrimary) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Work, "My Jobs") },
                        label = { Text("My Jobs") }, colors = employerNavColors())
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.AddCircle, "Post Job") },
                        label = { Text("Post Job") }, colors = employerNavColors())
                    // Alerts with badge
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                        icon = { BellWithBadge(unreadCount) },
                        label = { Text("Alerts") }, colors = employerNavColors())
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (selectedTab) {
                    0 -> MyJobsTab(session = session, jobVm = jobVm, navController = navController)
                    1 -> PostJobTab(
                        session  = session,
                        jobVm    = jobVm,
                        onPosted = { selectedTab = 0; jobVm.loadMyJobs(token) }
                    )
                    2 -> NotificationsTab(session = session, notifVm = notifVm)
                }
            }
        }
    }
}

@Composable
private fun employerNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = OrangeAccent,
    selectedTextColor   = OrangeAccent,
    unselectedIconColor = Color.White.copy(alpha = 0.6f),
    unselectedTextColor = Color.White.copy(alpha = 0.6f),
    indicatorColor      = Color.White.copy(alpha = 0.15f)
)

// ── My Jobs Tab ───────────────────────────────────────────────────

@Composable
fun MyJobsTab(
    session: SessionManager,
    jobVm: JobViewModel,
    navController: NavController
) {
    val token = session.getToken() ?: ""
    val state by jobVm.myJobsState.collectAsStateWithLifecycle()
    var jobToEdit   by remember { mutableStateOf<JobListingResponse?>(null) }
    var jobToDelete by remember { mutableStateOf<JobListingResponse?>(null) }

    LaunchedEffect(Unit) { jobVm.loadMyJobs(token) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Job Listings", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Spacer(Modifier.height(12.dp))

        when (val s = state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error   -> ErrorBanner(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) EmptyState("No jobs posted yet. Use 'Post Job' to add one.")
                else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(s.data) { job ->
                        EmployerJobCard(
                            job              = job,
                            onViewApplicants = { navController.navigate(NavRoutes.applicants(job.id, job.title ?: "Job")) },
                            onEdit           = { jobToEdit = job },
                            onClose          = { jobVm.closeJob(token, job.id) { jobVm.loadMyJobs(token) } },
                            onReactivate     = { jobVm.reactivateJob(token, job.id) { jobVm.loadMyJobs(token) } },
                            onDelete         = { jobToDelete = job }
                        )
                    }
                }
            }
            else -> {}
        }
    }

    jobToEdit?.let { job ->
        EditJobDialog(job = job, token = token, jobVm = jobVm,
            onDone = { jobToEdit = null; jobVm.loadMyJobs(token) },
            onDismiss = { jobToEdit = null })
    }

    jobToDelete?.let { job ->
        AlertDialog(
            onDismissRequest = { jobToDelete = null },
            title   = { Text("Delete Job?") },
            text    = { Text("\"${job.title}\" will be permanently deleted.") },
            confirmButton = {
                Button(onClick = { jobVm.deleteJob(token, job.id) { jobVm.loadMyJobs(token) }; jobToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { jobToDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EmployerJobCard(
    job: JobListingResponse,
    onViewApplicants: () -> Unit,
    onEdit: () -> Unit,
    onClose: () -> Unit,
    onReactivate: () -> Unit,
    onDelete: () -> Unit
) {
    val isOpen = job.isOpen()
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(job.title ?: "Untitled", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
                    Text("${job.location ?: ""}  •  ${job.shiftType?.label() ?: ""}", fontSize = 13.sp, color = TextSecondary)
                }
                Surface(shape = RoundedCornerShape(20.dp), color = if (isOpen) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)) {
                    Text(job.status ?: "OPEN", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp, color = if (isOpen) SuccessGreen else DangerRed, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("${job.totalApplicants ?: 0} applicants  •  ${job.salaryText()}", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = onViewApplicants, shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.People, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Applicants", fontSize = 12.sp)
                }
                OutlinedButton(onClick = onEdit, shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Edit, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Edit", fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isOpen) {
                    OutlinedButton(onClick = onClose, shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningAmber),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.PauseCircle, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Close", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(onClick = onReactivate, shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.PlayCircle, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Reactivate", fontSize = 12.sp)
                    }
                }
                OutlinedButton(onClick = onDelete, shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Delete, null, Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Delete", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditJobDialog(job: JobListingResponse, token: String, jobVm: JobViewModel, onDone: () -> Unit, onDismiss: () -> Unit) {
    var title       by remember { mutableStateOf(job.title ?: "") }
    var description by remember { mutableStateOf(job.description ?: "") }
    var location    by remember { mutableStateOf(job.location ?: "") }
    var reqSkills   by remember { mutableStateOf(job.requiredSkills ?: "") }
    var prefSkills  by remember { mutableStateOf(job.preferredSkills ?: "") }
    var minSalary   by remember { mutableStateOf(job.minSalary?.toString() ?: "") }
    var maxSalary   by remember { mutableStateOf(job.maxSalary?.toString() ?: "") }
    var shiftExp    by remember { mutableStateOf(false) }
    var selectedShift by remember { mutableStateOf(job.shiftType ?: ShiftType.FLEXIBLE) }

    val updateState by jobVm.updateJobState.collectAsStateWithLifecycle()
    LaunchedEffect(updateState) { if (updateState is UiState.Success) { jobVm.resetUpdateJobState(); onDone() } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Job", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                EF("Title *", title, { title = it })
                EF("Description *", description, { description = it }, singleLine = false)
                EF("Location *", location, { location = it })
                ExposedDropdownMenuBox(expanded = shiftExp, onExpandedChange = { shiftExp = it }) {
                    OutlinedTextField(value = selectedShift.label(), onValueChange = {}, readOnly = true,
                        label = { Text("Shift") }, trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(10.dp))
                    ExposedDropdownMenu(expanded = shiftExp, onDismissRequest = { shiftExp = false }) {
                        ShiftType.entries.forEach { s -> DropdownMenuItem(text = { Text(s.label()) },
                            onClick = { selectedShift = s; shiftExp = false }) }
                    }
                }
                EF("Required Skills", reqSkills, { reqSkills = it })
                EF("Preferred Skills", prefSkills, { prefSkills = it })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EF("Min Salary", minSalary, { minSalary = it }, modifier = Modifier.weight(1f))
                    EF("Max Salary", maxSalary, { maxSalary = it }, modifier = Modifier.weight(1f))
                }
                if (updateState is UiState.Error) ErrorBanner((updateState as UiState.Error).message)
            }
        },
        confirmButton = {
            Button(enabled = title.isNotBlank() && description.isNotBlank() && location.isNotBlank() && updateState !is UiState.Loading,
                onClick = {
                    jobVm.updateJob(token, job.id, oop.project.androidoopproject.model.PostJobRequest(
                        title = title.trim(), description = description.trim(), location = location.trim(),
                        shiftType = selectedShift, requiredSkills = reqSkills.ifBlank { null },
                        preferredSkills = prefSkills.ifBlank { null }, minExperienceYears = job.minExperienceYears ?: 0,
                        jobType = job.jobType, minSalary = minSalary.toDoubleOrNull(), maxSalary = maxSalary.toDoubleOrNull()))
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)) {
                if (updateState is UiState.Loading) CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                else Text("Save Changes")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun EF(label: String, value: String, onValueChange: (String) -> Unit, singleLine: Boolean = true, modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label, fontSize = 12.sp) },
        modifier = modifier.fillMaxWidth().then(if (!singleLine) Modifier.height(90.dp) else Modifier),
        singleLine = singleLine, shape = RoundedCornerShape(10.dp))
}

/**
 * Shared drawer header used by both SeekerDashboardScreen and EmployerDashboardScreen.
 * Defined here (top-level, non-private) so both files in the same package can use it.
 */
@Composable
fun DrawerHeader(name: String, email: String, role: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Surface(
            shape    = RoundedCornerShape(28.dp),
            color    = NavyPrimary,
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.fillMaxSize()
            ) {
                Text(
                    text       = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 22.sp
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(name,  fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
        Text(email, fontSize = 13.sp, color = TextSecondary)
        Text(role,  fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.Medium)
    }
}