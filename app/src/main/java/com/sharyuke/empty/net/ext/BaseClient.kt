package com.sharyuke.empty.net.ext

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.sharyuke.empty.BuildConfig
import com.sharyuke.empty.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
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

open class BaseClient(private val ctx: Context) {

    protected val ByteArray.reqBody get() = toRequestBody("application/octet-stream".toMediaTypeOrNull())
    protected val ByteArray.reqBodyMulti get() = MultipartBody.Part.createFormData("file", "file", toRequestBody("application/octet-stream".toMediaTypeOrNull()))
    protected val File.reqBody get() = MultipartBody.Part.createFormData("file", name, asRequestBody("image/*".toMediaTypeOrNull()))

    protected fun <T> genIf(cls: Class<T>) = retrofit.create(cls)

    protected fun <T> Flow<T>.net() = catch { it.printStackTrace().apply { throw it } }.flowOn(Dispatchers.IO)

    private val retrofit
        get() = Retrofit.Builder().baseUrl(URL)
            .client(okhttpLog)
            .addCallAdapterFactory(FlowCallAdapterFactory.createAsync())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()

    private val okhttpLog
        get() = okhttpBuilder
            .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("NetLog", "====> $message")
                }
            }).setLevel(if (BuildConfig.DEBUG) Level.BODY else Level.NONE)).build()

    private val okhttpBuilder
        get() = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
}

