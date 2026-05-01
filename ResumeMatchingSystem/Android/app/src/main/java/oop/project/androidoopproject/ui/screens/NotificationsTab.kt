package oop.project.androidoopproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import oop.project.androidoopproject.model.NotificationItem
import oop.project.androidoopproject.ui.components.*
import oop.project.androidoopproject.ui.theme.*
import oop.project.androidoopproject.ui.viewmodel.NotificationViewModel
import oop.project.androidoopproject.util.SessionManager
import oop.project.androidoopproject.util.UiState

@Composable
fun NotificationsTab(session: SessionManager, notifVm: NotificationViewModel) {
    val token = session.getToken() ?: ""
    val state by notifVm.notificationsState.collectAsStateWithLifecycle()
    val unreadCount by notifVm.unreadCount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { notifVm.loadNotifications(token) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        // Header row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Notifications",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                // Unread badge next to title
                if (unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .background(DangerRed, CircleShape)
                    ) {
                        Text(
                            text     = if (unreadCount > 99) "99+" else unreadCount.toString(),
                            color    = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (unreadCount > 0) {
                TextButton(onClick = { notifVm.markAllRead(token) }) {
                    Text("Mark all read", color = NavyPrimary, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        when (val s = state) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error   -> ErrorBanner(s.message)
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    EmptyState("No notifications yet.")
                } else {
                    // Section label if there are unread notifications
                    val unread = s.data.filter { !it.isRead }
                    val read   = s.data.filter { it.isRead }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        // ── Unread section ────────────────────────────
                        if (unread.isNotEmpty()) {
                            item {
                                Text(
                                    "New  (${unread.size})",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = NavyPrimary,
                                    modifier   = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(unread, key = { it.id }) { notif ->
                                NotificationCard(
                                    notif      = notif,
                                    onMarkRead = { notifVm.markRead(token, notif.id) }
                                )
                            }
                        }

                        // ── Read section ──────────────────────────────
                        if (read.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Earlier",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = TextSecondary,
                                    modifier   = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(read, key = { it.id }) { notif ->
                                NotificationCard(
                                    notif      = notif,
                                    onMarkRead = { /* already read */ }
                                )
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun NotificationCard(notif: NotificationItem, onMarkRead: () -> Unit) {
    // Animate background: unread = blue tint, read = plain white
    val bgColor = if (notif.isRead) SurfaceWhite else Color(0xFFEFF6FF)
    val elevation = if (notif.isRead) 1.dp else 3.dp

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                // Unread dot + subject
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (!notif.isRead) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(NavyPrimary, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text       = notif.subject ?: "Notification",
                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Medium,
                        fontSize   = 14.sp,
                        color      = TextPrimary
                    )
                }

                Spacer(Modifier.width(8.dp))

                // "Mark read" chip — changes to "Read ✓" chip after tap
                if (!notif.isRead) {
                    Surface(
                        shape   = RoundedCornerShape(20.dp),
                        color   = NavyPrimary,
                        modifier = Modifier
                    ) {
                        TextButton(
                            onClick            = onMarkRead,
                            contentPadding     = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text("Mark read", fontSize = 11.sp, color = Color.White)
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            "Read ✓",
                            fontSize = 11.sp,
                            color    = SuccessGreen,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Message body
            notif.message?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text       = it,
                    fontSize   = 13.sp,
                    color      = if (notif.isRead) TextSecondary else TextPrimary,
                    lineHeight = 18.sp
                )
            }

            // Timestamp
            notif.createdAt?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text     = it.take(16).replace("T", "  "),
                    fontSize = 11.sp,
                    color    = TextSecondary
                )
            }
        }
    }
}

/**
 * Bell icon with unread badge — used in bottom nav bars.
 * Usage: BellWithBadge(unreadCount)
 */
@Composable
fun BellWithBadge(unreadCount: Long) {
    Box {
        Icon(Icons.Default.Notifications, contentDescription = "Alerts")
        if (unreadCount > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(if (unreadCount > 9) 18.dp else 16.dp)
                    .background(DangerRed, CircleShape)
            ) {
                Text(
                    text     = if (unreadCount > 99) "99+" else unreadCount.toString(),
                    color    = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}