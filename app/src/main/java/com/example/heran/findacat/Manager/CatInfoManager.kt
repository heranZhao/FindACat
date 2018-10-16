package com.example.heran.findacat.Manager

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

object CatInfoManager
{

    var catinfoListener : CatInfoFinishedListener? = null
    var petlist : MutableList<Pet?> = ArrayList()

    interface CatInfoFinishedListener{
        fun loadListSuccess()
        fun loadListFailure()
        fun loadSingleSuccess()
        fun loadSingleFailure()
    }


    fun getPetList(zipCode : String, offset : String, returnSize : String)
    {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BING_SEARCH_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi) )
                .build()

        val apiEndpoint = retrofit.create(PetfinderPetRecordListInterface::class.java)
        apiEndpoint.getBingResponse(Constants.BING_API_KEY, "cat",
                zipCode, returnSize, offset,"json")
                .enqueue(object: Callback<PetfinderPetRecord> {

                    override fun onFailure(call: Call<PetfinderPetRecord>, t: Throwable) {
                        var a = 0
                    }

                    override fun onResponse(call: Call<PetfinderPetRecord>, response: Response<PetfinderPetRecord>) {
                        val bingResponseBody = response.body()

                        if(bingResponseBody != null) {
                            val temp = bingResponseBody.petfinder?.pets?.pet
                            if(temp != null)
                            {
                                for(pet : Pet? in temp)
                                {
                                    petlist.add(pet)
                                }

                                catinfoListener?.loadListSuccess()
                            }

                        }else{
                            catinfoListener?.loadListFailure()
                        }

                    }

                })
    }

    interface PetfinderPetRecordListInterface{
        @GET("pet.find")
        fun getBingResponse(@Query("key") k:String, @Query("animal") animal : String, @Query("location") location: String, @Query("count") count : String, @Query("offset") offset : String, @Query("format") f : String): Call<PetfinderPetRecord>
    }
}