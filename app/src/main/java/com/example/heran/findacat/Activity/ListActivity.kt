package com.example.heran.findacat.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.example.heran.findacat.Adapter.CatItemClickListener
import com.example.heran.findacat.Adapter.CatsAdapter
import com.example.heran.findacat.Adapter.ILoadMore
import com.example.heran.findacat.Manager.CatInfoManager
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.R
import android.widget.Toast
import org.jetbrains.anko.toast


class ListActivity : AppCompatActivity(), ILoadMore, CatInfoManager.CatInfoFinishedListener, CatItemClickListener {

    private val KEY_PETIDX = "pet_idx"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : CatsAdapter
    private lateinit var progressBarLayout : LinearLayout


    private var offset = 0
    private val returnSize = 20
    private var zipCode : String = "22202"
    private var requestType : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recyclerView = findViewById<RecyclerView>(R.id.rv_cat_list)
        progressBarLayout = findViewById<LinearLayout>(R.id.ll_progress_bar)


        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = CatsAdapter(recyclerView, this, CatInfoManager.petlist)
        adapter.setLoadMore(this)


        adapter.catclickListener = this
        recyclerView.adapter = adapter

        progressBarLayout.visibility = View.GONE

        CatInfoManager.catinfoListener = this

        getPetList()

    }


    fun getPetList()
    {
        progressBarLayout.visibility = View.VISIBLE
        CatInfoManager.getPetList(zipCode, "22202", "${offset}", "$returnSize")
    }


    override fun onLoadMore() {

        if(CatInfoManager.petlist.size >=returnSize)
            getPetList()

    }

    override fun loadListSuccess() {

        offset += returnSize
        adapter.notifyDataSetChanged()
        adapter.setLoaded()
        progressBarLayout.visibility = View.GONE

    }

    override fun loadListFailure() {
    }

    override fun loadSingleSuccess() {
        adapter.notifyItemInserted(CatInfoManager.petlist.size-1)
    }

    override fun loadSingleFailure() {
    }


    override fun catClick(view : View, idx : Int)
    {
        val intent = Intent(this@ListActivity, CatDetailActivity::class.java)
        intent.putExtra("PetIdx", idx)
        startActivity(intent)
    }


}

