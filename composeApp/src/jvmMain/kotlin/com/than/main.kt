package com.than

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.google.gson.Gson
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser


fun main() = application {
    val density = LocalDensity.current
    //var isVisible by remember { mutableStateOf(true) }

    val state = rememberWindowState(
        width = convertPxToDp(600f, density).toInt().dp,
        height = convertPxToDp(500f, density).toInt().dp,
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center)
    )
    Window(
        onCloseRequest = {
            exitApplication()
        },
        undecorated = true,
        state = state,
        //visible = isVisible,
    ) {
        val windowHandle = remember(this.window) {
            val windowPointer = this.window
                .windowHandle
                .let(::Pointer)
            WinDef.HWND(windowPointer)
        }
        remember(windowHandle) { CustomWindowProcedure(windowHandle) }
        /*if (!isVisible) {
            Tray(
                TrayIcon,
                tooltip = "Counter",
                onAction = { isVisible = true },
                menu = {
                    Item("Exit", onClick = ::exitApplication)
                },
            )
        }*/


        Column {
            val currentPathFiles = remember { mutableStateListOf<File>() }
            val pathList = remember { mutableStateListOf("/首页") }
            var nextTimeToRefresh by remember { mutableStateOf("") }
            var isUploading by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                launch(Dispatchers.IO) {
                    while (!Network.connectWithServer()) {
                        delay(1000)
                        println("retry....")
                    }
                    MassageHandle.sendHello()
                    launch(Dispatchers.IO) {
                        while (true) {
                            val data = Network.acceptMessage()
                            when (data.dataName) {
                                SocketMassage.CONFIG.dataName -> {
                                    Base.config = Gson().fromJson(data.data, Config::class.java)
                                    launch(Dispatchers.Main) {
                                        currentPathFiles.clear()
                                        currentPathFiles.addAll(Base.config?.rootFolders?.map { s ->
                                            File(
                                                s
                                            )
                                        }
                                            ?: emptyList())
                                        nextTimeToRefresh =
                                            "${if (Base.config != null) (Base.config!!.lastUpdateTime + Base.config!!.scanIntervalTime).formatTimestampToMin() else null}"

                                        pathList.removeRange(1, pathList.size)
                                    }
                                }

                                SocketMassage.UPLOAD_START.dataName -> {
                                    launch(Dispatchers.Main) {
                                        isUploading = true
                                    }
                                }

                                SocketMassage.UPLOAD_END.dataName -> {
                                    launch(Dispatchers.Main) {
                                        isUploading = false
                                    }
                                }

                                SocketMassage.CONFIG_SAVE_ERROR.dataName -> {
                                    showErrorDialog("更新配置失败")
                                }
                            }
                        }
                    }
                }
            }

            WindowDraggableArea {
                WindowManagerBar(
                    currentPathFiles = currentPathFiles,
                    pathList = pathList,
                    onSettingWindowClose = {
                        nextTimeToRefresh =
                            "${if (Base.config != null) (Base.config!!.lastUpdateTime + Base.config!!.scanIntervalTime).formatTimestampToMin() else null}"
                    }
                ) { s ->
                    when (s) {
                        WindowButtonState.Close -> {
                            exitApplication()
                        }

                        WindowButtonState.Maximize -> {
                            state.placement = WindowPlacement.Maximized
                        }

                        WindowButtonState.Minimize -> {
                            User32.INSTANCE.CloseWindow(windowHandle)
                            //isVisible = false
                        }

                        WindowButtonState.Restore -> {
                            state.placement = WindowPlacement.Floating
                        }
                    }
                }
            }
            App(currentPathFiles, pathList, nextTimeToRefresh, isUploading)

        }
    }
}


@Composable
fun WindowManagerBar(
    currentPathFiles: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    onSettingWindowClose: () -> Unit,
    onButtonClick: (WindowButtonState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    Column(
        modifier = Modifier.padding(bottom = 1.dp).background(hexToColor("#D2D0CE"))
            .padding(bottom = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(Color.White)) {
            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                var showAddFolderWindow by remember { mutableStateOf(false) }
                var showSettingDialog by remember { mutableStateOf(false) }

                Text(
                    "设置", modifier = Modifier.clickable {
                        showSettingDialog = true
                    }.background(Color.White).padding(start = 8.dp, end = 8.dp)
                        .height(convertPxToDp(40f, density).toInt().dp)

                        .wrapContentSize(Alignment.Center),
                    fontSize = 12.sp
                )
                Text(
                    "添加目录",
                    modifier = Modifier.clickable {
                        showAddFolderWindow = true
                    }.background(Color.White)
                        .padding(start = 8.dp, end = 8.dp)
                        .height(convertPxToDp(40f, density).toInt().dp)
                        .wrapContentSize(Alignment.Center),
                    fontSize = 12.sp
                )
                SettingWindow(showSettingDialog) {
                    showSettingDialog = false
                    onSettingWindowClose()
                }
                FileChooserDialog(show = showAddFolderWindow) {
                    if (it != null) {
                        Base.config?.rootFolders?.add(it)
                        scope.launch(Dispatchers.IO) {
                            if (!Base.setConfig()) {
                                showErrorDialog("更新配置失败")
                            }
                            currentPathFiles.clear()
                            val f = Base.config?.rootFolders?.map { s -> File(s) }
                            launch(Dispatchers.Main) {
                                currentPathFiles.addAll(
                                    f ?: emptyList()
                                )
                                pathList.removeRange(1, pathList.size)
                            }
                        }
                    }
                    showAddFolderWindow = false
                }
            }
            Text(
                text = "文件管理器",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 12.sp
            )
            WindowManageBar(
                onButtonClick = onButtonClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
fun FileChooserDialog(
    show: Boolean,
    onFileSelected: (String?) -> Unit
) {
    if (show) {
        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                val dialog = JFileChooser()
                dialog.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                dialog.currentDirectory = File("./")
                val result = dialog.showOpenDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    val selectedFolder: File = dialog.selectedFile
                    launch(Dispatchers.Main) {
                        onFileSelected(selectedFolder.absolutePath)
                    }
                } else {
                    onFileSelected(null)
                }
            }
        }
    }
}


/*
object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}
*/