package com.axiel7.moelist._GitHubPRs.Anilist

import android.content.Context
import android.content.SharedPreferences
import com.axiel7.moelist._GitHubPRs.Anilist.poco.Media
import com.google.gson.Gson

/**
 * Persist My Data On Disk
 */
class SharedPrefs (val context: Context) {

    companion object {
        lateinit var Instance :SharedPrefs

        fun init_New_SharedPrefs(context: Context)
        {
            Instance = SharedPrefs(context)
        }

    }

    private val PREFS_NAME = "sharedpref--AL-media"
    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    //----media
    fun save_AL_Media(anime: Media) {
        var key = "al-m-" + anime.idMal.toString()

        sharedPref.edit().putString(key, Gson().toJson(anime)).apply()
    }

    fun get_AL_Media(malId : Int): Media? {
        var key = "al-m-"+ malId.toString()

        val data = sharedPref.getString(key, null)
        if (data == null) {
            return null
        }
        return Gson().fromJson(data, Media::class.java)
    }

//
//    //----Kache
//    fun save_Kache(kacheobj: InMemoryKache<List<Int>,List<Media>>) {
//        sharedPref.edit().putString("kacheobj", Gson().toJson(kacheobj)).apply()
//    }
//
//    fun get_Kache(): InMemoryKache<List<Int>, List<Media>>? {
//        val data = sharedPref.getString("kacheobj", null)
//        if (data == null) {
//            return null
//        }
//        val ret = Json.decodeFromString(InMemoryKache<List<Int>,List<Media>>(),data)
////        return Gson().fromJson(data, InMemoryKache<List<Int>,List<Media>    >::class.java)
//    }

}