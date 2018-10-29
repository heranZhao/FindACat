package com.example.heran.findacat.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.heran.findacat.Manager.NetworkManager
import com.example.heran.findacat.Model.generated.CatFact.CatFactResponse
import com.example.heran.findacat.R
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


class MenuActivity : AppCompatActivity() {

    private lateinit var findBtn : Button
    private lateinit var favoriteBtn : Button
    private lateinit var tvcatFact : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findBtn = findViewById(R.id.menu_find_btn)
        favoriteBtn = findViewById(R.id.menu_favorite_btn)
        tvcatFact = findViewById(R.id.tv_cat_fact)

        val noNetworkMsg = resources.getString(R.string.noNetwork)

        findBtn.setOnClickListener {

            if(NetworkManager.isNetworkAvailable(this))
            {
                val intent = Intent( this, ListActivity::class.java)
                intent.putExtra(ListActivity.KEY_REQUESTTYPE, 0)
                startActivity(intent)
            }
            else
                toast(noNetworkMsg)

        }
        favoriteBtn.setOnClickListener {

            if(NetworkManager.isNetworkAvailable(this))
            {
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra(ListActivity.KEY_REQUESTTYPE, 1)
                startActivity(intent)
            }
            else
                toast(noNetworkMsg)
        }

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

                                    tvcatFact.text = bingResponseBody.fact
                                    Handler().postDelayed({
                                        keepgoing = true
                                    },10000)

                                }else{

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

