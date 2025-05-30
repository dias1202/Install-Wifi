package com.dias.installwifi.data.model

data class Notification(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

val NotificationList = listOf(
    Notification(
        id = "1",
        title = "Welcome to InstallWifi",
        content = "Thank you for installing InstallWifi! We hope you enjoy using our app.",
        timestamp = System.currentTimeMillis(),
        isRead = false
    ),
    Notification(
        id = "2",
        title = "New Features Available",
        content = "Check out the latest features in the app, including improved Wi-Fi management.",
        timestamp = System.currentTimeMillis(),
        isRead = false
    )
)
