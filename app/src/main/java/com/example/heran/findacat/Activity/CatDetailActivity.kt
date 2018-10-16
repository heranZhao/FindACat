package com.example.heran.findacat.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.heran.findacat.Manager.CatInfoManager
import com.example.heran.findacat.Manager.PresistenceManager
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_catdetail.*


class CatDetailActivity: AppCompatActivity()
{

    private lateinit var tvCatName :TextView
    private lateinit var tvCatGender:TextView
    private lateinit var tvCatBreed: TextView
    private lateinit var tvCatZip : TextView
    private lateinit var tvCatDes : TextView
    private lateinit var actionBarMenu : Menu
    private lateinit var imgCatPic : ImageView

    private var pet : Pet? = null
    private var isFavorite : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catdetail)

        setSupportActionBar(catdetailToolbar)
        val actionbar = supportActionBar
        actionbar?.title = ""
        actionbar?.setDisplayHomeAsUpEnabled(true)


        tvCatName = findViewById(R.id.tv_cat_name_detail)
        tvCatGender = findViewById(R.id.tv_cat_gender_detail)
        tvCatBreed = findViewById(R.id.tv_cat_breed_detail)
        tvCatZip = findViewById(R.id.tv_cat_zip_detail)
        tvCatDes = findViewById(R.id.tv_cat_des_detail)
        imgCatPic = findViewById(R.id.img_cat_detail)

        val bundle = intent.extras
        if(bundle!=null)
        {
            val idx = bundle.getInt("PetIdx")
            pet = CatInfoManager.petlist[idx]

            tvCatName.text = pet?.name?.T
            tvCatGender.text = pet?.sex?.T

            val catbreed = pet?.breeds?.breed
            if(catbreed is List<*>)
            {

                val temp = catbreed[0]
                if(temp is Map<*,*>)
                {
                    tvCatBreed.text = "${temp["${'$'}t"]}"

                }
            }
            else if(catbreed is Map<*,*>)
            {
                tvCatBreed.text = "${catbreed["${'$'}t"]}"
            }


            tvCatZip.text = pet?.contact?.zip?.T
            tvCatDes.text = pet?.description?.T

            Picasso.get().load("${pet?.media?.photos?.photo?.get(2)?.T}").into(imgCatPic)
        }

    }

    fun setFavorite(fav : Boolean)
    {
        isFavorite = fav
        if(isFavorite)
        {
            PresistenceManager.addFavorite(pet!!)

        }
        else
        {
            PresistenceManager.rmvFavorite(pet!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_catdetail, menu)
        actionBarMenu = menu

        if(PresistenceManager.isFavorite(pet!!))
        {
            setFavorite(true)
            (actionBarMenu.getItem(0) as MenuItem).setIcon(R.drawable.ic_action_favorite)
        }
        else
        {
            (actionBarMenu.getItem(0) as MenuItem).setIcon(R.drawable.ic_action_favorite_not)

        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.btn_favorite -> {

            if(isFavorite)
            {
                setFavorite(false)
                item.setIcon(R.drawable.ic_action_favorite_not)
            }
            else
            {
                setFavorite(true)
                item.setIcon(R.drawable.ic_action_favorite)
            }

            true
        }
        R.id.btn_mail -> {
            val to = "person@gmail.com"
            val subject = "Test"
            val message = "Test22222"

            val intent = Intent(Intent.ACTION_SEND)
            val addressees = arrayOf(to)
            intent.putExtra(Intent.EXTRA_EMAIL, addressees)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.setType("message/rfc822")
            startActivity(Intent.createChooser(intent, "Send Email using:"));
            true
        }
        R.id.btn_share -> {

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            startActivity(Intent.createChooser(shareIntent,getString(R.string.abc_action_bar_home_description)))

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}