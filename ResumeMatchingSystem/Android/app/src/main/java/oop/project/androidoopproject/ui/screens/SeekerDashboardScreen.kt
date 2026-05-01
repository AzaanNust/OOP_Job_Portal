package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import oop.project.androidoopproject.ui.NavRoutes
import oop.project.androidoopproject.ui.theme.NavyPrimary
import oop.project.androidoopproject.ui.theme.OrangeAccent
import oop.project.androidoopproject.ui.viewmodel.*
import oop.project.androidoopproject.util.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekerDashboardScreen(
    session: SessionManager,
    navController: NavController
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val jobVm:    JobViewModel           = viewModel()
    val appVm:    ApplicationViewModel   = viewModel()
    val resumeVm: ResumeViewModel        = viewModel()
    val notifVm:  NotificationViewModel  = viewModel()

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
                    role  = "Job Seeker"
                )
                HorizontalDivider()
                NavigationDrawerItem(label = { Text("My Profile") },
                    icon = { Icon(Icons.Default.Person, null) }, selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(NavRoutes.SEEKER_PROFILE) })
                NavigationDrawerItem(label = { Text("My Resume") },
                    icon = { Icon(Icons.Default.Description, null) }, selected = false,
                    onClick = { selectedTab = 2; scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("My Applications") },
                    icon = { Icon(Icons.Default.Assignment, null) }, selected = false,
                    onClick = { selectedTab = 1; scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("Browse Jobs") },
                    icon = { Icon(Icons.Default.Search, null) }, selected = false,
                    onClick = { selectedTab = 0; scope.launch { drawerState.close() } })
                NavigationDrawerItem(label = { Text("Notifications") },
                    icon = { Icon(Icons.Default.Notifications, null) }, selected = false,
                    onClick = { selectedTab = 3; scope.launch { drawerState.close() } })
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
                    title = { Text("Job Portal") },
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
                    // Jobs
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Search, "Jobs") },
                        label = { Text("Jobs") },
                        colors = navBarColors())
                    // Applied
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Assignment, "Applied") },
                        label = { Text("Applied") },
                        colors = navBarColors())
                    // Resume
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Description, "Resume") },
                        label = { Text("Resume") },
                        colors = navBarColors())
                    // Alerts — with badge
                    NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 },
                        icon = { BellWithBadge(unreadCount) },
                        label = { Text("Alerts") },
                        colors = navBarColors())
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (selectedTab) {
                    0 -> BrowseJobsTab(session = session, navController = navController, jobVm = jobVm)
                    1 -> MyApplicationsTab(session = session, appVm = appVm)
                    2 -> ResumeScreen(session = session, navController = navController, resumeVm = resumeVm)
                    3 -> NotificationsTab(session = session, notifVm = notifVm)
                }
            }
        }
    }
}

@Composable
private fun navBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = OrangeAccent,
    selectedTextColor   = OrangeAccent,
    unselectedIconColor = Color.White.copy(alpha = 0.6f),
    unselectedTextColor = Color.White.copy(alpha = 0.6f),
    indicatorColor      = Color.White.copy(alpha = 0.15f)
)