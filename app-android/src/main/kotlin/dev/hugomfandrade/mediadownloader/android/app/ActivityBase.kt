package dev.hugomfandrade.mediadownloader.android.app

import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import dev.hugomfandrade.mediadownloader.android.utils.AndroidNetworkUtils
import dev.hugomfandrade.mediadownloader.android.utils.ViewUtils
import dev.hugomfandrade.mediadownloader.android.R


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

            ViewUtils.Companion.setHeightDpAnim(applicationContext, checkNotNull(tvNoNetworkConnection), if (isNetworkAvailable) 0 else 20)
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
            mNetworkBroadcastReceiver = AndroidNetworkUtils.Companion.register(this, iNetworkListener)
        }

        tvNoNetworkConnection = findViewById(R.id.tv_no_network_connection)

        ViewUtils.Companion.setHeightDp(this, tvNoNetworkConnection, if (AndroidNetworkUtils.Companion.isNetworkAvailable(this)) 0 else 20)
    }

    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    override fun onDestroy() {
        super.onDestroy()

        if (mNetworkBroadcastReceiver != null) {
            AndroidNetworkUtils.Companion.unregister(this, checkNotNull(mNetworkBroadcastReceiver))
            mNetworkBroadcastReceiver = null
        }
    }

    protected fun onNetworkStateChanged(isAvailable: Boolean) {}
}