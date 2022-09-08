package com.sharyuke.empty.net

import android.content.Context
import com.sharyuke.empty.net.ext.BaseClient

class NetClient(ctx: Context) : BaseClient(ctx) {

    private val service = genIf(IfNet::class.java)

    fun bar() = service.bar().net()

    fun test() = service.test("https://www.baidu.com").net()
}