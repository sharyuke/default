package com.sharyuke.empty.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

/**
 * 网络判断，非可用状态，根据自己的需求来使用
 * 这里演示了新旧网络判断的使用方法。
 */
val Context.hasNetwork: String
    @SuppressLint("MissingPermission")
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
                getNetworkCapabilities(activeNetwork).apply {
                    if (this == null) return "无网络"
                    if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return "移动网络"
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return "WIFI网络"
                    if (hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) return "蓝牙网络"
                    if (hasTransport(NetworkCapabilities.TRANSPORT_USB)) return "USB网络"
                    if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)) return "VPN网络"
                    if (hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return "以太网网络"
                    // ... 这里还有其他的
                }
                activeNetwork.apply {

                }
            }
        } else {
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo.apply {
                if (this == null || state == null) return "无网络"
//                if (isConnected) return "有可用网络"
                return when (state) {
                    NetworkInfo.State.CONNECTING -> "连接中"
                    NetworkInfo.State.CONNECTED -> "已链接"
                    NetworkInfo.State.SUSPENDED -> "挂起"
                    NetworkInfo.State.DISCONNECTING -> "断开链接中"
                    NetworkInfo.State.DISCONNECTED -> "已断开链接"
                    NetworkInfo.State.UNKNOWN -> "未知状态"
                    else -> "state为NULL"
                }
            }
        }
        return ""
    }
