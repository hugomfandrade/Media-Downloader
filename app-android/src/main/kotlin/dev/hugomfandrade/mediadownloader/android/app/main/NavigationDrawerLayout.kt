package dev.hugomfandrade.mediadownloader.android.app.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import dev.hugomfandrade.mediadownloader.android.R

@Deprecated("to be replaced with compose equivalent")
class NavigationDrawerLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : DrawerLayout(context, attrs, defStyle) {

    var coordinatorLayout: CoordinatorLayout? = null
    var navigationDrawerContentCompose: ComposeView? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        val inflater =
            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.navigation_drawer_layout, this, true)

        coordinatorLayout = findViewById(R.id.coordinator_layout)
        navigationDrawerContentCompose = findViewById(R.id.navigation_drawer_compose)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (coordinatorLayout == null) return super.addView(child, index, params)
        coordinatorLayout?.addView(child, index, params)
    }

    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams, preventRequestLayout: Boolean): Boolean {
        return false
    }
}