package com.sharyuke.empty.net

import com.sharyuke.empty.net.ext.ResBaseModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * 接口定义
 */
interface IfNet {

    @GET("/foo/bar")
    fun bar(): Flow<ResBaseModel>

    @GET
    fun test(@Url url: String): Flow<Any>
}