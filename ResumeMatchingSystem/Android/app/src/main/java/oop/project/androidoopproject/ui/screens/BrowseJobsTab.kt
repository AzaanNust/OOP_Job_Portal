package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import oop.project.androidoopproject.api.RetrofitClient
import oop.project.androidoopproject.model.JobListingResponse
import oop.project.androidoopproject.model.ResumeResponse
import oop.project.androidoopproject.model.ShiftType
import oop.project.androidoopproject.ui.NavRoutes
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.ApplicationViewModel
import oop.project.androidoopproject.ui.viewmodel.JobViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ── Groq Config ───────────────────────────────────────────────────
// Get your free key at console.groq.com (no credit card needed)
private const val GROQ_API_KEY = "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"
private const val GROQ_MODEL   = "llama-3.3-70b-versatile"
private const val GROQ_URL     = "https://api.groq.com/openai/v1/chat/completions"

// ── Chat message ──────────────────────────────────────────────────
private data class ChatMessage(val role: String, val text: String)

// ─────────────────────────────────────────────────────────────────
//  BROWSE JOBS TAB
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseJobsTab(
    session: SessionManager,
    navController: NavController,
    jobVm: JobViewModel
) {
    var searchText    by remember { mutableStateOf("") }
    var selectedShift by remember { mutableStateOf<ShiftType?>(null) }
    var shiftExpanded by remember { mutableStateOf(false) }
    var chatJob       by remember { mutableStateOf<JobListingResponse?>(null) }

    val jobsState by jobVm.jobsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { jobVm.searchJobs() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Browse Jobs", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Spacer(Modifier.height(12.dp))

        // Search + shift filter row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchText, onValueChange = { searchText = it },
                label = { Text("Search by title or skill") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp), singleLine = true
            )
            ExposedDropdownMenuBox(
                expanded = shiftExpanded, onExpandedChange = { shiftExpanded = it },
                modifier = Modifier.width(130.dp)
            ) {
                OutlinedTextField(
                    value = selectedShift?.label() ?: "Shift", onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(expanded = shiftExpanded, onDismissRequest = { shiftExpanded = false }) {
                    DropdownMenuItem(text = { Text("Any Shift") }, onClick = { selectedShift = null; shiftExpanded = false })
                    ShiftType.entries.forEach { s ->
                        DropdownMenuItem(text = { Text(s.label()) }, onClick = { selectedShift = s; shiftExpanded = false })
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { jobVm.searchJobs(searchText, null, selectedShift?.name) },
                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) { Text("Search") }
            OutlinedButton(
                onClick = { searchText = ""; selectedShift = null; jobVm.searchJobs() },
                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
            ) { Text("Clear") }
        }
        Spacer(Modifier.height(12.dp))

        when (val s = jobsState) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error   -> ErrorBanner(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    EmptyState("No jobs found. Try clearing filters.")
                } else {
                    Text("${s.data.size} job(s) found", fontSize = 12.sp, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(s.data) { job ->
                            JobCard(job = job, onClick = {
                                navController.navigate(NavRoutes.jobDetail(job.id))
                            })
                            // AI Career Advisor button below each job card
                            Button(
                                onClick  = { chatJob = job },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp).height(40.dp),
                                shape    = RoundedCornerShape(8.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary.copy(alpha = 0.88f))
                            ) {
                                Icon(Icons.Default.SmartToy, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("AI Career Advisor for this Job", fontSize = 13.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
            else -> {}
        }
    }

    // Open chatbot when a job is selected
    chatJob?.let { job ->
        CareerAdvisorChat(job = job, session = session, onDismiss = { chatJob = null })
    }
}

// ─────────────────────────────────────────────────────────────────
//  CAREER ADVISOR CHAT DIALOG
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CareerAdvisorChat(
    job: JobListingResponse,
    session: SessionManager,
    onDismiss: () -> Unit
) {
    val scope     = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val token     = session.getToken() ?: ""

    var messages     by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var userInput    by remember { mutableStateOf("") }
    var isLoading    by remember { mutableStateOf(true) }
    var resumeText   by remember { mutableStateOf("") }
    var resumeLoaded by remember { mutableStateOf(false) }
    var loadError    by remember { mutableStateOf("") }

    // Auto-load resume + send initial analysis on open
    LaunchedEffect(Unit) {
        try {
            val res = RetrofitClient.apiWithAuth(token).getMyResume()
            if (res.isSuccessful) {
                val r = res.body()!!
                resumeText = buildResumeText(r)
                resumeLoaded = true

                // Send initial analysis automatically
                val greeting = groqChat(
                    systemPrompt = buildSystemPrompt(job, resumeText),
                    history      = emptyList(),
                    newMessage   = "Analyse my resume for this specific job. Give me:\n1. My match score estimate\n2. My top 3 strengths for this role\n3. The most important skills I'm missing\n4. 3 specific actions I should take this week to improve my chances"
                )
                messages  = listOf(ChatMessage("assistant", greeting))
                isLoading = false
            } else {
                loadError    = "⚠️ Resume not found. Please save your resume in the Resume tab first, then try again."
                resumeLoaded = true
                isLoading    = false
            }
        } catch (e: Exception) {
            loadError    = "⚠️ Cannot connect to server: ${e.message}"
            resumeLoaded = true
            isLoading    = false
        }
    }

    // Scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier         = Modifier.fillMaxWidth(),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SmartToy, null, tint = OrangeAccent, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("AI Career Advisor", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                }
                Text(
                    "${job.title ?: "Job"} @ ${job.companyName ?: "Company"}",
                    fontSize = 12.sp, color = TextSecondary,
                    maxLines = 1
                )
            }
        },
        text = {
            Column(Modifier.fillMaxWidth().height(430.dp)) {

                // Error banner
                if (loadError.isNotBlank()) {
                    Card(
                        shape  = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Text(loadError, modifier = Modifier.padding(10.dp), fontSize = 12.sp, color = DangerRed)
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Resume loaded indicator
                if (resumeLoaded && loadError.isBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Resume loaded automatically", fontSize = 11.sp, color = NavyPrimary)
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // Loading spinner for initial analysis
                if (isLoading && messages.isEmpty()) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NavyPrimary, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("Analysing your resume...", fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                } else {
                    // Chat messages list
                    LazyColumn(
                        state    = listState,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { msg -> ChatBubble(msg) }
                        if (isLoading) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp, color = NavyPrimary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Thinking...", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Text input + send button
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value         = userInput,
                        onValueChange = { userInput = it },
                        placeholder   = { Text("Ask a follow-up...", fontSize = 12.sp) },
                        modifier      = Modifier.weight(1f),
                        shape         = RoundedCornerShape(20.dp),
                        singleLine    = true,
                        textStyle     = TextStyle(fontSize = 13.sp)
                    )
                    IconButton(
                        enabled  = userInput.isNotBlank() && !isLoading && resumeLoaded,
                        onClick  = {
                            val q    = userInput.trim()
                            userInput = ""
                            val withUser = messages + ChatMessage("user", q)
                            messages  = withUser
                            isLoading = true
                            scope.launch {
                                val reply = groqChat(
                                    systemPrompt = buildSystemPrompt(job, resumeText),
                                    history      = withUser,
                                    newMessage   = null
                                )
                                messages  = messages + ChatMessage("assistant", reply)
                                isLoading = false
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Send, "Send",
                            tint = if (userInput.isNotBlank() && !isLoading) OrangeAccent else Color.LightGray
                        )
                    }
                }

                // Quick-action chips
                Spacer(Modifier.height(6.dp))
                val chips = listOf("Missing skills?", "Improve CV", "Cover letter", "Interview tips")
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chips) { chip ->
                        SuggestionChip(
                            onClick = {
                                if (isLoading || !resumeLoaded) return@SuggestionChip
                                val withUser = messages + ChatMessage("user", chip)
                                messages  = withUser
                                isLoading = true
                                scope.launch {
                                    val reply = groqChat(
                                        systemPrompt = buildSystemPrompt(job, resumeText),
                                        history      = withUser,
                                        newMessage   = null
                                    )
                                    messages  = messages + ChatMessage("assistant", reply)
                                    isLoading = false
                                }
                            },
                            label = { Text(chip, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NavyPrimary)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────
//  CHAT BUBBLE
// ─────────────────────────────────────────────────────────────────
@Composable
private fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment     = Alignment.Top
    ) {
        if (!isUser) {
            Icon(Icons.Default.SmartToy, null, tint = OrangeAccent,
                modifier = Modifier.size(18.dp).padding(top = 2.dp))
            Spacer(Modifier.width(6.dp))
        }
        Card(
            shape  = RoundedCornerShape(
                topStart    = if (isUser) 16.dp else 4.dp,
                topEnd      = if (isUser) 4.dp else 16.dp,
                bottomStart = 16.dp, bottomEnd = 16.dp
            ),
            colors   = CardDefaults.cardColors(
                containerColor = if (isUser) NavyPrimary else Color(0xFFF1F5F9)
            ),
            modifier = Modifier.widthIn(max = 270.dp)
        ) {
            Text(
                text     = msg.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 13.sp,
                color    = if (isUser) Color.White else TextPrimary,
                lineHeight = 19.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
//  SYSTEM PROMPT — gives the AI full context of job + resume
// ─────────────────────────────────────────────────────────────────
private fun buildSystemPrompt(job: JobListingResponse, resumeText: String): String {
    val required  = job.requiredSkillsList().joinToString(", ").ifBlank { "Not specified" }
    val preferred = job.preferredSkillsList().joinToString(", ").ifBlank { "None" }
    return """
You are an expert career advisor and resume coach with 15 years of experience in tech recruitment in Pakistan.

TARGET JOB:
- Title: ${job.title ?: "Unknown"}
- Company: ${job.companyName ?: "Unknown"}
- Location: ${job.location ?: "Not specified"}
- Required Skills: $required
- Preferred Skills: $preferred
- Min Experience Required: ${job.minExperienceYears ?: 0} year(s)
- Job Description: ${job.description?.take(500) ?: "Not provided"}

CANDIDATE'S RESUME:
$resumeText

INSTRUCTIONS:
- Be honest, specific, and actionable — not generic
- Always relate advice to THIS specific job and THIS candidate's background
- Use bullet points for lists to keep it readable on mobile
- When writing a cover letter, write a complete professional one tailored to this job
- When asked for interview tips, give tips specific to this job title and industry
- Keep each response under 300 words for readability on mobile
""".trimIndent()
}

// ─────────────────────────────────────────────────────────────────
//  BUILD RESUME TEXT from ResumeResponse object
// ─────────────────────────────────────────────────────────────────
private fun buildResumeText(r: ResumeResponse): String {
    val sb = StringBuilder()
    r.seekerName?.let          { sb.appendLine("Name: $it") }
    r.totalExperienceYears?.let{ sb.appendLine("Years of Experience: $it") }
    r.skills?.let              { sb.appendLine("\nSKILLS:\n$it") }
    r.workExperience?.let      { sb.appendLine("\nWORK EXPERIENCE:\n$it") }
    r.education?.let           { sb.appendLine("\nEDUCATION:\n$it") }
    r.certifications?.let      { sb.appendLine("\nCERTIFICATIONS:\n$it") }
    r.projects?.let            { sb.appendLine("\nPROJECTS:\n$it") }
    r.awards?.let              { sb.appendLine("\nAWARDS:\n$it") }
    r.languages?.let           { sb.appendLine("\nLANGUAGES: $it") }
    r.volunteerWork?.let       { sb.appendLine("\nVOLUNTEER WORK:\n$it") }
    r.publications?.let        { sb.appendLine("\nPUBLICATIONS:\n$it") }
    r.githubUrl?.let           { sb.appendLine("\nGitHub: $it") }
    r.linkedinUrl?.let         { sb.appendLine("LinkedIn: $it") }
    r.portfolioUrl?.let        { sb.appendLine("Portfolio: $it") }
    return if (sb.isBlank()) "No resume saved yet." else sb.toString().trim()
}

// ─────────────────────────────────────────────────────────────────
//  GROQ API CALL
//  Uses OpenAI-compatible endpoint — simple POST, no SDK needed
// ─────────────────────────────────────────────────────────────────
private suspend fun groqChat(
    systemPrompt: String,
    history: List<ChatMessage>,
    newMessage: String?        // null = last message in history is already the new user msg
): String = withContext(Dispatchers.IO) {
    try {
        val msgArray = JSONArray()

        // 1. System prompt
        msgArray.put(JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        })

        // 2. Full conversation history
        history.forEach { msg ->
            msgArray.put(JSONObject().apply {
                put("role", msg.role)
                put("content", msg.text)
            })
        }

        // 3. New user message if not already in history
        newMessage?.let {
            msgArray.put(JSONObject().apply {
                put("role", "user")
                put("content", it)
            })
        }

        val requestBody = JSONObject().apply {
            put("model", GROQ_MODEL)
            put("messages", msgArray)
            put("max_tokens", 700)
            put("temperature", 0.7)
        }

        val url  = URL(GROQ_URL)
        val conn = url.openConnection() as HttpURLConnection
        conn.apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $GROQ_API_KEY")
            doOutput      = true
            connectTimeout = 20_000
            readTimeout    = 45_000
        }

        conn.outputStream.use { it.write(requestBody.toString().toByteArray(Charsets.UTF_8)) }

        val code = conn.responseCode
        if (code != 200) {
            val err = conn.errorStream?.bufferedReader()?.readText() ?: "HTTP $code"
            return@withContext "⚠️ Groq API error ($code).\n\nMake sure your API key is correct.\n\n$err"
        }

        val responseJson = conn.inputStream.bufferedReader().readText()
        JSONObject(responseJson)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()

    } catch (e: Exception) {
        "⚠️ Cannot reach Groq API.\n\nCheck your internet connection.\n\nError: ${e.message}"
    }
}

// ─────────────────────────────────────────────────────────────────
//  MY APPLICATIONS TAB
// ─────────────────────────────────────────────────────────────────
@Composable
fun MyApplicationsTab(
    session: SessionManager,
    appVm: ApplicationViewModel
) {
    val token = session.getToken() ?: ""
    val state by appVm.myAppsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { appVm.loadMyApplications(token) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Applications", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Spacer(Modifier.height(12.dp))
        when (val s = state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error   -> ErrorBanner(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) EmptyState("No applications yet. Browse jobs and apply!")
                else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(s.data) { app -> ApplicationCard(app) }
                }
            }
            else -> {}
        }
    }
}