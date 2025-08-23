package com.than

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

fun convertPxToDp(px: Float, density: Density): Float {
    return with(density) {
        px.toDp().value
    }
}

fun hexToColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val argb = when (cleanHex.length) {
        6 -> "FF$cleanHex" // 添加不透明 alpha
        8 -> cleanHex      // 包含 alpha
        else -> throw IllegalArgumentException("Invalid color format: $hex")
    }
    val colorLong = argb.toLong(16)
    return Color(colorLong)
}

fun Long.formatTimestampToMin(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun showErrorDialog(message: String) {
    SwingUtilities.invokeLater {
        JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE)
    }
}

fun showWindowsNotification(title: String, message: String) {
    if (SystemTray.isSupported()) {
        val tray = SystemTray.getSystemTray()
        val image: Image = Toolkit.getDefaultToolkit().createImage(ByteArray(0))
        val trayIcon = TrayIcon(image, "通知")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "通知"
        tray.add(trayIcon)
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
        tray.remove(trayIcon)
    }
}
