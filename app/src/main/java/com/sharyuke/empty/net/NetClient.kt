package com.sharyuke.empty.net

import android.content.Context
import com.sharyuke.empty.net.ext.BaseClient

/**
 * 此类用于调用接口时候，对数据的封装。
 * 便于同于管理接口。
 */
class NetClient(ctx: Context) : BaseClient(ctx) {

    private val service = genIf(IfNet::class.java)

    fun bar() = service.bar().net()

    fun test() = service.test("https://www.baidu.com").net()
}