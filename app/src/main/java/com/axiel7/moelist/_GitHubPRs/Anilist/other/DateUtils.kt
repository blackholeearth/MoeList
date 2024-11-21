package com.axiel7.moelist._GitHubPRs.Anilist.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.axiel7.moelist.R
import com.axiel7.moelist.data.model.anime.AnimeNode
import com.axiel7.moelist.data.model.anime.Broadcast
import com.axiel7.moelist.data.model.media.BaseMediaNode
import com.axiel7.moelist.data.model.media.BaseUserMediaList
import com.axiel7.moelist.utils.StringExtensions.toStringOrEmpty

@Composable
fun AiringEpN_in_Ndays_ToString(
    broadcast: Broadcast?,
    item: BaseUserMediaList<out BaseMediaNode>
): String {
    val isAiring = remember { item.isAiring }

//  var textCompact = broadcast?.airingInString() ?: stringResource(R.string.airing)
    var text =
         if (isAiring ) broadcast?.airingInString() ?: stringResource(R.string.airing)
       else item.node.mediaFormat?.localized().orEmpty()

    if (item.node is AnimeNode)
        text = (item.node as AnimeNode)?.al_nextAiringEpisode.toStringOrEmpty()
    return text
}

/**
 * For Grid - ie: 8d
 */
@Composable
fun AiringEpN_in_Ndays_ToShortString(
    broadcast: Broadcast?,
    item: BaseUserMediaList<out BaseMediaNode>
): String {
    val isAiring = remember { item.isAiring }

    var text = broadcast?.airingInShortString() ?: stringResource(R.string.airing)

    if (item.node is AnimeNode)
        text = (item.node as AnimeNode)?.al_nextAiringEpisode.toStringOrEmpty()
    return text
}



//----------- seconds to NaturalLangDate Util
/**
 * Supports Months, Days, Hours, Minutes. -- less than a minute will be = "? sec"
 */
fun secondsToDays_AsString(seconds: Long): String {
    val _1month :Long = 30 *24 * 60 * 60
    val _1day :Long = 24 * 60 * 60
    val _1hour :Long = 60 * 60
    val _1min :Long =  60

    var HumanReadbleTime =""

    HumanReadbleTime =
        GetNLDatesString_OrNull(seconds, _1month , "months" ,"month" )
            ?: GetNLDatesString_OrNull(seconds, _1day , "days" ,"day" )
                    ?: GetNLDatesString_OrNull(seconds, _1hour , "hours" ,"hour" )
                    ?: GetNLDatesString_OrNull(seconds, _1min , "mins" ,"min" )
                    ?: "? sec" ;


//    if(seconds> _1day)
//    { val days = seconds / _1day; HumanReadbleTime="${days} days" }
//    else if(seconds== _1day)
//    { val days = seconds / _1day; HumanReadbleTime="${days} day" }
//
//    else if(seconds>_1hour)
//    { val days = seconds / _1hour; HumanReadbleTime="${days} hours" }
//    else if(seconds==_1hour)
//    { val days = seconds / _1hour; HumanReadbleTime="${days} hour"}
//
//    else if(seconds>_1min)
//    { val days = seconds / _1min; HumanReadbleTime="${days} mins"}
//    else if(seconds==_1min)
//    { val days = seconds / _1min; HumanReadbleTime="${days} min"}
//    else
//    { val days = seconds / _1min; HumanReadbleTime="? sec"}

    return HumanReadbleTime;
}

private fun GetNLDatesString_OrNull(
    seconds: Long,
    _1Period: Long,
    PluaralText:String,
    SingularText:String,
): String? {
    var HumanReadbleTime: String? = null

    if (seconds > _1Period) {
        val days = seconds / _1Period; HumanReadbleTime = "${days} ${PluaralText}"
    } else if (seconds == _1Period) {
        val days = seconds / _1Period; HumanReadbleTime = "${days} ${SingularText}"
    }
    return HumanReadbleTime;
}




// old --backup

//fun EpN_in_Mdays_ToString(it_AirInfo: NextAiringEpisode?) =
//    """Ep ${it_AirInfo?.episode} in ${secondsToDays(it_AirInfo?.timeUntilAiring ?: Long.MAX_VALUE)} day(s) """

////this behaves likes Static Func of c#. interesting?
//fun secondsToDays_AsStr(seconds: Long): String {
//    val days = seconds.toDouble() / (24 * 60 * 60)
//    return String.format("%.1f", days)
//}

