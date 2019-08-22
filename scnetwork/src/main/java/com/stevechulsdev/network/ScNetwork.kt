package com.stevechulsdev.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object ScNetwork {

    /**
     * 1회성 네트워크 연결 유무 확인
     * os api level 23 미만일 경우, 코드 분기 처리
     * @param context Context
     * @return true 연결 됨, false 연결 안됨
     */
    fun isConnectNetwork(context: Context): Boolean {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT < 23) {
            cm.activeNetworkInfo?.let { networkInfo ->
                networkInfo.apply {
                    return (isConnected && (type == ConnectivityManager.TYPE_MOBILE) || (type == ConnectivityManager.TYPE_WIFI))
                }
            }
        }
        else {
            cm.activeNetwork?.let { network ->
                network.apply {
                    cm.getNetworkCapabilities(this)?.let {
                        return (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) || (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    }
                }
            }
        }
        return false
    }
}