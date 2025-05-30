package com.dias.installwifi.utils

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class InfiniteCarouselHelper<T>(
    private val recyclerView: RecyclerView,
    private val originalData: List<T>,
    private val setAdapterData: (List<T>) -> Unit,
    private val scrollIntervalMillis: Long = 3000L
) {
    private val infiniteList = mutableListOf<T>()
    private val handler = Handler(Looper.getMainLooper())
    private var isAutoScrollEnabled = false

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val currentPosition = lm.findFirstVisibleItemPosition()
            recyclerView.smoothScrollToPosition(currentPosition + 1)
            handler.postDelayed(this, scrollIntervalMillis)
        }
    }

    init {
        setupInfiniteList()
        setupRecyclerView()
    }

    private fun setupInfiniteList() {
        if (originalData.size > 1) {
            infiniteList.clear()
            infiniteList.add(originalData.last())
            infiniteList.addAll(originalData)
            infiniteList.add(originalData.first())
        } else {
            infiniteList.clear()
            infiniteList.addAll(originalData)
        }
        setAdapterData(infiniteList)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(
            recyclerView.context, LinearLayoutManager.HORIZONTAL, false
        )
        PagerSnapHelper().attachToRecyclerView(recyclerView)

        recyclerView.scrollToPosition(1)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)
                val lm = rv.layoutManager as? LinearLayoutManager ?: return
                val position = lm.findFirstVisibleItemPosition()
                val lastIndex = infiniteList.lastIndex

                when (position) {
                    0 -> rv.scrollToPosition(lastIndex - 1)
                    lastIndex -> rv.scrollToPosition(1)
                }
            }
        })
    }

    fun startAutoScroll() {
        if (!isAutoScrollEnabled) {
            isAutoScrollEnabled = true
            handler.postDelayed(autoScrollRunnable, scrollIntervalMillis)
        }
    }

    fun stopAutoScroll() {
        isAutoScrollEnabled = false
        handler.removeCallbacks(autoScrollRunnable)
    }
}
