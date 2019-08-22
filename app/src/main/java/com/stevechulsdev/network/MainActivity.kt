package com.stevechulsdev.network

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Toast.makeText(this, ScNetwork.isConnectNetwork(this).toString(), Toast.LENGTH_SHORT).show()

        if(Build.VERSION.SDK_INT >= 21) {
            ScNetwork.checkNetworkRealTime(this, object : ScNetwork.NetworkInterface {
                override fun onNetworkAvailable() {
                    Toast.makeText(this@MainActivity, "onNetworkAvailable", Toast.LENGTH_SHORT).show()
                    Log.e("steve", "onNetworkAvailable")
                }

                override fun onNetworkLost() {
                    Toast.makeText(this@MainActivity, "onNetworkLost", Toast.LENGTH_SHORT).show()
                    Log.e("steve", "onNetworkLost")
                }
            })
        }
    }

    override fun onDestroy() {
        if(Build.VERSION.SDK_INT >= 21) ScNetwork.removeCheckNetworkRealTime()
        super.onDestroy()
    }
}
