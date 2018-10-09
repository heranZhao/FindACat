package com.example.heran.findacat.Activity

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.heran.findacat.Constants
import com.example.heran.findacat.Model.generated.CatFact.CatFactResponse
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.Model.generated.PetfinderPetRecord
import com.example.heran.findacat.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class MenuActivity : AppCompatActivity() {

    //private var mFusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var findBtn : Button
    private lateinit var tvcatFact : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findBtn = findViewById<Button>(R.id.menu_find_btn)
        tvcatFact = findViewById<TextView>(R.id.tv_cat_fact)


        findBtn.setOnClickListener {
            var intent = Intent( this, ListActivity::class.java)
            startActivity(intent)

        }

//        var zipcode = MyLocationManager().getLocation(this)
//
//        toast("$zipcode")

    }

    override fun onResume() {
        super.onResume()

        tvcatFact.text = ""
        showCatFact()

    }


    private fun showCatFact()
    {
        doAsync {
            var keepgoing = true
            while(true)
            {
                if(!keepgoing)
                {
                    continue
                }
                keepgoing = false
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val retrofit = Retrofit.Builder()
                        .baseUrl("https://catfact.ninja")
                        .addConverterFactory(MoshiConverterFactory.create(moshi) )
                        .build()

                val apiEndpoint = retrofit.create(catFactResponseInterface::class.java)
                apiEndpoint.getBingResponse()
                        .enqueue(object: Callback<CatFactResponse> {
                            override fun onFailure(call: Call<CatFactResponse>, t: Throwable) {
                                var a = 0
                            }

                            override fun onResponse(call: Call<CatFactResponse>, response: Response<CatFactResponse>) {
                                val bingResponseBody = response.body()

                                if(bingResponseBody != null) {

                                    val fact: String = getString(R.string.cat_fact)

                                    tvcatFact.text = "$fact ${bingResponseBody.fact}"
                                    Handler().postDelayed({
                                        keepgoing = true
                                    },5000)

                                }else{
                                    //TODO: handle response body null

                                }

                            }
                        })

            }
        }
    }

    interface catFactResponseInterface
    {
        @GET("fact")
        fun getBingResponse(): Call<CatFactResponse>
    }

}

