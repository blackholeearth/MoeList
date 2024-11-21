package com.axiel7.moelist._GitHubPRs.Anilist.poco

//import androidx.room.Entity
import kotlinx.serialization.Serializable


//@Entity(tableName = "ALMedias")
@Serializable
data class Media(
    // id is alraedy coming Form Web
    //@PrimaryKey(autoGenerate = true)
    val id: Long,
    val idMal: Long,
    val nextAiringEpisode: NextAiringEpisode?,
    val title: Title,
)

