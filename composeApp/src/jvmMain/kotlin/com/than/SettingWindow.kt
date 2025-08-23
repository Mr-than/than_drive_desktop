package com.than

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingWindow(isDialogOpen: Boolean = false, onClose: () -> Unit = {}) {
    if (isDialogOpen) {
        val state = rememberDialogState(position = WindowPosition(Alignment.Center))
        DialogWindow(
            onCloseRequest = { onClose() },
            state = state,
            undecorated = true
        ) {
            val windowHandle = remember(this.window) {
                val windowPointer = this.window
                    .windowHandle
                    .let(::Pointer)
                WinDef.HWND(windowPointer)
            }
            remember(windowHandle) { CustomWindowProcedure(windowHandle) }
            val scope = rememberCoroutineScope()
            Column {
                var num by remember {
                    mutableStateOf("${(Base.config?.singleFileCount) ?: 5}")
                }
                var downloadPath by remember {
                    mutableStateOf(
                        Base.config?.downLoadPath ?: "./"
                    )
                }
                var textTime by remember {
                    mutableStateOf(
                        "${
                            (Base.config?.scanIntervalTime)?.div(
                                1000
                            )?.div(60) ?: 30
                        }"
                    )
                }
                var fileChooserShow by remember { mutableStateOf(false) }
                var bootUp by remember { mutableStateOf(Base.config?.bootUp ?: false) }
                var serverUrl by remember {
                    mutableStateOf(
                        Base.config?.serverIp ?: "http://127.0.0.1:8080"
                    )
                }

                WindowDraggableArea {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "设置",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 12.sp
                        )

                        WindowManageBar(
                            modifier = Modifier.align(alignment = Alignment.CenterEnd),
                            minimizeVisibility = false,
                            midVisibility = false
                        ) {
                            when (it) {
                                WindowButtonState.Close -> {
                                    onClose()
                                }

                                else -> {}
                            }
                        }
                    }
                }
                val verticalScrollState = rememberScrollState(0)
                Column(modifier = Modifier.verticalScroll(verticalScrollState)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp).padding(top = 8.dp)
                    ) {
                        Text("扫描并上传文件间隔时间（分钟）: ")
                        BasicTextField(
                            value = textTime,
                            onValueChange = {
                                if (it.toIntOrNull() != null) {
                                    textTime = it
                                } else if (it.isEmpty()) {
                                    textTime = it
                                }
                            },
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Column {
                                    innerTextField()
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp).padding(top = 1.dp)
                                    ) {
                                        drawLine(
                                            color = Color.Gray,
                                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                            end = androidx.compose.ui.geometry.Offset(
                                                size.width,
                                                0f
                                            ),
                                            strokeWidth = size.height
                                        )
                                    }
                                }
                            }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp).padding(top = 8.dp)
                    ) {
                        Text("服务器保存的历史文件个数: ")
                        BasicTextField(
                            value = num, onValueChange = {
                                if (it.toIntOrNull() != null) {
                                    num = it
                                } else if (it.isEmpty()) {
                                    num = it
                                }
                            },
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Column {
                                    innerTextField()
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp).padding(top = 1.dp)
                                    ) {
                                        drawLine(
                                            color = Color.Gray,
                                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                            end = androidx.compose.ui.geometry.Offset(
                                                size.width,
                                                0f
                                            ),
                                            strokeWidth = size.height
                                        )
                                    }
                                }
                            }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp).padding(top = 8.dp)
                    ) {

                        Text("服务器地址: ")
                        BasicTextField(
                            value = serverUrl, onValueChange = {
                                serverUrl = it
                            },
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Column {
                                    innerTextField()
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp).padding(top = 1.dp)
                                    ) {
                                        drawLine(
                                            color = Color.Gray,
                                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                            end = androidx.compose.ui.geometry.Offset(
                                                size.width,
                                                0f
                                            ),
                                            strokeWidth = size.height
                                        )
                                    }
                                }
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp).padding(top = 8.dp)
                    ) {
                        Text("下载地址: ")
                        Text(
                            text = downloadPath,
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp)
                                .clickable {
                                    fileChooserShow = true
                                },
                            color = Color.Blue
                        )

                        FileChooserDialog(fileChooserShow) {
                            fileChooserShow = false
                            it?.let {
                                downloadPath = it
                                Base.config?.downLoadPath = it
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp).padding(top = 8.dp)
                    ) {
                        Text(
                            "开机启动",
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp)
                        )

                        Checkbox(bootUp, onCheckedChange = {
                            bootUp = it
                        })
                    }
                    Button(
                        onClick = {
                            do {
                                if (textTime.toLong() > 0 && textTime.toLong() <= 120) {
                                    Base.config?.scanIntervalTime = textTime.toLong() * 60 * 1000
                                } else {
                                    showErrorDialog("扫描间隔时间只能为1-120分钟")
                                    break
                                }
                                if (num.toInt() in 1..10) {
                                    Base.config?.singleFileCount = num.toInt()
                                } else {
                                    showErrorDialog("历史文件个数只能为1-10个")
                                    break
                                }

                                if (serverUrl.isNotEmpty()) {
                                    Base.config?.serverIp = serverUrl
                                } else {
                                    showErrorDialog("服务器地址不能为空")
                                    break
                                }

                                Base.config?.bootUp = bootUp
                                scope.launch(Dispatchers.IO) {
                                    if (!Base.setConfig()) {
                                        showErrorDialog("更新配置失败")
                                    }
                                    scope.launch(Dispatchers.Main) {
                                        onClose()
                                    }
                                }
                            } while (false)
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = hexToColor("#0078d7"))
                    ) {
                        Text(
                            "保存设置",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}