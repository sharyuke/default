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

open class BaseActivity : AppCompatActivity() {

    protected lateinit var app: App
    lateinit var netClient: NetClient
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