package com.axiel7.moelist._GitHubPRs.Anilist

import com.axiel7.moelist._GitHubPRs.Anilist.poco.ALNextAiringEpisode
import com.axiel7.moelist._GitHubPRs.Anilist.poco.Media
import com.axiel7.moelist._GitHubPRs.Anilist.other.ANILIST_GRAPHQL_URL
import com.axiel7.moelist.data.model.anime.UserAnimeList
import com.axiel7.moelist.data.model.media.ListStatus
import com.axiel7.moelist.utils.StringExtensions.toStringOrEmpty
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.hours


class AnilistQuery {

    //static Holder
    companion object {
        lateinit var cacheDir: File

        //okhttp - cache -doesnt work. cant figure out.
        /**
         * context.cacheDir
         */
        lateinit var OkHttp_Cache: Cache

        suspend fun init_New_OkHttpCache(_cacheDir: File) {
            cacheDir = _cacheDir
            var OkHttp_cacheSize: Int = 10 * 1024 * 1024 /* 10 MB. Also try increasing cache size */
            OkHttp_Cache = Cache(_cacheDir, OkHttp_cacheSize.toLong())
        }


        //---------single Item
        lateinit var cache : InMemoryKache<Int, Media>

        suspend fun init_New_ObjectKache() {
            cache = New_ObjectKache()
        }
        @JvmStatic
        suspend fun New_ObjectKache() : InMemoryKache<Int, Media>  {
            //GlobalScope.launch (Dispatchers.Main)  {}
            val cache = InMemoryKache<Int, Media>(500) {
                strategy = KacheStrategy.LRU
                expireAfterAccessDuration =  1.hours
            }
            return cache
        }



        //---------------cache - ListT
        lateinit var cache_ListT : InMemoryKache<List<Int>,List<Media>>

        suspend fun init_New_ObjectKache_ListT() {
            cache_ListT = New_ObjectKache_ListT()
        }
        @JvmStatic
        suspend fun New_ObjectKache_ListT() : InMemoryKache<List<Int>,List<Media>>  {
            //GlobalScope.launch (Dispatchers.Main)  {}
            val cache = InMemoryKache<List<Int>,List<Media>>(500) {
                strategy = KacheStrategy.LRU
                expireAfterAccessDuration =  1.hours
            }
            return cache
        }



        //---------------Normal
        /**
         * Uses withCache
         */
        suspend fun AddNextAiringEpInfo_withMeasureTime(
            result: com.axiel7.moelist.data.model.Response<List<UserAnimeList>>) {
            result.data?.let {
                val timeInMillis: Long = measureTimeMillis {
                    AddNextAiringEpInfo(it)
                }
                println("AddNextAiringEpInfo : elapsedTime(ms):" + timeInMillis)
            };
        }

        //AnimeRepository
        /**
         * Uses withCache
         */
        suspend fun AddNextAiringEpInfo( userAnimeList :List<UserAnimeList>
        ):List<UserAnimeList>?
        {
            fun _isAiring(it: UserAnimeList) =
                (
                        (it.listStatus?.status == ListStatus.WATCHING
                                || it.listStatus?.status == ListStatus.PLAN_TO_WATCH)
                                && it.isAiring
                        )

            var airingAnimes = userAnimeList.filter{ _isAiring(it) }

            val airingAnimes_idlist = airingAnimes.map{ it.node.id }
            if (airingAnimes_idlist.isEmpty())
                return null

            var al_mediaList = GetAiringInfo_ToPoco_FromCache_ListT(airingAnimes_idlist)
            if (al_mediaList?.isEmpty() == true)
                return null

            userAnimeList.filter  { _isAiring(it) }.forEach { it ->
                var _id = it.node.id.toLong();
                // it.node.al_nextAiringEpisode = "test success";
                var it_AirInfo = al_mediaList?.firstOrNull {  it.idMal == _id }?.nextAiringEpisode

                it.node.al_nextAiringEpisode = it_AirInfo?.EpN_in_Mdays_ToString()
            }
            return userAnimeList;
        }


        fun KotlinJsonTest(mediaList: List<Media>) {
            // Serialization
            val media0 = mediaList[0];
            val json = Json.encodeToString(media0)
            println("KotlinJsonTest - encodeToString: " + json)
//            println(json) // Output: {"name":"John Doe","age":30}

//            // Deserialization
//            val jsonString = """{"name":"Jane Smith","age":25}"""
//            val newUser = Json.decodeFromString<Media>(jsonString)
//            println(newUser) // Output: User(name=Jane Smith, age=25)
        }

        //---------------GetAiringInfo
        suspend fun GetAiringInfo_ToPoco_FromCache_ListT_withMeasureTime(mal_id_list: List<Int>): List<Media>?
        {
            var ret :List<Media>?

            val timeInMillis: Long = measureTimeMillis {
                ret = GetAiringInfo_ToPoco_FromCache_ListT(mal_id_list)
            }
            println("GetAiringInfo_ToPoco_FromCache_ListT : elapsedTime(ms):" + timeInMillis)

            ret?.let { KotlinJsonTest(it) }

            return ret
        }
        suspend fun GetAiringInfo_ToPoco_FromCache_ListT(mal_id_list: List<Int>): List<Media>?
        {
            val key = mal_id_list;

            val data = cache_ListT.getOrPut(key) {
                try {
                    GetAiringInfo_ToPoco(key)
                } catch (ex: Throwable) {
                    println(ex.message )
                    println(ex.cause )
                    println("Throwed Exception at GetAiringInfo_ToPoco_FromCache" )
                    null // returning null, The value (null) will not be cached
                }
            }
            return data;
        }

        fun GetAiringInfo_ToPoco( mal_id_list: List<Int> ): List<Media>?
        {
            var resp1 = getAiringInfo(mal_id_list)
            var resp1_bodSTR = resp1.body?.string().toStringOrEmpty()
            val al_AirDataList = Json.decodeFromString<ALNextAiringEpisode>(resp1_bodSTR)
            var al_mediaList = al_AirDataList.data?.Page?.media;
            return al_mediaList
        }
        
        fun getAiringInfo (mal_id_list: List<Int>): Response
        {
            var url =  "https://com.example/graphql";
            var query = Build_query_AiringInfo(mal_id_list);
//        // HAVE TO MAKE graphql query one liner.  - otherwise its not valid json, will fail.
//        query = query.replace("\n", "  " ).replace("  ", "  ")

            var resp1 = makeRequest_POST_JSON(ANILIST_GRAPHQL_URL,query);

            //okhttp doesn't use cache if app is restarted
            if(resp1.networkResponse == null)
                println("getAiringInfo - okhttp -response From cache")

            if(resp1.code != 200) {
                println("response NOT200:" + resp1.code.toString() )
                var errorMessage = resp1.body?.string()
                println("response_body?_string:" + errorMessage)
            }

            return  resp1;
        }

        private fun Build_query_AiringInfo( mal_id_list: List<Int> ): String
        {
            val mal_ids_asSTR = mal_id_list.joinToString(separator = ", ")

            //validJSON
            val query_AiringInfo_oneliner = """   query Al_AiringInfo { Page (page: 0, perPage: 50) {  pageInfo {  total  currentPage  lastPage  hasNextPage  perPage  }  media(idMal_in: [$mal_ids_asSTR], type: ANIME)  {  id  idMal  nextAiringEpisode {  episode  timeUntilAiring  }  title {  english  }  } } }  """;
            return  query_AiringInfo_oneliner;

//        val query_AiringInfo =
//            """
//query  {
//    Page (page: 0, perPage: 50) {
//        pageInfo {
//            total
//            currentPage
//            lastPage
//            hasNextPage
//            perPage
//        }
//
//        media(idMal_in: [$mal_ids_asSTR], type: ANIME)
//        {
//            id
//            idMal
//            nextAiringEpisode {
//                episode
//                timeUntilAiring
//            }
//            title {
//                english
//            }
//        }
//    }
//}
//
//"""

        }

        /**
         *  Enabled Cache For 1 Hour
         */
        private fun makeRequest_POST_JSON(url: String, query: String ): Response
        {
            var jsonQuery =  """ {"query":"  $query  ", "variables":null, "operationName":null} """
//            val client = OkHttpClient()
            val client = NewOkHttpClient()

            val requestBody = jsonQuery.toRequestBody()
            val requestBuilder =
                Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-type", "application/json")
                    .addHeader("Accept", "application/json")

            //this part doenst work ??!!..
                val maxAge = 60 *60 *6 // read from cache for 6 hour
                val maxStale = 60 * 60 * 24 * 3 // tolerate 3-days stale
                requestBuilder
                    .addHeader("Cache-Control", "public, max-age=$maxAge")
//                    .addHeader("Cache-Control", "public, only-if-cached, max-stale=$maxStale")

            return client.newCall(requestBuilder.build()).execute()
        }


        fun NewOkHttpClient():OkHttpClient
        {
            //val cacheSize = (10 * 1024 * 1024).toLong() /*10mb*/
            //val cache = Cache(context_cacheDir, cacheSize)

            val okHttpClient = OkHttpClient.Builder()
                .cache(OkHttp_Cache)
                .build()

            return okHttpClient;
        }




    }


}

