package com.axiel7.moelist._GitHubPRs.Anilist.poco

import com.axiel7.moelist._GitHubPRs.Anilist.other.secondsToDays_AsString
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ALNextAiringEpisode(
    val data: Data,
)
@Serializable
data class Data(
    @SerialName("Page" ) val Page: Page,
)
@Serializable
data class Page(
    val pageInfo: PageInfo,
    val media: List<Media>,
)
@Serializable
data class PageInfo(
    val total: Long,
    val currentPage: Long,
    val lastPage: Long,
    val hasNextPage: Boolean,
    val perPage: Long,
)

//@Entity(tableName = "AL_Medias")
//@Serializable
//data class Media(
//    // id is alraedy coming Form Web
//    //@PrimaryKey(autoGenerate = true)
//    val id: Long,
//    val idMal: Long,
//    val nextAiringEpisode: NextAiringEpisode?,
//    val title: Title,
//)

@Serializable
data class NextAiringEpisode(
    val episode: Long,
    val timeUntilAiring: Long,
)
{
    fun EpN_in_Mdays_ToString():String
    {
        var days = secondsToDays_AsString(timeUntilAiring)
        var str = """Ep ${episode} in ${days}"""
        return str
    }

}
@Serializable
data class Title(
    val english: String?,
)

