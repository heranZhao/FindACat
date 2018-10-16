package com.example.heran.findacat.Manager

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.example.heran.findacat.Model.generated.Pet
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

object PresistenceManager
{

    private var sharePreferences: SharedPreferences? = null
    private val KEY_FAVORITELIST = "key_favoritelist"
    private var editor : SharedPreferences.Editor? = null
    private var idlist : MutableList<Pet?> = ArrayList()
    private val listType  = Types.newParameterizedType(List::class.java, Pet::class.java)
    private val moshi = Moshi.Builder()
            .add(Date::class.java, com.squareup.moshi.Rfc3339DateJsonAdapter())
            .build()
    private val jsonAdapter = moshi.adapter<List<Pet?>>(listType)

    fun setContext(context: Context) {
        if(sharePreferences == null)
        {
            sharePreferences = PreferenceManager.getDefaultSharedPreferences(context)
            idlist = getFavoriteList()
            editor = sharePreferences?.edit()
        }
    }

    fun getList() : MutableList<Pet?>
    {
        return idlist
    }

    fun addFavorite(pet :Pet)
    {
        if(!idlist?.contains(pet)!!)
        {
            idlist?.add(pet)
            val jsonString = jsonAdapter.toJson(idlist);
            editor?.putString(KEY_FAVORITELIST, jsonString)

            editor?.apply()
        }

    }

    fun rmvFavorite(pet : Pet) : Boolean
    {
        if(idlist?.contains(pet)!!)
        {
            idlist?.remove(pet)
            val jsonString = jsonAdapter.toJson(idlist);
            editor?.putString(KEY_FAVORITELIST, jsonString)
            editor?.apply()
        }
        return false
    }




    fun clearFavoriteList()
    {
        idlist = ArrayList()
        editor?.putStringSet(KEY_FAVORITELIST, null)
        editor?.apply()
    }

    private fun getFavoriteList() : MutableList<Pet?>
    {
        val jsonString = sharePreferences?.getString(KEY_FAVORITELIST, null)
        if(jsonString == null) {
            return ArrayList()
        }
        else {
            var pets:List<Pet?>? = ArrayList()
            try {
                pets = jsonAdapter.fromJson(jsonString)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, e.message)
            }

            if(pets != null) {
                return pets.toMutableList()
            }
            else {
                return ArrayList()
            }
        }
    }


    fun isFavorite(pet : Pet): Boolean
    {
        if(idlist.contains(pet))
            return true
        return false
    }
}