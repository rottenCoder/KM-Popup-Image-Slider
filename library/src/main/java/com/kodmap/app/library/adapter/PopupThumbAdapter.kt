package com.kodmap.app.library.adapter

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kodmap.app.library.R
import com.kodmap.app.library.listener.AdapterClickListener
import com.kodmap.app.library.loader.core.DisplayImageOptions
import com.kodmap.app.library.loader.core.ImageLoader
import com.kodmap.app.library.loader.core.assist.FailReason
import com.kodmap.app.library.loader.core.listener.SimpleImageLoadingListener
import com.kodmap.app.library.model.BaseItem
import com.kodmap.app.library.ui.KmImageView
import com.kodmap.app.library.ui.KmRelativeLayout
import java.util.*


class PopupThumbAdapter(private val listener: AdapterClickListener) : ListAdapter<BaseItem, PopupThumbAdapter.ViewHolder>(itemCallback) {

    private val itemList = ArrayList<BaseItem>()
    lateinit var mLoadingView: View
    var oldSelectedPosition = 0
    val options: DisplayImageOptions = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()


    fun setList(itemList: List<BaseItem>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.km_item_thumb, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemList[position].isSelected) {
            holder.gradient_view.visibility = View.VISIBLE
        } else {
            holder.gradient_view.visibility = View.GONE
        }

        ImageLoader.getInstance()
                .displayImage(
                        if (itemList[position].imageUrl == null) "drawable://${itemList[position].drawableId}" else itemList[position].imageUrl,
                        holder.iv_thumb,
                        options,
                        object : SimpleImageLoadingListener() {
                            override fun onLoadingStarted(imageUri: String, view: View) {

                            }

                            override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
                                holder.iv_thumb.disableLoading()
                            }

                            override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
                                holder.iv_thumb.disableLoading()
                            }
                        })
    }


    inner class ViewHolder(itemView: View, listener: AdapterClickListener) : RecyclerView.ViewHolder(itemView) {
        var iv_thumb: KmImageView = itemView.findViewById(R.id.km_iv_thumb)
        var gradient_view: View = itemView.findViewById(R.id.km_gradient_view)

        init {

            if (::mLoadingView.isInitialized) {
                (itemView as KmRelativeLayout).addLoadingLayout(mLoadingView)
                iv_thumb.setLoadingLayout(mLoadingView)
            }

            iv_thumb.setOnClickListener {
                listener.onClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun changeSelectedItem(position: Int) {
        if (position != -1) {
            itemList[oldSelectedPosition].isSelected = false
            itemList[position].isSelected = true
            oldSelectedPosition = position
            notifyDataSetChanged()
        }
    }

    fun setLoadingView(mLoadingView: View?) {
        if (mLoadingView != null) {
            this.mLoadingView = mLoadingView
        }
    }

    companion object {
        var itemCallback: DiffUtil.ItemCallback<BaseItem> = object : DiffUtil.ItemCallback<BaseItem>() {
            override fun areItemsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean {
                return newItem.imageUrl == oldItem.imageUrl
            }

            override fun areContentsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean {
                return newItem.isSelected == oldItem.isSelected
            }

            override fun getChangePayload(oldItem: BaseItem, newItem: BaseItem): Any? {
                val bundle = Bundle()
                if (oldItem !== newItem)
                    bundle.putParcelable("newItem", newItem)
                return bundle
            }
        }
    }
}
