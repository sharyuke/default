package com.sharyuke.empty

import android.app.Application
import android.util.DisplayMetrics
import com.sharyuke.empty.net.NetClient

/**
 * 采用静态方法生成该对象，保证在每个没有上下文对象的地方，均能访问设备屏幕信息，以便于能使用10.dp这种写法。
 * 考虑到，该对象的唯一性、不变性，因此采用全局静态变量方式。
 */
var dm: DisplayMetrics? = null

/**
 * App
 * Designed by sharyuke
 */
class App : Application() {

    /**
     * 网络请求客户端，因网络请求是最常见的工具，因此采用饿汉单例模式。
     * 考虑到网络请求有异步操作，因此需要搭配具有生命周期的对象来使用，因此不使用静态变量方式，采用上下文对象持有。
     * 上下文对象持有的好处是，每个上下文都具有特定的生命周期。
     * 在上下文对象的生命周期内，拥有该协程对象，就能保证网络客户端所有异步操作均在生命周期内完成。
     */
    lateinit var netClient: NetClient

    override fun onCreate() {
        super.onCreate()
        netClient = NetClient(this)
        dm = resources.displayMetrics
    }
}