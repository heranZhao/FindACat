package com.example.heran.findacat.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.heran.findacat.Manager.CatInfoManager
import com.example.heran.findacat.R
import org.jetbrains.anko.toast

class CatDetailActivity: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catdetail)

        val bundle = intent.extras
        if(bundle!=null)
        {
            val idx = bundle.getInt("PetIdx")

            toast("${CatInfoManager.petlist.get(idx)?.name?.T}")
        }

    }

}