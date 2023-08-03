package com.axiel7.moelist.uicompose.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.moelist.data.model.anime.AnimeRanking
import com.axiel7.moelist.data.model.anime.airingInString
import com.axiel7.moelist.data.model.media.userPreferredTitle
import com.axiel7.moelist.uicompose.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.moelist.uicompose.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.moelist.uicompose.composables.media.MediaPoster
import com.axiel7.moelist.uicompose.composables.media.SmallScoreIndicator

@Composable
fun AiringAnimeHorizontalItem(item: AnimeRanking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .sizeIn(maxWidth = 300.dp, minWidth = 250.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        MediaPoster(
            url = item.node.mainPicture?.large,
            modifier = Modifier.size(
                width = MEDIA_POSTER_SMALL_WIDTH.dp,
                height = MEDIA_POSTER_SMALL_HEIGHT.dp
            )
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = item.node.userPreferredTitle(),
                fontSize = 18.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = item.node.broadcast?.airingInString() ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SmallScoreIndicator(
                score = item.node.mean
            )
        }
    }
}