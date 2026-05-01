package oop.project.androidoopproject.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import oop.project.androidoopproject.model.SaveResumeRequest
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.ResumeViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeScreen(
    session: SessionManager,
    navController: NavController,
    resumeVm: ResumeViewModel
) {
    val token       = session.getToken() ?: ""
    val resumeState by resumeVm.resumeState.collectAsStateWithLifecycle()
    val saveState   by resumeVm.saveState.collectAsStateWithLifecycle()
    val context     = LocalContext.current
    val scope       = rememberCoroutineScope()

    var skills       by remember { mutableStateOf("") }
    var expYears     by remember { mutableStateOf("") }
    var workExp      by remember { mutableStateOf("") }
    var education    by remember { mutableStateOf("") }
    var projects     by remember { mutableStateOf("") }
    var certs        by remember { mutableStateOf("") }
    var awards       by remember { mutableStateOf("") }
    var languages    by remember { mutableStateOf("") }
    var volunteer    by remember { mutableStateOf("") }
    var publications by remember { mutableStateOf("") }
    var references   by remember { mutableStateOf("") }
    var github       by remember { mutableStateOf("") }
    var linkedin     by remember { mutableStateOf("") }
    var portfolio    by remember { mutableStateOf("") }

    var initialized   by remember { mutableStateOf(false) }
    var showSuccess   by remember { mutableStateOf(false) }
    var downloadMsg   by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(false) }
    var isLoading     by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { resumeVm.loadResume(token) }

    LaunchedEffect(resumeState) {
        when (val s = resumeState) {
            is UiState.Loading -> isLoading = true
            is UiState.Success -> {
                isLoading = false
                if (!initialized) {
                    val r        = s.data
                    skills       = r.skills ?: ""
                    expYears     = r.totalExperienceYears?.let { if (it == 0) "" else it.toString() } ?: ""
                    workExp      = r.workExperience ?: ""
                    education    = r.education ?: ""
                    projects     = r.projects ?: ""
                    certs        = r.certifications ?: ""
                    awards       = r.awards ?: ""
                    languages    = r.languages ?: ""
                    volunteer    = r.volunteerWork ?: ""
                    publications = r.publications ?: ""
                    references   = r.referencesText ?: ""
                    github       = r.githubUrl ?: ""
                    linkedin     = r.linkedinUrl ?: ""
                    portfolio    = r.portfolioUrl ?: ""
                    initialized  = true
                }
            }
            else -> isLoading = false
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) { showSuccess = true; resumeVm.resetSaveState() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Resume") },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null)
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                },
                actions = {
                    IconButton(onClick = {
                        if (isDownloading) return@IconButton
                        isDownloading = true
                        downloadMsg   = "Downloading PDF..."
                        scope.launch {
                            downloadMsg   = downloadAndOpenPdf(token, context)
                            isDownloading = false
                        }
                    }) {
                        if (isDownloading)
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        else
                            Icon(Icons.Default.Download, "Download PDF", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = NavyPrimary,
                    titleContentColor          = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = NavyPrimary)
                    Spacer(Modifier.height(12.dp))
                    Text("Loading your resume...", fontSize = 13.sp, color = TextSecondary)
                }
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (downloadMsg.isNotBlank()) {
                Card(
                    shape  = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (downloadMsg.startsWith("✅")) Color(0xFFDCFCE7)
                        else Color(0xFFFEE2E2)
                    )
                ) {
                    Text(
                        downloadMsg,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        color    = if (downloadMsg.startsWith("✅")) SuccessGreen else DangerRed
                    )
                }
            }

            Card(
                shape  = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Text(
                    "Fill in your details and tap Save. Then use the ⬇ button at the top to download your professional PDF.",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp, color = NavyPrimary
                )
            }

            SectionHeader("Core Information")
            RField("Skills * (comma-separated, e.g. Java, Python, SQL)", skills, { skills = it })
            RField("Years of Experience", expYears, { expYears = it })
            RField("Work Experience\nFormat: Job Title | Company | Dates — one entry per line", workExp, { workExp = it }, singleLine = false)
            RField("Education\nFormat: Degree | University | Year — one entry per line", education, { education = it }, singleLine = false)

            SectionHeader("Additional Sections")
            RField("Projects\nFormat: Name | Tech Stack | Description | Link — one per line", projects, { projects = it }, singleLine = false)
            RField("Certifications (one per line)", certs, { certs = it }, singleLine = false)
            RField("Awards & Achievements (one per line)", awards, { awards = it }, singleLine = false)
            RField("Languages Spoken (e.g. English, Urdu)", languages, { languages = it })
            RField("Volunteer Work (one per line)", volunteer, { volunteer = it }, singleLine = false)
            RField("Publications / Articles (one per line)", publications, { publications = it }, singleLine = false)
            RField("References\nFormat: Name | Title | Company | Email — one per line", references, { references = it }, singleLine = false)

            SectionHeader("Links")
            RField("GitHub URL", github, { github = it })
            RField("LinkedIn URL", linkedin, { linkedin = it })
            RField("Portfolio / Website URL", portfolio, { portfolio = it })

            if (saveState is UiState.Error) ErrorBanner((saveState as UiState.Error).message)

            if (showSuccess) {
                Card(
                    shape  = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7))
                ) {
                    Text(
                        "✅ Resume saved! Tap ⬇ at the top to download your PDF.",
                        modifier   = Modifier.padding(12.dp),
                        color      = SuccessGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 13.sp
                    )
                }
            }

            PrimaryButton(
                text      = "Save Resume",
                isLoading = saveState is UiState.Loading,
                enabled   = skills.isNotBlank(),
                onClick   = {
                    showSuccess = false
                    downloadMsg = ""
                    resumeVm.saveResume(token, SaveResumeRequest(
                        skills               = skills.ifBlank { null },
                        workExperience       = workExp.ifBlank { null },
                        education            = education.ifBlank { null },
                        certifications       = certs.ifBlank { null },
                        languages            = languages.ifBlank { null },
                        projects             = projects.ifBlank { null },
                        awards               = awards.ifBlank { null },
                        volunteerWork        = volunteer.ifBlank { null },
                        publications         = publications.ifBlank { null },
                        referencesText       = references.ifBlank { null },
                        totalExperienceYears = expYears.toIntOrNull(),
                        githubUrl            = github.ifBlank { null },
                        linkedinUrl          = linkedin.ifBlank { null },
                        portfolioUrl         = portfolio.ifBlank { null }
                    ))
                }
            )

            OutlinedButton(
                onClick  = {
                    if (isDownloading) return@OutlinedButton
                    isDownloading = true
                    downloadMsg   = "Downloading PDF..."
                    scope.launch {
                        downloadMsg   = downloadAndOpenPdf(token, context)
                        isDownloading = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                enabled  = !isDownloading
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                } else {
                    Icon(Icons.Default.Download, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text("Download PDF Resume", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyPrimary,
        modifier = Modifier.padding(top = 6.dp))
}

@Composable
private fun RField(label: String, value: String, onValueChange: (String) -> Unit, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        modifier = Modifier.fillMaxWidth().then(if (!singleLine) Modifier.height(100.dp) else Modifier),
        singleLine = singleLine, shape = RoundedCornerShape(10.dp)
    )
}

// ─────────────────────────────────────────────────────────────────
// PDF DOWNLOAD + OPEN
//
// Strategy:
//   Android 10+ (API 29+): Use MediaStore.Downloads to insert the PDF
//     directly into the public Downloads collection. MediaStore gives us
//     a content:// URI we can immediately pass to startActivity — no
//     FileProvider needed, no permission needed, bottom sheet appears.
//
//   Android 9 and below: Write to Environment.DIRECTORY_DOWNLOADS with
//     a File, then open via a plain file:// URI (allowed on older Android).
// ─────────────────────────────────────────────────────────────────

private suspend fun downloadAndOpenPdf(token: String, context: Context): String =
    withContext(Dispatchers.IO) {
        try {
            // 1. Fetch PDF bytes from backend
            val response = oop.project.androidoopproject.api.RetrofitClient.apiWithAuth(token).downloadMyResume()
            if (!response.isSuccessful) {
                return@withContext "❌ Save your resume first, then download. (Error ${response.code()})"
            }
            val bytes = response.body()?.bytes()
                ?: return@withContext "❌ Empty response from server."

            val fileName = "Resume_${System.currentTimeMillis()}.pdf"

            val contentUri: Uri

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // ── Android 10+ : MediaStore approach ────────────────
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                ) ?: return@withContext "❌ Could not create file in Downloads."

                // Write bytes through ContentResolver
                resolver.openOutputStream(uri)?.use { it.write(bytes) }
                    ?: return@withContext "❌ Could not write PDF file."

                // Mark as complete
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)

                contentUri = uri

            } else {
                // ── Android 9 and below : direct file write ───────────
                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { it.write(bytes) }
                contentUri = Uri.fromFile(file)
            }

            // 2. Open the PDF — shows bottom sheet with all capable apps
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Open Resume PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)

            "✅ PDF saved to Downloads and opened!"

        } catch (e: Exception) {
            "❌ Download failed: ${e.message}"
        }
    }