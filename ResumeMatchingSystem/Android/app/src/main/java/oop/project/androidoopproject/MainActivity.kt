package oop.project.androidoopproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import oop.project.androidoopproject.ui.NavRoutes
import oop.project.androidoopproject.ui.screens.*
import oop.project.androidoopproject.ui.theme.AndroidOopProjectTheme
import oop.project.androidoopproject.ui.viewmodel.ResumeViewModel
import oop.project.androidoopproject.util.SessionManager
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val session = SessionManager(this)
        val start   = when {
            !session.isLoggedIn()                    -> NavRoutes.LOGIN
            session.getUserRole().name == "EMPLOYER" -> NavRoutes.EMPLOYER_DASHBOARD
            else                                     -> NavRoutes.SEEKER_DASHBOARD
        }

        setContent {
            AndroidOopProjectTheme {
                JobPortalApp(session = session, startDestination = start)
            }
        }
    }
}

@Composable
fun JobPortalApp(session: SessionManager, startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable(NavRoutes.LOGIN) {
            LoginScreen(session = session, navController = navController)
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(session = session, navController = navController)
        }

        composable(NavRoutes.SEEKER_DASHBOARD) {
            SeekerDashboardScreen(session = session, navController = navController)
        }

        composable(NavRoutes.EMPLOYER_DASHBOARD) {
            EmployerDashboardScreen(session = session, navController = navController)
        }

        composable(NavRoutes.SEEKER_PROFILE) {
            SeekerProfileScreen(session = session, navController = navController)
        }



        composable(
            route     = NavRoutes.JOB_DETAIL,
            arguments = listOf(navArgument("jobId") { type = NavType.LongType })
        ) { back ->
            JobDetailScreen(
                jobId         = back.arguments?.getLong("jobId") ?: 0L,
                session       = session,
                navController = navController
            )
        }

        composable(
            route     = NavRoutes.APPLICANTS,
            arguments = listOf(
                navArgument("jobId")    { type = NavType.LongType },
                navArgument("jobTitle") { type = NavType.StringType }
            )
        ) { back ->
            ApplicantsScreen(
                jobId         = back.arguments?.getLong("jobId") ?: 0L,
                jobTitle      = URLDecoder.decode(back.arguments?.getString("jobTitle") ?: "", "UTF-8"),
                session       = session,
                navController = navController
            )
        }
    }
}










