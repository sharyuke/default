package com.sharyuke.empty.net

import android.content.Context
import android.util.Log
import com.sharyuke.empty.net.ext.FlowCallAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private val ByteArray.reqBody get() = toRequestBody("application/octet-stream".toMediaTypeOrNull())
private val ByteArray.reqBodyMulti get() = MultipartBody.Part.createFormData("file", "file", toRequestBody("application/octet-stream".toMediaTypeOrNull()))

private val File.reqBody get() = MultipartBody.Part.createFormData("file", name, asRequestBody("image/*".toMediaTypeOrNull()))

// TODO: URL ==> Config this.
const val URL = ""

fun retrofit(ctx: Context): Retrofit {
    return Retrofit.Builder().baseUrl(URL)
        .client(okhttpClient(ctx))
        .addCallAdapterFactory(FlowCallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun okhttpClient(ctx: Context): OkHttpClient {
    val builder = OkHttpClient.Builder()
//        .dns(object : Dns {
//            override fun lookup(hostname: String): List<InetAddress> {
//                return if (hostname == url) InetAddress.getAllByName(ip).toList() else Dns.SYSTEM.lookup(hostname)
//            }
//        })
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("NetLog", "====> $message")
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY))
    return builder.build()
}
