package com.than

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ConnectException
import java.net.Socket
import java.util.concurrent.TimeUnit


object Network {
    private lateinit var socket: Socket
    private const val FILE_URL: String = "http://127.0.0.1:8080/"
    private val gson= Gson()
    private val CLIENT: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun getFileVersions(path: String): ArrayList<String> {
        val requestBody = FormBody.Builder().add("path",path).build()
        val request = Request.Builder()
            .url(FILE_URL + "get_file_versions")
            .post(requestBody)
            .header("verify", Base.config?.verify?:"")
            .build()

        CLIENT.newCall(request).execute().use { response ->
            if (!response.isSuccessful){
                if (response.code==502){
                    showErrorDialog("密码错误，请确认服务密码")
                    return ArrayList()
                }else{
                    throw Exception("Unexpected code $response")
                }
            }
            val body = response.body.string()
            return gson.fromJson(body, object:TypeToken<ArrayList<String>>(){}.type)
        }
    }

    fun connectWithServer(): Boolean {
        try {
            socket= Socket("127.0.0.1", 12345)
            return true
        }catch (e: ConnectException){
            // TODO: 打印错误日志
            return false
        }
    }

    // TODO: 完善异常处理
    fun acceptMessage(): SocketMassage{
        val br=socket.inputStream.bufferedReader()
        val text = br.readLine()
        val massage=gson.fromJson(text, SocketMassage::class.java)
        return massage
    }

    fun sendMessage(socketMassage: SocketMassage): Boolean {
        val bw= socket.outputStream.bufferedWriter()
        try {
            val data= gson.toJson(socketMassage)
            bw.write(data)
            bw.newLine()
            bw.flush()
            return true
        }catch (e: Exception) {
            // TODO: 写日志
            return false
        }
    }

}