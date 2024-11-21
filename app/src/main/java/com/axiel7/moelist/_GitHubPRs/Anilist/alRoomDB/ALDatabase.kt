//package com.axiel7.moelist._GitHubPRs.Anilist.RoomDB
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//
//@Database(entities = [Media::class], version = 1)
//abstract class ALDatabase : RoomDatabase() {
////    abstract fun MediaDaoLD(): MediaDaoLD
//    abstract fun MediaDao(): MediaDao
//
//    companion object {
//        @Volatile
////        private var INSTANCE: AL_Database? = null
//        var INSTANCE: ALDatabase? = null
//
//        fun getDatabase(context: Context): ALDatabase {
//            val tempInstance = INSTANCE
//            if (tempInstance != null){
//                return tempInstance
//            }
//            synchronized(this)
//            {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    ALDatabase::class.java,
//                    "AL_Database"
//                ).build()
//
//                INSTANCE = instance
//                return instance
//            }
//
//        }
//    }
//}
//
//
///*
//
//5. Perform CRUD Operations:
//
//In your activity or fragment, you can use the database like this:
//
//Initialize the database
//```
//    // Initialize the database
//    val noteDatabase = NoteDatabase.getDatabase(this)
//
//
//    // Insert a new note
//    val newNote = Note(title = "My Note", content = "This is a sample note.")
//    noteDatabase.noteDao().insert(newNote)
//```
//
//Update a note
//```
//    val existingNote = noteDatabase.noteDao().getAllNotes().value?.firstOrNull()
//    existingNote?.let {
//        it.title = "Updated Note"
//        noteDatabase.noteDao().update(it)
//    }
//```
//
//Delete a note
//```
//    existingNote?.let {
//        noteDatabase.noteDao().delete(it)
//    }
//```
//
//Retrieve all notes
//```
//    val allNotes = noteDatabase.noteDao().getAllNotes()
//    allNotes.observe(this, { notes ->
//        // Update UI with the list of notes
//    })
//```
//
//
//
//
//
//
//
// */