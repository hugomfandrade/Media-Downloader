package dev.hugomfandrade.mediadownloader.android.app.main

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import dev.hugomfandrade.mediadownloader.android.R
import dev.hugomfandrade.mediadownloader.ui.shared.DrawerItem
import dev.hugomfandrade.mediadownloader.ui.shared.Header
import dev.hugomfandrade.mediadownloader.ui.shared.NavigationDrawerInterface
import dev.hugomfandrade.mediadownloader.ui.shared.OnDrawerClickListener
import dev.hugomfandrade.mediadownloader.ui.shared.OptionItem
import dev.hugomfandrade.mediadownloader.ui.shared.QuickAccessItem
import java.util.*

class NavigationDrawerAdapter :
    NavigationDrawerInterface,
    RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder>() {

    companion object {
        private val TAG = NavigationDrawerAdapter::class.java.simpleName
    }

    private val mItemList: MutableList<DrawerItem> = ArrayList()
    private var mListener: OnDrawerClickListener? = null

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vi = LayoutInflater.from(parent.context)
        return ViewHolder(vi.inflate(R.layout.list_item_drawer, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        synchronized(mItemList) {
            val item = mItemList[holder.bindingAdapterPosition]
            val isHeader = item is Header
            holder.drawerHeader.visibility = if (isHeader) View.VISIBLE else View.GONE
            holder.drawerLayout.visibility = if (isHeader) View.GONE else View.VISIBLE
            when (item) {
                is Header -> {
                    holder.tvHeader.text = item.headerTitle
                }
                is QuickAccessItem -> {
                    holder.tvTitle.text = item.title
                    holder.ivIcon.setImageResource(item.resourceID)
                }
                is OptionItem -> {
                    holder.tvTitle.text = item.title
                    holder.ivIcon.setImageResource(item.resourceID)
                }
                else -> {
                    holder.drawerLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun setOnItemClickListener(listener: OnDrawerClickListener) {
        mListener = listener
    }

    override fun addOptionItem(item: OptionItem) {
        synchronized(mItemList) { mItemList.add(item) }
    }

    override fun addItem(item: QuickAccessItem) {
        synchronized(mItemList) { mItemList.add(item) }
    }

    override fun addHeader(header: String) {
        synchronized(mItemList) { mItemList.add(Header(header)) }
    }

    fun getItemAt(position: Int): DrawerItem? {
        synchronized(mItemList) {
            return mItemList[position]
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var drawerHeader: View = view.findViewById(R.id.drawer_header)
        var tvHeader: TextView = view.findViewById(R.id.tv_drawer_header)
        var drawerLayout: View = view.findViewById(R.id.drawer_layout)
        var ivIcon: ImageView = view.findViewById(R.id.iv_drawer_icon)
        var tvTitle: TextView = view.findViewById(R.id.tv_drawer_title)

        override fun onClick(v: View) {
            val drawerItem = getItemAt(bindingAdapterPosition)
            if (drawerItem != null) {
                mListener?.onItemClicked(drawerItem)
            }
        }

        init {
            drawerLayout.setOnClickListener(this)
            ivIcon = view.findViewById(R.id.iv_drawer_icon)
            tvTitle = view.findViewById(R.id.tv_drawer_title)
        }
    }
}