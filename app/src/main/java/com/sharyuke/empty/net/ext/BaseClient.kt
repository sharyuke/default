package com.sharyuke.empty.net.ext

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.sharyuke.empty.BuildConfig
import com.sharyuke.empty.net.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 网络客户端基类
 * 网络客户端基础配置，请求数据处理、网络日志、请求拦击、请求适配器、模型转换等。
 *
 * Designed by sharyuke
 */
open class BaseClient(private val ctx: Context) : Interceptor {

    protected val ByteArray.reqBody get() = toRequestBody("application/octet-stream".toMediaTypeOrNull())
    protected val ByteArray.reqBodyMulti get() = MultipartBody.Part.createFormData("file", "file", toRequestBody("application/octet-stream".toMediaTypeOrNull()))
    protected val File.reqBody get() = MultipartBody.Part.createFormData("file", name, asRequestBody("image/*".toMediaTypeOrNull()))

    protected fun <T> genIf(cls: Class<T>) = retrofit.create(cls)

    /**
     * 每个接口之后调用次方法
     * 1、打印异常
     * 2、IO线程进行网络请求
     * 3、便于进行统一接口拦截
     */
    protected fun <T> Flow<T>.net() = catch { it.printStackTrace().apply { throw it } }.flowOn(Dispatchers.IO)

    private val retrofit
        get() = Retrofit.Builder().baseUrl(URL)
            .client(okhttpBuilder.config(okhttpLog, this).build())
            .addCallAdapterFactory(FlowCallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()

    private val okhttpLog
        get() = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("NetLog", "====> $message")
            }
        }).setLevel(if (BuildConfig.DEBUG && NET_CONFIG_OKHTTP_LOG) Level.BODY else Level.NONE)

    private val okhttpBuilder
        get() = OkHttpClient.Builder()
            .connectTimeout(NET_CONFIG_OKHTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(NET_CONFIG_OKHTTP_WRITE_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NET_CONFIG_OKHTTP_READ_TIME_OUT, TimeUnit.SECONDS)

    override fun intercept(chain: Interceptor.Chain) = chain.proceed(chain.request())
}

private fun OkHttpClient.Builder.config(vararg interceptor: Interceptor) = apply { interceptor.forEach { addInterceptor(it) } }
