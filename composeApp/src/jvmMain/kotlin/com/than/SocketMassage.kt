package com.than

data class SocketMassage(val dataName:String, var data: String="") {
    companion object {
        val HELLO: SocketMassage = SocketMassage("Hello")

        val CONFIG: SocketMassage = SocketMassage("Config")

        val CONFIG_SAVE_ERROR: SocketMassage = SocketMassage("Config Error")

        val UPLOAD_START: SocketMassage = SocketMassage("Upload Start")

        val UPLOAD_END: SocketMassage = SocketMassage("Upload End")

        val FILE_DOWNLOAD: SocketMassage = SocketMassage("Download")
    }
}
