package com.example.heran.findacat.Activity

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.widget.*
import com.example.heran.findacat.Adapter.CatItemClickListener
import com.example.heran.findacat.Adapter.CatsAdapter
import com.example.heran.findacat.Adapter.ILoadMore
import com.example.heran.findacat.Manager.CatInfoManager
import com.example.heran.findacat.Manager.PresistenceManager
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.R
import kotlinx.android.synthetic.main.activity_list.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class ListActivity : AppCompatActivity(), ILoadMore, CatInfoManager.CatInfoFinishedListener, CatItemClickListener {

    private val KEY_PETIDX = "pet_idx"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : CatsAdapter
    private lateinit var progressBarLayout : LinearLayout
    private lateinit var popwindowLayout : LinearLayout

    private var offset = 0
    private val returnSize = 20
    private var zipCode : String = "22202"
    private var requestType : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setSupportActionBar(catlistToolbar)
        val actionbar = supportActionBar

        actionbar?.setDisplayHomeAsUpEnabled(true)

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


        val bundle = intent.extras
        if(bundle!=null) {
            requestType = bundle.getInt("RequestType", 0)
            if(requestType == 0)
            {
                actionbar?.title = "Cat Near $zipCode"
                progressBarLayout.visibility = View.VISIBLE
                getPetList()
            }
            else
            {
                actionbar?.title = "Favorite List"
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


    private fun getPetList()
    {
        CatInfoManager.getPetList(zipCode, "${offset}", "$returnSize")
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catlist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.btn_zip -> {

            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.pop_location_setting,null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                    view, // Custom view to show in popup window
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                    LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )
            popupWindow.elevation = 10.0F

            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.BOTTOM
            popupWindow.exitTransition = slideOut

            val editTextPopup = view.findViewById<EditText>(R.id.et_locatio_pop)
            val buttonPopup = view.findViewById<Button>(R.id.btn_location_ok)


            editTextPopup.hint = "Your current location is $zipCode"

            editTextPopup.requestFocus();

            val imm = this.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editTextPopup, 0);

            popwindowLayout.isClickable = true
            popwindowLayout.isFocusable = true

            buttonPopup.setOnClickListener{
                // Dismiss the popup window
                popupWindow.dismiss()
                popwindowLayout.isClickable = false
                popwindowLayout.isFocusable = false
            }

            // Set a dismiss listener for popup window
            popupWindow.setOnDismissListener {
                //Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
                var newzip = editTextPopup.text.toString();
                newzip = "98101"
                if(newzip != zipCode)
                {
                    zipCode = newzip
                    offset = 0
                    val size = CatInfoManager.petlist.size
                    CatInfoManager.petlist.clear()
                    adapter.notifyItemRangeRemoved(0,size)
                    progressBarLayout.visibility = View.VISIBLE
                    CatInfoManager.getPetList(zipCode, "${offset}", "$returnSize")
                }
            }
            TransitionManager.beginDelayedTransition(popwindowLayout)
            popupWindow.showAtLocation(
                    popwindowLayout, // Location to display popup window
                    Gravity.CENTER, // Exact position of layout to display popup
                    0, // X offset
                    0 // Y offset
            )
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        //onBackPressed()
        CatInfoManager.petlist.clear()
        val intent = Intent(this@ListActivity, MenuActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

}

