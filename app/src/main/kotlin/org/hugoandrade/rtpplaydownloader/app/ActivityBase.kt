package org.hugoandrade.rtpplaydownloader.app

import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import org.hugoandrade.rtpplaydownloader.R
import dev.hugomfandrade.mediadownloader.core.utils.NetworkUtils
import org.hugoandrade.rtpplaydownloader.utils.AndroidNetworkUtils
import org.hugoandrade.rtpplaydownloader.utils.ViewUtils


abstract class ActivityBase : AppCompatActivity() {

    /**
     * Debugging tag used by the Android logger.
     */
    protected var TAG = javaClass.simpleName

    /**
     * Network UI/UX
     */
    private var mNetworkBroadcastReceiver: BroadcastReceiver? = null

    private var tvNoNetworkConnection: View? = null

    private val iNetworkListener = object : AndroidNetworkUtils.INetworkBroadcastReceiver {

        override fun setNetworkAvailable(isNetworkAvailable: Boolean) {

            onNetworkStateChanged(isNetworkAvailable)

            ViewUtils.setHeightDpAnim(applicationContext, checkNotNull(tvNoNetworkConnection), if (isNetworkAvailable) 0 else 20)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeNetworkFooter()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        initializeNetworkFooter()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)

        initializeNetworkFooter()
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)

        initializeNetworkFooter()
    }

    private fun initializeNetworkFooter() {

        if (mNetworkBroadcastReceiver == null) {
            mNetworkBroadcastReceiver = AndroidNetworkUtils.register(this, iNetworkListener)
        }

        tvNoNetworkConnection = findViewById(R.id.tv_no_network_connection)

        ViewUtils.setHeightDp(this, tvNoNetworkConnection, if (AndroidNetworkUtils.isNetworkAvailable(this)) 0 else 20)
    }

    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    override fun onDestroy() {
        super.onDestroy()

        if (mNetworkBroadcastReceiver != null) {
            AndroidNetworkUtils.unregister(this, checkNotNull(mNetworkBroadcastReceiver))
            mNetworkBroadcastReceiver = null
        }
    }

    protected fun onNetworkStateChanged(isAvailable: Boolean) {}
}