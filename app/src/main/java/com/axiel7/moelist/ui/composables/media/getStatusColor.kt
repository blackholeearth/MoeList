package com.axiel7.moelist.ui.composables.media

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.moelist.data.model.media.ListStatus

val StatusWatching = Color(0xFF357f3d)
val StatusPlanned = Color(0xFFC3C3C3)
val StatusCompleted = Color(0xFF2b406f)
val StatusOnHold = Color(0xFFc89a1c)
val StatusDropped = Color(0xFF812d2b)

@Composable
fun getStatusColor(status: ListStatus?): Color {
    return when (status) {
        ListStatus.WATCHING, ListStatus.READING -> StatusWatching
        ListStatus.PLAN_TO_WATCH,ListStatus.PLAN_TO_READ -> StatusPlanned
        ListStatus.COMPLETED -> StatusCompleted
        ListStatus.ON_HOLD -> StatusOnHold
        ListStatus.DROPPED -> StatusDropped
        else -> MaterialTheme.colorScheme.primaryContainer // A sensible default
    }
}