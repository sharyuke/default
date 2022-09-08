package com.sharyuke.empty

import android.app.Application
import android.util.DisplayMetrics
import com.sharyuke.empty.net.NetClient

var dm: DisplayMetrics? = null

class App : Application() {
    lateinit var netClient: NetClient
    override fun onCreate() {
        super.onCreate()
        netClient = NetClient(this)
        dm = resources.displayMetrics
    }
}