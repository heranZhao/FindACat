package com.example.heran.findacat.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.example.heran.findacat.Adapter.CatItemClickListener
import com.example.heran.findacat.Adapter.CatsAdapter
import com.example.heran.findacat.Adapter.ILoadMore
import com.example.heran.findacat.Manager.CatInfoManager
import com.example.heran.findacat.Manager.LocationDetector
import com.example.heran.findacat.Manager.PresistenceManager
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.R
import kotlinx.android.synthetic.main.activity_list.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class ListActivity : AppCompatActivity(), ILoadMore, CatInfoManager.CatInfoFinishedListener, CatItemClickListener, LocationDetector.LocationListener {


    companion object {
        val KEY_PETIDX = "PetIdx"
        val KEY_REQUESTTYPE = "RequestType"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : CatsAdapter
    private lateinit var progressBarLayout : LinearLayout
    private lateinit var popwindowLayout : LinearLayout
    private lateinit var locationDetector: LocationDetector
    private var actionbar : android.support.v7.app.ActionBar? = null
    private var offset = 0
    private val returnSize = 20
    private var zipCode : String = ""
    private var requestType : Int = 0
    private var lastClickPetIdx : Int = -1
    private var waitforPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setSupportActionBar(catlistToolbar)
        actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.title = ""

        PresistenceManager.setContext(this)

        recyclerView = findViewById(R.id.rv_cat_list)
        progressBarLayout = findViewById(R.id.ll_progress_bar)
        popwindowLayout = findViewById(R.id.ll_pop_window_bg)
        popwindowLayout.isClickable = false
        popwindowLayout.isFocusable = false

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = CatsAdapter(recyclerView, this, CatInfoManager.petlist)
        adapter.setLoadMore(this)
        adapter.catclickListener = this
        recyclerView.adapter = adapter
        progressBarLayout.visibility = View.GONE
        CatInfoManager.catinfoListener = this

        locationDetector = LocationDetector(this)
        locationDetector.locationListener = this

        val bundle = intent.extras
        if(bundle!=null) {
            requestType = bundle.getInt(KEY_REQUESTTYPE, 0)
            if(requestType == 0) {
                progressBarLayout.visibility = View.VISIBLE
                locationDetector.detectLocation()
            }
            else
            {
                val title = resources.getString(R.string.text_listactivity_title2)
                actionbar?.title = title
                val list = PresistenceManager.getList()
                for(pet : Pet? in list)
                {
                    CatInfoManager.petlist.add(pet)
                }
                adapter.notifyDataSetChanged()
                adapter.setLoaded()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestType == 1 && lastClickPetIdx != -1) {
            val size = PresistenceManager.getList().size
            val size2 = CatInfoManager.petlist.size
            if (size < size2) {
                CatInfoManager.petlist.removeAt(lastClickPetIdx)
                adapter.notifyItemRemoved(lastClickPetIdx)
            }
            lastClickPetIdx = -1
        }
        if( requestType == 0 && waitforPermission)
        {
            val flag = locationDetector.checkPermission()
            if(flag)
            {
                progressBarLayout.visibility = View.VISIBLE
                locationDetector.detectLocation()
                waitforPermission = false
            }
            else
            {
                val msg = resources.getString(R.string.locationErrPermission)
                toast(msg)
                waitforPermission = false
            }
        }
    }

    private  fun initloadList()
    {
        val title = resources.getString(R.string.text_listactivity_title1,zipCode)
        actionbar?.title = title
        getPetList()
    }

    private fun getPetList()
    {
        CatInfoManager.getPetList(zipCode, "$offset", "$returnSize")
    }

    private fun popLocationPermissionWindow()
    {
        val msg = resources.getString(R.string.locationErrPermission)
        val btnY = resources.getString(R.string.btn_yes)
        val btnI = resources.getString(R.string.btn_input)
        val hint = resources.getString(R.string.locationInput)

        alert(msg) {
            positiveButton(btnY) { waitforPermission = true
                locationDetector.requestPermission(this@ListActivity) }
            negativeButton(btnI) { popInputWindow(hint)}
        }.show()
    }

    @SuppressLint("InflateParams")
    private fun popInputWindow(hintText: String)
    {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val promptsView = inflater.inflate(R.layout.pop_location_setting,null)

        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setView(promptsView)

        val editTextPopup = promptsView.findViewById<EditText>(R.id.et_locatio_pop)
        editTextPopup.hint = hintText
        val btnY = resources.getString(R.string.btn_yes)
        val btnC = resources.getString(R.string.btn_cancle)
        alertDialogBuilder.setNegativeButton(btnC) { dialog, _ -> dialog.cancel() }
        alertDialogBuilder.setPositiveButton(btnY) { _, _ ->
                    val newzip = editTextPopup.text.toString()
                    if(newzip != zipCode && newzip != "") {
                        zipCode = newzip
                        offset = 0
                        val size = CatInfoManager.petlist.size
                        CatInfoManager.petlist.clear()
                        adapter.notifyItemRangeRemoved(0,size)
                        progressBarLayout.visibility = View.VISIBLE
                        CatInfoManager.getPetList(zipCode, "$offset", "$returnSize")

                        val actionbar = supportActionBar
                        val title = resources.getString(R.string.text_listactivity_title1,zipCode)
                        actionbar?.title = title
                    }
                    else if(newzip == "")
                    {
                        val msg = resources.getString(R.string.locationInput)
                        toast(msg)
                    }
                }


        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun locationFound(location: String?) {
        progressBarLayout.visibility = View.GONE
        waitforPermission = false
        if( location != null)
        {
            zipCode = location
            initloadList()
        }
        else
        {
            val msg = resources.getString(R.string.locationNotFound)
            val btnY = resources.getString(R.string.btn_yes)
            alert(msg) {
                positiveButton(btnY) { }
            }.show()
        }
    }

    override fun locationNotFound(reason: LocationDetector.FailureReason) {
        progressBarLayout.visibility = View.GONE
        if(reason == LocationDetector.FailureReason.TIMEOUT)
        {
            waitforPermission = false
            val msg = resources.getString(R.string.locationErrTimeout)
            toast(msg)
        }
        else if(reason == LocationDetector.FailureReason.NO_PERMISSION)
        {
            popLocationPermissionWindow()
        }
    }

    override fun onLoadMore() {

        if(CatInfoManager.petlist.size >=returnSize)
        {
            getPetList()
        }
    }

    override fun loadListSuccess() {

        offset += returnSize
        adapter.notifyDataSetChanged()
        adapter.setLoaded()
        progressBarLayout.visibility = View.GONE

    }

    override fun loadListFailure(msg : String) {
        if(msg == "")
        {
            val msg2 = resources.getString(R.string.Err_Network)
            msg.plus(msg2)
        }
        toast(msg)
        progressBarLayout.visibility = View.GONE
    }


    override fun catClick(view : View, idx : Int)
    {
        lastClickPetIdx = idx
        val intent = Intent(this@ListActivity, CatDetailActivity::class.java)
        intent.putExtra(KEY_PETIDX, idx)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(requestType == 0) {
            menuInflater.inflate(R.menu.menu_catlist, menu)
        }
        return true
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.btn_zip -> {
            var t : String = ""
            if(zipCode == "")
            {
                t = resources.getString(R.string.locationInput)
            }
            else
            {
                t = resources.getString(R.string.text_listactivity_popwindow_hint, zipCode)

            }
            popInputWindow(t)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        CatInfoManager.petlist.clear()
        val intent = Intent(this@ListActivity, MenuActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

}

