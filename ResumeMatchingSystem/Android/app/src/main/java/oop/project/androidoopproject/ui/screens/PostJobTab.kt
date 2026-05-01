package oop.project.androidoopproject.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import oop.project.androidoopproject.model.PostJobRequest
import oop.project.androidoopproject.model.ShiftType
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.JobViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobTab(
    session: SessionManager,
    jobVm: JobViewModel,
    onPosted: () -> Unit
) {
    val token     = session.getToken() ?: ""
    val postState by jobVm.postJobState.collectAsStateWithLifecycle()

    var title           by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var location        by remember { mutableStateOf("") }
    var requiredSkills  by remember { mutableStateOf("") }
    var preferredSkills by remember { mutableStateOf("") }
    var minExp          by remember { mutableStateOf("") }
    var minSalary       by remember { mutableStateOf("") }
    var maxSalary       by remember { mutableStateOf("") }
    var jobType         by remember { mutableStateOf("Full-time") }
    var selectedShift   by remember { mutableStateOf<ShiftType?>(null) }
    var shiftExpanded   by remember { mutableStateOf(false) }
    var typeExpanded    by remember { mutableStateOf(false) }

    LaunchedEffect(postState) {
        if (postState is UiState.Success) {
            jobVm.resetPostJobState()
            onPosted()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Post a New Job", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)

        PostField("Job Title *", title, { title = it })
        PostField("Job Description *", description, { description = it }, singleLine = false)
        PostField("Location (City) *", location, { location = it })

        // Shift dropdown
        ExposedDropdownMenuBox(
            expanded         = shiftExpanded,
            onExpandedChange = { shiftExpanded = it }
        ) {
            OutlinedTextField(
                value         = selectedShift?.label() ?: "Select Shift *",
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Shift Type") },
                trailingIcon  = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                shape         = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(
                expanded         = shiftExpanded,
                onDismissRequest = { shiftExpanded = false }
            ) {
                ShiftType.entries.forEach { s ->
                    DropdownMenuItem(
                        text    = { Text(s.label()) },
                        onClick = { selectedShift = s; shiftExpanded = false }
                    )
                }
            }
        }

        // Job type dropdown
        val jobTypes = listOf("Full-time", "Part-time", "Contract", "Internship", "Freelance")
        ExposedDropdownMenuBox(
            expanded         = typeExpanded,
            onExpandedChange = { typeExpanded = it }
        ) {
            OutlinedTextField(
                value         = jobType,
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Job Type") },
                trailingIcon  = { Icon(Icons.Default.ArrowDropDown, null) },
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                shape         = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(
                expanded         = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                jobTypes.forEach { t ->
                    DropdownMenuItem(
                        text    = { Text(t) },
                        onClick = { jobType = t; typeExpanded = false }
                    )
                }
            }
        }

        PostField("Required Skills (comma-separated) *", requiredSkills, { requiredSkills = it })
        PostField("Preferred Skills (optional)", preferredSkills, { preferredSkills = it })

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PostField("Min Exp (yrs)", minExp,     { minExp = it },     modifier = Modifier.weight(1f))
            PostField("Min Salary",    minSalary,  { minSalary = it },  modifier = Modifier.weight(1f))
            PostField("Max Salary",    maxSalary,  { maxSalary = it },  modifier = Modifier.weight(1f))
        }

        if (postState is UiState.Error) ErrorBanner((postState as UiState.Error).message)

        PrimaryButton(
            text      = "Post Job",
            isLoading = postState is UiState.Loading,
            enabled   = title.isNotBlank() && description.isNotBlank()
                    && location.isNotBlank() && requiredSkills.isNotBlank()
                    && selectedShift != null,
            onClick   = {
                jobVm.postJob(
                    token,
                    PostJobRequest(
                        title              = title.trim(),
                        description        = description.trim(),
                        location           = location.trim(),
                        shiftType          = selectedShift!!,
                        requiredSkills     = requiredSkills.ifBlank { null },
                        preferredSkills    = preferredSkills.ifBlank { null },
                        minExperienceYears = minExp.toIntOrNull() ?: 0,
                        jobType            = jobType,
                        minSalary          = minSalary.toDoubleOrNull(),
                        maxSalary          = maxSalary.toDoubleOrNull()
                    )
                )
            }
        )
    }
}

@Composable
private fun PostField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label) },
        modifier      = modifier
            .fillMaxWidth()
            .then(if (!singleLine) Modifier.height(100.dp) else Modifier),
        singleLine    = singleLine,
        shape         = RoundedCornerShape(10.dp)
    )
}