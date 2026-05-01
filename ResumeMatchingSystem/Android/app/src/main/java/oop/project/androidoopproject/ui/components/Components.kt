package oop.project.androidoopproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import oop.project.androidoopproject.model.AppStage
import oop.project.androidoopproject.model.ApplicationResponse
import oop.project.androidoopproject.model.JobListingResponse
import oop.project.androidoopproject.ui.theme.*

// ── Score Badge ───────────────────────────────────────────────────

@Composable
fun MatchBadge(score: Int) {
    val (bg, fg) = when {
        score >= 75 -> Color(0xFFDCFCE7) to Color(0xFF14532D)
        score >= 50 -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        else        -> Color(0xFFFEE2E2) to Color(0xFF7F1D1D)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text     = "$score%",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp, color = fg, fontWeight = FontWeight.Bold
        )
    }
}

// ── Stage Badge ───────────────────────────────────────────────────

@Composable
fun StageBadge(stage: AppStage?) {
    if (stage == null) return
    val bg: Color
    val fg: Color
    when (stage) {
        AppStage.APPLIED             -> { bg = StageAppliedBg;    fg = StageAppliedFg    }
        AppStage.SCREENING           -> { bg = StageScreeningBg;  fg = StageScreeningFg  }
        AppStage.INTERVIEW_SCHEDULED -> { bg = StageInterviewBg;  fg = StageInterviewFg  }
        AppStage.OFFER_SENT          -> { bg = StageOfferBg;      fg = StageOfferFg      }
        AppStage.HIRED               -> { bg = StageHiredBg;      fg = StageHiredFg      }
        AppStage.REJECTED            -> { bg = StageRejectedBg;   fg = StageRejectedFg   }
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text     = stage.label(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp, color = fg, fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Match Progress Bar ────────────────────────────────────────────

@Composable
fun MatchProgressBar(score: Int) {
    val color = when {
        score >= 75 -> SuccessGreen
        score >= 50 -> WarningAmber
        else        -> DangerRed
    }
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Match Score", fontSize = 12.sp, color = TextSecondary)
            Text("$score%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress    = { score / 100f },
            modifier    = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color       = color,
            trackColor  = BorderGray
        )
    }
}

// ── Tag Chip ──────────────────────────────────────────────────────

@Composable
fun Tag(text: String, bgColor: Color = TagBlueBg, textColor: Color = NavyPrimary) {
    Surface(shape = RoundedCornerShape(6.dp), color = bgColor) {
        Text(
            text     = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp, color = textColor, fontWeight = FontWeight.Medium
        )
    }
}

// ── Company Avatar ────────────────────────────────────────────────

@Composable
fun CompanyAvatar(initial: String, size: Int = 44) {
    Box(
        modifier        = Modifier.size(size.dp).clip(RoundedCornerShape(10.dp)).background(NavyPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size / 2.5).sp)
    }
}

// ── Job Card ──────────────────────────────────────────────────────

@Composable
fun JobCard(job: JobListingResponse, matchScore: Int? = null, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CompanyAvatar(job.companyInitial())
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        job.title ?: "Untitled",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Text(
                        "${job.companyName ?: ""} · ${job.companyLocation ?: ""}",
                        fontSize = 12.sp, color = TextSecondary
                    )
                }
                if (matchScore != null) MatchBadge(matchScore)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!job.location.isNullOrBlank())
                    Tag(job.location, TagBlueBg, NavyPrimary)
                job.shiftType?.let {
                    Tag(it.label(), TagAmberBg, Color(0xFF92400E))
                }
                job.jobType?.let { Tag(it, TagBlueBg, NavyPrimary) }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                job.description ?: "",
                fontSize = 13.sp,
                color    = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(job.salaryText(), fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                Text(
                    "${job.totalApplicants ?: 0} applicants",
                    fontSize = 11.sp, color = TextSecondary
                )
            }
        }
    }
}

// ── Application Card ──────────────────────────────────────────────

@Composable
fun ApplicationCard(app: ApplicationResponse) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(
                        app.jobTitle ?: "Unknown Position",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        color      = TextPrimary
                    )
                    Text(app.companyName ?: "", fontSize = 13.sp, color = TextSecondary)
                }
                StageBadge(app.stage)
            }
            Spacer(Modifier.height(10.dp))
            MatchProgressBar(app.matchPercent())
            if (app.requiredMissing().isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Skills to improve:", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    app.requiredMissing().take(3).forEach {
                        Tag(it.removeSuffix(" ⚠ required"), TagRedBg, DangerRed)
                    }
                }
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String) {
    Text(
        text       = title,
        fontSize   = 18.sp,
        fontWeight = FontWeight.Bold,
        color      = TextPrimary,
        modifier   = Modifier.padding(vertical = 8.dp)
    )
}

// ── Primary Button ────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled  = enabled && !isLoading,
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color    = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

// ── Error Banner ──────────────────────────────────────────────────

@Composable
fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = Color(0xFFFEE2E2),
        shape    = RoundedCornerShape(8.dp)
    ) {
        Text(
            text     = message,
            modifier = Modifier.padding(12.dp),
            color    = DangerRed,
            fontSize = 14.sp
        )
    }
}

// ── Loading Screen ────────────────────────────────────────────────

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = NavyPrimary)
    }
}

// ── Empty State ───────────────────────────────────────────────────

@Composable
fun EmptyState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = TextSecondary, fontSize = 15.sp)
    }
}
