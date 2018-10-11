package com.example.heran.findacat.Adapter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.heran.findacat.Manager.CatInfoManager.petlist
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cat_list_item.view.*



interface CatItemClickListener{
    fun catClick(view :View ,idx : Int)
}

class ItemViewHolder(itemview : View): RecyclerView.ViewHolder(itemview)
{

    val catName = itemview.tv_cat_name
    val catImg = itemview.img_cat_pic
}

class CatsAdapter(recyclerView: RecyclerView,internal var activity: Activity, internal var petlist: MutableList<Pet?>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    internal var loadmore: ILoadMore? = null
    internal var isLoading = false
    internal var visibleThreshold= 5
    internal var lastVisibleItem = 0
    internal var totalItemCount : Int = 0
    var catclickListener : CatItemClickListener? = null

    init {
        val linearlayoutManager= recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearlayoutManager.itemCount
                lastVisibleItem = linearlayoutManager.findLastVisibleItemPosition()

                if(!isLoading && totalItemCount < lastVisibleItem + visibleThreshold)
                {
                    if(loadmore!=null)
                        loadmore!!.onLoadMore()
                    isLoading = true
                }
            }

        })


    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder
    {
        val view = LayoutInflater.from(activity).inflate(R.layout.cat_list_item, p0, false)
        val mViewHolder = ItemViewHolder(view)
        view.setOnClickListener { v -> catclickListener!!.catClick(v, mViewHolder.adapterPosition) }
        return mViewHolder
    }

    override fun getItemCount(): Int
    {
        return petlist.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int)
    {

        if(p0 is ItemViewHolder)
        {
            val pet = petlist[p1]
            p0.catName?.text = pet?.name?.T.toString()

            Picasso.get().load("${pet?.media?.photos?.photo?.get(2)?.T}").into(p0.catImg)

        }
    }

    fun setLoaded()
    {
        isLoading = false
    }

    fun setLoadMore(iLoadMore: ILoadMore)
    {
        this.loadmore = iLoadMore
    }


}