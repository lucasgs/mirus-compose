package com.dendron.mirus.data.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.dendron.mirus.domain.NetworkChecker

class AndroidNetworkChecker(private val context: Context) : NetworkChecker {

    override fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return (
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }
}
