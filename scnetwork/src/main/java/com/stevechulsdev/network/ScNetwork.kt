package com.stevechulsdev.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

object ScNetwork {
    private val TAG: String = javaClass.simpleName

    private var mConnectivityManager: ConnectivityManager? = null
    private var mNetworkCallback: ConnectivityManager.NetworkCallback? = null

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

    /**
     * 앱 실행 중에 인터넷 연결 여부를 지속적으로 확인 할 수 있습니다.
     * @param context Context
     * @param networkInterface 연결 유무에 따른 Interface
     */
    @RequiresApi(21)
    fun checkNetworkRealTime(context: Context, networkInterface: NetworkInterface) {
        mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder = NetworkRequest.Builder()

        mNetworkCallback = object  : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.i(TAG, "Network onAvailable")
                networkInterface.onNetworkAvailable()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.i(TAG, "Network onLost")
                networkInterface.onNetworkLost()
            }
        }

        mConnectivityManager?.let { connectivityManager ->
            mNetworkCallback?.let { networkCallback ->
                connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
            } ?: run {
                Log.e(TAG, "ConnectivityManager.NetworkCallback is null")
            }
        } ?: run {
            Log.e(TAG, "ConnectivityManager is null")
        }
    }

    @RequiresApi(21)
    fun removeCheckNetworkRealTime() {
        mConnectivityManager?.let { connectivityManager ->
            mNetworkCallback?.let { networkCallback ->
                connectivityManager.unregisterNetworkCallback(networkCallback)
            } ?: run {
                Log.e(TAG, "ConnectivityManager.NetworkCallback is null")
            }
        } ?: run {
            Log.e(TAG, "ConnectivityManager is null")
        }
    }

    interface NetworkInterface {
        fun onNetworkAvailable()
        fun onNetworkLost()
    }
}