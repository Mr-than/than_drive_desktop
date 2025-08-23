package com.than

import com.google.gson.Gson

object MassageHandle {

    fun sendHello(): Boolean {
        val massage = SocketMassage.HELLO
        return Network.sendMessage(massage)
    }

    fun sendConfig(): Boolean {
        if (Base.config != null) {
            val massage = SocketMassage.CONFIG.apply {
                data= Gson().toJson(Base.config)
            }
            return Network.sendMessage(massage)
        }
        return false
    }

    fun sendScan(): Boolean {
        val massage = SocketMassage.UPLOAD_START
        return Network.sendMessage(massage)
    }

    fun sendDownLoad(filePath: String,name: String): Boolean {
        //由于只能携带一条信息，所以要将两个信息合并成一条，中间使用文件不能命名的字符隔离
        joinPaths(filePath, name).let { joinedPath ->
            val massage = SocketMassage.FILE_DOWNLOAD.apply {
                data = joinedPath
            }
            return Network.sendMessage(massage)
        }
    }


    // 拼接路径
    fun joinPaths(path1: String, path2: String): String {
        return "$path1\u0000$path2"
    }

    // 还原路径
    fun splitPaths(joined: String): Pair<String, String> {
        val parts = joined.split('\u0000', limit = 2)
        return Pair(parts[0], parts[1])
    }

}