package com.example.heran.findacat.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.heran.findacat.R
import android.support.v7.widget.RecyclerView
import com.example.heran.findacat.Adapter.CatsAdapter
import com.example.heran.findacat.Adapter.ILoadMore
import com.example.heran.findacat.Constants
import com.example.heran.findacat.Model.generated.Pet
import com.example.heran.findacat.Model.generated.PetfinderPetRecord
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class ListActivity : AppCompatActivity(), ILoadMore {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : CatsAdapter

    private var offset = 0
    private val returnSize = 25
    private var zipCode : String = "22202"
    private var requestType : Int = 0

    var petlist : MutableList<Pet?> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.rv_cat_list)

        recyclerView.layoutManager = linearLayoutManager

        adapter = CatsAdapter(recyclerView, this, petlist)
        adapter.setLoadMore(this)
        recyclerView.adapter = adapter

        getPetList()

    }



    fun getPetList()
    {
        petlist.add(null)
        adapter.notifyItemInserted(petlist.size-1)


        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BING_SEARCH_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi) )
                .build()

        if(requestType == 0)
        {
            val apiEndpoint = retrofit.create(ListActivity.petfinderPetRecordListInterface::class.java)
            apiEndpoint.getBingResponse("${Constants.BING_API_KEY}", "cat",
                    zipCode, "${returnSize}", "${offset}","json")
                    .enqueue(object: Callback<PetfinderPetRecord> {

                        override fun onFailure(call: Call<PetfinderPetRecord>, t: Throwable) {
                            var a = 0
                        }

                        override fun onResponse(call: Call<PetfinderPetRecord>, response: Response<PetfinderPetRecord>) {
                            val bingResponseBody = response.body()
                            petlist.removeAt(petlist.size - 1)
                            adapter.notifyItemRemoved(petlist.size)

                            if(bingResponseBody != null) {
                                var temp = bingResponseBody.petfinder?.pets?.pet
                                if(temp != null)
                                {
                                    for(pet : Pet? in temp)
                                    {
                                        petlist.add(pet)
                                    }
                                }
                                offset += returnSize
                                adapter.notifyDataSetChanged()
                                adapter.setLoaded()

                            }else{
                                //TODO: handle response body null

                            }

                        }

                    })
        }
        else
        {
            val apiEndpoint = retrofit.create(ListActivity.petfinderPetRecordInterface::class.java)
            apiEndpoint.getBingResponse("${Constants.BING_API_KEY}", "12333","json")
                    .enqueue(object: Callback<PetfinderPetRecord> {

                        override fun onFailure(call: Call<PetfinderPetRecord>, t: Throwable) {
                            var a = 0
                        }

                        override fun onResponse(call: Call<PetfinderPetRecord>, response: Response<PetfinderPetRecord>) {
                            val bingResponseBody = response.body()
                            petlist.removeAt(petlist.size - 1)
                            adapter.notifyItemRemoved(petlist.size)

                            if(bingResponseBody != null) {
                                var temp = bingResponseBody.petfinder?.pets?.pet
                                if(temp != null)
                                {
                                    for(pet : Pet? in temp)
                                    {
                                        petlist.add(pet)
                                    }
                                }
                                offset += returnSize
                                adapter.notifyDataSetChanged()
                                adapter.setLoaded()

                            }else{
                                //TODO: handle response body null

                            }

                        }

                    })
        }

    }

    override fun onLoadMore() {

        if(petlist.size >=returnSize)
            getPetList()

    }

    interface petfinderPetRecordInterface{
        @GET("pet.get")
        fun getBingResponse(@Query("key") k:String, @Query("id") query: String, @Query("format") f : String): Call<PetfinderPetRecord>
    }

    interface petfinderPetRecordListInterface{
        @GET("pet.find")
        fun getBingResponse(@Query("key") k:String, @Query("animal") animal : String, @Query("location") location: String, @Query("count") count : String, @Query("offset") offset : String, @Query("format") f : String): Call<PetfinderPetRecord>
    }

}

