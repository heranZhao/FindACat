package com.example.heran.findacat.Adapter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cat_list_item.view.*
import kotlinx.android.synthetic.main.item_loading.view.*

internal class LoadingViewHolder(itemview : View): RecyclerView.ViewHolder(itemview)
{
    var progressBar = itemview.progress_bar

}

internal class ItemViewHolder(itemview : View): RecyclerView.ViewHolder(itemview)
{
    val catName = itemview.tv_cat_name
    val catSex = itemview.tv_cat_sex
    val catAge = itemview.tv_cat_age
    val catImg = itemview.img_cat_pic
}

class CatsAdapter(recyclerView: RecyclerView,internal var activity: Activity, internal var petlist: MutableList<Pet?>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{


    val VIEW_ITEMTYPE = 0
    val VIEW_LOADNGTYPE = 1

    internal var loadmore: ILoadMore? = null
    internal var isLoading = false
    internal var visibleThreshold= 5
    internal var lastVisibleItem = 0
    internal var totalItemCount : Int = 0

    init {
        var linearlayoutManager= recyclerView.layoutManager as LinearLayoutManager
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


    override fun getItemViewType(position: Int): Int {
        return if(petlist[position] == null) VIEW_LOADNGTYPE else VIEW_ITEMTYPE
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder
    {
        if (p1 == VIEW_ITEMTYPE)
        {
            var view = LayoutInflater.from(activity).inflate(R.layout.cat_list_item, p0, false)
            return ItemViewHolder(view)
        }
        else
        {
            var view = LayoutInflater.from(activity).inflate(R.layout.item_loading, p0, false)
            return LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int
    {
        return petlist.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int)
    {

        if(p0 is ItemViewHolder)
        {
            var pet = petlist[p1]
            p0?.catName?.text = pet?.name?.T.toString()
            p0?.catSex?.text = pet?.sex?.T.toString()
            p0?.catAge?.text = pet?.age?.T.toString()

            Picasso.get().load("${pet?.media?.photos?.photo?.get(0)?.T}").into(p0?.catImg);
        }
        else if(p0 is LoadingViewHolder)
        {
            p0.progressBar.isIndeterminate = true
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