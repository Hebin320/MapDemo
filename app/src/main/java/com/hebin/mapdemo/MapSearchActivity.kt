package com.hebin.mapdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.hebin.superrecyclerview.adapter.SuperBaseAdapter
import com.hebin.superrecyclerview.recycleview.ProgressStyle
import kotlinx.android.synthetic.main.activity_map_search.*
import java.util.*

class MapSearchActivity : AppCompatActivity(), SuperBaseAdapter.OnRecyclerViewItemChildClickListener, Inputtips.InputtipsListener {


    private var adapter: MapSearchAdapter? = null
    private val mList = ArrayList<Tip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_search)
        init()
    }

    private fun init() {
        // 设置Recyclerview
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvSearch.layoutManager = layoutManager
        rvSearch.itemAnimator = DefaultItemAnimator()
        rvSearch.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader)
        rvSearch.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader)
        rvSearch.setArrowImageView(R.drawable.ic_downgrey)
        adapter = MapSearchAdapter(this, mList)
        rvSearch.adapter = adapter
        rvSearch.setLoadMoreEnabled(false)
        rvSearch.setRefreshEnabled(false)
        adapter?.setOnItemChildClickListener(this)
        //监听输入框文本变化
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(string: Editable) {
                search(string.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    fun search(string: String) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        val inputquery = InputtipsQuery(string, "")
        inputquery.cityLimit = true//限制在当前城市
        val inputTips = Inputtips(this@MapSearchActivity, inputquery)
        inputTips.setInputtipsListener(this)
        inputTips.requestInputtipsAsyn()
    }

    override fun onItemChildClick(adapter: SuperBaseAdapter<*>?, view: View?, position: Int) {
        setResult(Activity.RESULT_OK, Intent().putExtra("title", mList[position].address))
        finish()
    }

    override fun onGetInputtips(list: MutableList<Tip>, i: Int) {
        if (i == 1000) {
            list.indices
                    .filter { list[it].address.isEmpty() }
                    .forEach { list.removeAt(it) }
            mList.clear()
            mList.addAll(list)
            adapter?.refresh(mList)
        }
    }
}
