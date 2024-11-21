//package com.axiel7.moelist._GitHubPRs.Anilist.RoomDB
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//
//@Dao
//interface MediaDao {
//    @Insert
//    suspend fun insert(note: Media)
//
//    @Update
//    suspend fun update(note: Media)
//
//    @Delete
//    suspend fun delete(note: Media)
//
//    @Query("SELECT * FROM ALMedias")
//    fun getAllMedias(): List<Media>
//}