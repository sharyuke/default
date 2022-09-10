package com.sharyuke.empty.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.sharyuke.empty.App
import com.sharyuke.empty.net.NetClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 所有Activity基类
 * 每个Activity均具有特定的生命周期
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * 每个Activity具有自己特定的生命周期，同时持有整个App的上下文对象，用于获取App的相关信息
     * 例如，网络客户端，设备信息等。
     * Activity需要在自己的生命周期完成属于自己的事情。在自己生命周期之外，不能持有任何对象以及线程。
     */
    protected lateinit var app: App

    /**
     * 在自己生命周期内，获取整个App的网络客户端对象
     */
    lateinit var netClient: NetClient

    /**
     * 为了方便调用this，避免在其他this范围内，调用this@xxxx方式。
     * 注意，该对象在onCreate之后才初始化。注意对象的生命周期。
     * 一般说来，任何业务流程，均不应该在onCreate之前进行实现，因此使用me获取当前对象是安全的，也是合理的。
     */
    protected lateinit var me: FragmentActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        me = this
        app = application as App
        netClient = app.netClient
    }

    /**
     * 1、主线程回调
     * 2、绑定了当前Activity的生命周期。
     */
    protected fun <T> Flow<T>.sub(block: T.() -> Unit = {}) = catch { }.onEach { block(it) }.launchIn(lifecycleScope)
}