package com.example.heran.findacat.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

import com.example.heran.findacat.R



class MenuActivity : AppCompatActivity() {

    //private var mFusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var findBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findBtn = findViewById<Button>(R.id.menu_find_btn)

        findBtn.setOnClickListener {
            var intent = Intent( this, ListActivity::class.java)
            startActivity(intent)

        }
    }


}

