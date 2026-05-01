package oop.project.androidoopproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Brand Colors ──────────────────────────────────────────────────

val NavyPrimary      = Color(0xFF1A3C6E)
val NavyDark         = Color(0xFF122D52)
val NavyLight        = Color(0xFF2A5298)
val OrangeAccent     = Color(0xFFF97316)
val OrangeAccentDark = Color(0xFFEA6C0A)
val BackgroundGray   = Color(0xFFF8FAFC)
val SurfaceWhite     = Color(0xFFFFFFFF)
val TextPrimary      = Color(0xFF1E293B)
val TextSecondary    = Color(0xFF64748B)
val BorderGray       = Color(0xFFE2E8F0)
val SuccessGreen     = Color(0xFF16A34A)
val WarningAmber     = Color(0xFFD97706)
val DangerRed        = Color(0xFFDC2626)

// ── Tag Colors ────────────────────────────────────────────────────

val TagBlueBg  = Color(0xFFE8F0FE)
val TagAmberBg = Color(0xFFFEF3C7)
val TagGreenBg = Color(0xFFDCFCE7)
val TagRedBg   = Color(0xFFFEF2F2)

// ── Stage Badge Colors ────────────────────────────────────────────

val StageAppliedBg    = Color(0xFFDBEAFE)
val StageAppliedText  = Color(0xFF1E40AF)
val StageAppliedFg    = StageAppliedText     // alias

val StageScreeningBg   = Color(0xFFE0E7FF)
val StageScreeningText = Color(0xFF3730A3)
val StageScreeningFg   = StageScreeningText  // alias

val StageInterviewBg   = Color(0xFFFEF3C7)
val StageInterviewText = Color(0xFF92400E)
val StageInterviewFg   = StageInterviewText  // alias

val StageOfferBg   = Color(0xFFD1FAE5)
val StageOfferText = Color(0xFF065F46)
val StageOfferFg   = StageOfferText          // alias

val StageHiredBg   = Color(0xFFDCFCE7)
val StageHiredText = Color(0xFF14532D)
val StageHiredFg   = StageHiredText          // alias

val StageRejectedBg   = Color(0xFFFEE2E2)
val StageRejectedText = Color(0xFF7F1D1D)
val StageRejectedFg   = StageRejectedText    // alias

// ── Color Schemes ─────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary              = NavyPrimary,
    onPrimary            = Color.White,
    primaryContainer     = NavyLight,
    onPrimaryContainer   = Color.White,
    secondary            = OrangeAccent,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFFFF7ED),
    onSecondaryContainer = OrangeAccentDark,
    background           = BackgroundGray,
    onBackground         = TextPrimary,
    surface              = SurfaceWhite,
    onSurface            = TextPrimary,
    surfaceVariant       = Color(0xFFF1F5F9),
    onSurfaceVariant     = TextSecondary,
    error                = DangerRed,
    onError              = Color.White,
    outline              = BorderGray
)

private val DarkColorScheme = darkColorScheme(
    primary          = NavyLight,
    onPrimary        = Color.White,
    secondary        = OrangeAccent,
    onSecondary      = Color.White,
    background       = Color(0xFF0F172A),
    onBackground     = Color(0xFFE2E8F0),
    surface          = Color(0xFF1E293B),
    onSurface        = Color(0xFFE2E8F0),
    surfaceVariant   = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    error            = Color(0xFFF87171),
    onError          = Color.White,
    outline          = Color(0xFF475569)
)

// ── Typography ────────────────────────────────────────────────────

private val AppTypography = Typography(
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    headlineSmall  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 24.sp),
    titleMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 15.sp, lineHeight = 22.sp),
    titleSmall     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 13.sp, lineHeight = 20.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp)
)

// ── Root Theme ────────────────────────────────────────────────────

@Composable
fun JobPortalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
