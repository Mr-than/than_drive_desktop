package com.than

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import drive_desktop.composeapp.generated.resources.Res
import drive_desktop.composeapp.generated.resources.closeactive
import drive_desktop.composeapp.generated.resources.maximize
import drive_desktop.composeapp.generated.resources.minimize
import drive_desktop.composeapp.generated.resources.restore
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowManageBar(
    modifier: Modifier = Modifier,
    minimizeVisibility: Boolean = true,
    midVisibility: Boolean = true,
    closeVisibility: Boolean = true,
    onButtonClick: (WindowButtonState) -> Unit = {}
) {
    val density = LocalDensity.current
    var midIcon by remember { mutableStateOf(Res.drawable.maximize) }
    var active by remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.End, modifier = modifier) {
        if (minimizeVisibility) {
            Column(
                modifier = modifier.size(convertPxToDp(40f, density).toInt().dp).clickable {
                    onButtonClick(WindowButtonState.Minimize)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
                ) {
                Image(
                    painter = painterResource(Res.drawable.minimize),
                    contentDescription = "icon",
                )
            }
        }

        if (midVisibility) {
            Column(
                modifier = modifier.size(convertPxToDp(40f, density).toInt().dp)
                    .clickable {
                        midIcon = if (midIcon == Res.drawable.maximize) {
                            onButtonClick(WindowButtonState.Maximize)
                            Res.drawable.restore
                        } else {
                            onButtonClick(WindowButtonState.Restore)
                            Res.drawable.maximize
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(midIcon),
                    contentDescription = "icon",
                )
            }
        }

        if (closeVisibility) {
            Column(
                modifier = modifier
                    .size(convertPxToDp(40f, density).toInt().dp)
                    .clickable {
                        onButtonClick(WindowButtonState.Close)
                    }
                    .background(color = if (active) Color(0xFFE81123) else Color.Transparent)
                    .onPointerEvent(eventType = PointerEventType.Enter) {
                        active = true
                    }.onPointerEvent(eventType = PointerEventType.Exit) {
                        active = false
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.closeactive),
                    contentDescription = "icon",
                )
            }
        }

    }
}

sealed class WindowButtonState {
    object Restore : WindowButtonState()
    object Minimize : WindowButtonState()
    object Maximize : WindowButtonState()
    object Close : WindowButtonState()
}