package com.example.heran.findacat.Manager

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PresistenceManager
{

    private var sharePreferences: SharedPreferences? = null
    private val KEY_FAVORITELIST = "key_flist"
    private var editor : SharedPreferences.Editor? = null
    private var idlist : MutableSet<String>? = null


    fun setContext(context: Context) {
        if(sharePreferences == null)
        {
            sharePreferences = PreferenceManager.getDefaultSharedPreferences(context)
            idlist = getFavoriteList()
            editor = sharePreferences?.edit()
        }
    }


    fun addFavorite(id :String)
    {
        if(!idlist?.contains(id)!!)
        {
            idlist?.add(id)
            editor?.putStringSet(KEY_FAVORITELIST, idlist)
            editor?.apply()
        }

    }

    fun rmvFavorite(id : String) : Boolean
    {
        if(idlist?.contains(id)!!)
        {
            idlist?.remove(id)
            editor?.putStringSet(KEY_FAVORITELIST, idlist)
            editor?.apply()
        }
        return false
    }




    fun clearFavoriteList()
    {
        idlist = HashSet()
        editor?.putStringSet(KEY_FAVORITELIST, null)
        editor?.apply()
    }

    private fun getFavoriteList() : MutableSet<String>?
    {
        return sharePreferences?.getStringSet(KEY_FAVORITELIST, HashSet())
    }

    fun getList() :MutableSet<String>?
    {
        return idlist
    }
}