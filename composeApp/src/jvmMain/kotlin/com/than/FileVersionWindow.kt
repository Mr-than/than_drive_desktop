package com.than

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun FileVersionWindow(
    isDialogOpen: Boolean = false,
    filePath: String,
    onClose: () -> Unit = {}
) {
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
            var fileList by remember { mutableStateOf(ArrayList<String>()) }

            Column {

                WindowDraggableArea {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "版本",
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

                scope.launch(Dispatchers.IO) {
                    val list = Network.getFileVersions(filePath)
                    launch(Dispatchers.Main) {
                        fileList = list
                    }
                }


                LazyColumn {
                    items(fileList) {

                        ContextMenuArea(items = {
                            listOf(
                                ContextMenuItem("下载") {
                                    scope.launch(Dispatchers.IO) {
                                        MassageHandle.sendDownLoad(filePath, it)
                                    }
                                }
                            )
                        }) {
                            val file = File(filePath)

                            val extension = if (file.extension.isNotEmpty()) {
                                ".${file.extension}"
                            } else ""
                            val name = file.name.replaceFirst(extension, "")

                            val time =
                                it.trim()
                                    .replaceFirst(name, "")
                                    .replaceFirst(extension, "")
                                    .trim()
                                    .toLong().formatTimestampToMin()


                            Text(
                                text = "$name$extension 时间: $time",
                                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable{
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}