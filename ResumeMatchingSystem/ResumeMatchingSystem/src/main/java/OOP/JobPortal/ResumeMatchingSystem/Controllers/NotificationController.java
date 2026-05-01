package OOP.JobPortal.ResumeMatchingSystem.Controllers;

import OOP.JobPortal.ResumeMatchingSystem.Entities.Notification;
import OOP.JobPortal.ResumeMatchingSystem.Entities.User;
import OOP.JobPortal.ResumeMatchingSystem.Services.impl.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** NotificationController – in-app notification management */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Manage in-app notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get all notifications for the logged-in user")
    public ResponseEntity<List<Notification>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(user.getEmail()));
    }

//    @GetMapping("/unread")
//    @Operation(summary = "Get unread notifications")
//    public ResponseEntity<List<Notification>> getUnread(@AuthenticationPrincipal User user) {
//        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getEmail()));
//    }

    @GetMapping("/count")
    @Operation(summary = "Get count of unread notifications (for bell badge)")
    public ResponseEntity<Map<String, Long>> getCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.countUnread(user.getEmail())));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark one notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getEmail());
        return ResponseEntity.ok().build();
    }
}

