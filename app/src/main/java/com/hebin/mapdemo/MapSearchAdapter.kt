package com.hebin.mapdemo

import android.content.Context
import android.view.View
import android.widget.TextView

import com.amap.api.services.help.Tip
import com.hebin.superrecyclerview.adapter.BaseViewHolder
import com.hebin.superrecyclerview.adapter.SuperBaseAdapter

/**
 * Author Hebin
 *<p>
 * created at 2017/8/12
 *<p>
 * blog: http://blog.csdn.net/hebin320320
 *<p>
 * GitHub: https://github.com/Hebin320
 *<p>
 * 说明：
 */
class MapSearchAdapter(context: Context, private var list: List<Tip>) : SuperBaseAdapter<Tip>(context, list) {

    fun refresh(list: List<Tip>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: Tip, position: Int) {
        if (item.address.isEmpty()) {
            holder.getView<TextView>(R.id.tvTitle).visibility = View.GONE
        } else {
            holder.getView<TextView>(R.id.tvTitle).visibility = View.VISIBLE
        }
        holder.setText(R.id.tvTitle, item.address).setOnClickListener(R.id.tvTitle, OnItemChildClickListener())
    }

    override fun getItemViewLayoutId(position: Int, item: Tip): Int {
        return R.layout.adapter_map_search
    }
}
