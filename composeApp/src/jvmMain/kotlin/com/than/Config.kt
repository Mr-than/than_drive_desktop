package com.than

import androidx.compose.runtime.Stable

@Stable
data class Config(
    val rootFolders: ArrayList<String>,
    val lastUpdateTime: Long,
    var scanIntervalTime: Long,//每xx上传一次文件
    var bootUp: Boolean,//开机启动
    var singleFileCount: Int,//服务器最多保留历史版本xx个
    var downLoadPath: String,
    //要附带端口 http://ip:port
    var serverIp:String,
    var verify: String
)
