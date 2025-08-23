package com.than

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import drive_desktop.composeapp.generated.resources.Res
import drive_desktop.composeapp.generated.resources.file_big
import drive_desktop.composeapp.generated.resources.folder
import drive_desktop.composeapp.generated.resources.folder_big
import drive_desktop.composeapp.generated.resources.home
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App(
    files: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    nextTimeToRefresh: String,
    isUploading: Boolean
) {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            val isSole by remember { derivedStateOf { pathList.size == 1 } }
            var hostFolderPath by remember { mutableStateOf("") }
            if (pathList.size == 1) {
                hostFolderPath = ""
            }
            val scope = rememberCoroutineScope()
            val horizontalScrollState = rememberScrollState(0)
            LaunchedEffect(pathList.size) {
                horizontalScrollState.scrollTo(Int.MAX_VALUE)
            }
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(horizontalScrollState)
                        .padding(start = 4.dp, end = 8.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "首页",
                        modifier = Modifier.padding(end = 8.dp, start = 4.dp).clickable {
                            if (!isSole) {
                                backHome(files = files, pathList = pathList) {
                                    hostFolderPath = it
                                }
                            }
                        }
                    )
                    pathList.forEachIndexed { index, path ->
                        if (index == 1) {
                            TooltipArea(
                                tooltip = {
                                    Surface(
                                        modifier = Modifier.shadow(4.dp),
                                        color = Color(255, 255, 210),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = hostFolderPath,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                },
                                delayMillis = 600,
                                tooltipPlacement = TooltipPlacement.CursorPoint(
                                    alignment = Alignment.BottomEnd,
                                    offset = DpOffset(5.dp, 5.dp)
                                )
                            ) {
                                Text(path, fontSize = 15.sp, modifier = Modifier.clickable {
                                    if (pathList.size > 2) {
                                        backPreviousPath(
                                            files = files,
                                            pathList = pathList,
                                            index = index,
                                            hostFolderPath = hostFolderPath,
                                            scope = scope
                                        )
                                    }
                                })
                            }
                        } else if (index > 1) {
                            Text(path, fontSize = 15.sp, modifier = Modifier.clickable {
                                if (index < pathList.size - 1) {
                                    backPreviousPath(
                                        files = files,
                                        pathList = pathList,
                                        index = index,
                                        hostFolderPath = hostFolderPath,
                                        scope = scope
                                    )
                                }
                            })
                        } else {
                            Text(path, fontSize = 15.sp, modifier = Modifier.clickable {
                                if (!isSole) {
                                    backHome(files = files, pathList = pathList) {
                                        hostFolderPath = it
                                    }
                                }
                            })
                        }
                    }
                }
                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    adapter = rememberScrollbarAdapter(horizontalScrollState)
                )
            }
            FilesList(
                isSole = isSole,
                files = files,
                pathList = pathList,
                modifier = Modifier.padding(top = 8.dp).weight(1f)
            ) {
                hostFolderPath = it
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "下一次扫描时间: $nextTimeToRefresh",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Button(
                        onClick = {
                            MassageHandle.sendScan()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = hexToColor("#0078d7")),
                        enabled = !isUploading
                    ) {
                        Text(
                            text = if (isUploading) {
                                "正在扫描..."
                            } else {
                                "立即扫描"
                            }, color = Color.White
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun FilesList(
    isSole: Boolean,
    files: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    modifier: Modifier = Modifier,
    onFileClick: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    if (isSole) {
        RowList(
            files = files,
            pathList = pathList,
            scope = scope,
            modifier = modifier
        ) {
            onFileClick(it)
        }
    } else {
        if (files.isNotEmpty()) {
            GridList(
                files = files,
                pathList = pathList,
                scope = scope,
                modifier = modifier
            )
        } else {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("文件夹为空")
            }
        }
    }
}

@Composable
fun RowList(
    modifier: Modifier = Modifier,
    files: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    scope: CoroutineScope,
    onFileClick: (String) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        val state = rememberLazyListState()
        LazyColumn(state = state) {
            itemsIndexed(files) { index, file ->
                ContextMenuArea(items = {
                    listOf(
                        ContextMenuItem("删除此文件夹") {
                            scope.launch(Dispatchers.Main) {
                                files.remove(file)
                                launch(Dispatchers.IO) {
                                    Base.config?.rootFolders?.remove(file.absolutePath)
                                    if (!Base.setConfig()) {
                                        showErrorDialog("更新配置失败")
                                    }
                                }
                            }
                        }
                    )
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                            },
                            onDoubleClick = {
                                toNextPath(
                                    file = file,
                                    pathList = pathList,
                                    scope = scope,
                                    files = files,
                                    nextPath = "文件夹${index + 1}"
                                ) {
                                    onFileClick(it)
                                }
                            },
                            onLongClick = null,
                        )
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.folder),
                            contentDescription = "文件类型",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            file.absolutePath,
                            modifier = Modifier.padding(start = 4.dp),
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Composable
fun GridList(
    modifier: Modifier = Modifier,
    files: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    scope: CoroutineScope
) {
    Box(modifier = modifier.fillMaxSize()) {
        val state: LazyGridState = rememberLazyGridState()
        var showFileVersion by remember { mutableStateOf(false) }
        var filePath by remember { mutableStateOf("") }

        LazyVerticalGrid(columns = GridCells.Fixed(3), state = state) {
            items(files) {
                ContextMenuArea(items = {
                    listOf(
                        ContextMenuItem("在资源管理器中打开") {
                            scope.launch {
                                openFileInExplorer(it.absolutePath)
                            }
                        }
                    )
                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.combinedClickable(
                            onClick = {
                            },
                            onDoubleClick = {
                                if (it.isDirectory) {
                                    toNextPath(
                                        file = it,
                                        pathList = pathList,
                                        scope = scope,
                                        files = files,
                                        nextPath = it.name
                                    )
                                } else {
                                    showFileVersion = true
                                    filePath = it.absolutePath
                                }
                            },
                            onLongClick = null,
                        )
                    ) {
                        Image(
                            painter = if (it.isDirectory) {
                                painterResource(Res.drawable.folder_big)
                            } else {
                                painterResource(Res.drawable.file_big)
                            },
                            contentDescription = "文件类型",
                            modifier = Modifier.size(70.dp)
                        )
                        Text(
                            it.name, modifier = Modifier.padding(start = 32.dp, end = 32.dp)
                        )
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )

        FileVersionWindow(isDialogOpen = showFileVersion, filePath = filePath) {
            showFileVersion = false
        }
    }
}

fun toNextPath(
    file: File,
    pathList: SnapshotStateList<String>,
    scope: CoroutineScope,
    files: SnapshotStateList<File>,
    nextPath: String,
    doSomeElse: (String) -> Unit = { _ -> }
) {
    if (file.isDirectory) {
        scope.launch(Dispatchers.IO) {
            val fileList = file.listFiles()
            launch(Dispatchers.Main) {
                pathList.add("/$nextPath")
                files.apply {
                    clear()
                    addAll(fileList)
                }
                doSomeElse(file.absolutePath)
            }
        }
    }
}

fun backPreviousPath(
    files: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    index: Int,
    hostFolderPath: String,
    scope: CoroutineScope
) {
    scope.launch(Dispatchers.IO) {
        pathList.removeRange(index + 1, pathList.size)
        val fileList = File(hostFolderPath + pathList.drop(2).joinToString("")).listFiles()
        launch(Dispatchers.Main) {
            files.apply {
                files.clear()
                files.addAll(fileList)
            }
        }
    }
}

fun backHome(
    files: SnapshotStateList<File>,
    pathList: SnapshotStateList<String>,
    setHomePath: (String) -> Unit
) {
    files.clear()
    files.addAll(Base.config?.rootFolders?.map { s -> File(s) } ?: emptyList())
    pathList.removeRange(1, pathList.size)
    setHomePath("")
}

fun openFileInExplorer(path: String) {
    val file = File(path)
    if (file.exists()) {
        Desktop.getDesktop().open(file.parentFile)
    }
}