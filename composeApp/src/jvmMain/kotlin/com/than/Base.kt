package com.than

object Base{
    var config: Config? = null

    fun setConfig(): Boolean {
        return MassageHandle.sendConfig()
    }
}