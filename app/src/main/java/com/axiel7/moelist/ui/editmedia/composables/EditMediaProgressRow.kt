package com.axiel7.moelist.ui.editmedia.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.moelist.R
import com.axiel7.moelist.data.model.media.MediaType
import com.axiel7.moelist.ui.editmedia.EditMediaEvent
import com.axiel7.moelist.ui.editmedia.EditMediaUiState
import com.axiel7.moelist.ui.theme.MoeListTheme
import com.axiel7.moelist.utils.StringExtensions.toStringOrEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMediaProgressRow(
    label: String,
    progress: Int?,
    modifier: Modifier,
    totalProgress: Int?,
    onValueChange: (String) -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {



        BasicTextField(
            value = progress.toStringOrEmpty(),
            onValueChange = onValueChange,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .fillMaxWidth().weight(5f)
                .padding(end = 16.dp)
                ,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textIndent = TextIndent(firstLine = 2.sp)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (progress == null) {
                        Text(
                            text = "0",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                    //suffix
                    totalProgress?.let {
                        Text(text = "/$it $label",
                            modifier = Modifier
                                .width(IntrinsicSize.Min))
                    }
                }
            }
        )



//        OutlinedTextField(
//            value = progress.toStringOrEmpty(),
//            onValueChange =  onValueChange,
//            modifier = Modifier
//                .fillMaxWidth().weight(5f)
//                .padding(end = 16.dp),
//           //label = { Text(text = label) },
//            suffix = {
//                totalProgress?.let { Text(text = "/$it $label") }
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//        )

        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onMinusClick()
            },
            modifier = Modifier
                //.weight(1f)
//                .fillMaxWidth().weight(2.5f)
                .padding(end = 8.dp),

            ) {
            Text(text = stringResource(R.string.minus_one))
        }

        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onPlusClick()
            },
            modifier = Modifier
                //.weight(1f)
//                .fillMaxWidth().weight(2.5f)


        ) {
            Text(text = stringResource(R.string.plus_one))
        }
    }
}





@Preview
@Composable
fun EditMediaProgressRowPreview() {

    MoeListTheme {
        Surface {
            val uiState = EditMediaUiState(
                mediaType = MediaType.ANIME
            )
            val event: EditMediaEvent? =null

            EditMediaProgressRow(
                label = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.episodes)
                else stringResource(R.string.chapters),
                progress = 199,//uiState.progress,
                modifier = Modifier
                    //.padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                totalProgress = uiState.mediaInfo?.totalDuration(),
                onValueChange = { event?.onChangeProgress(it.toIntOrNull()) },
                onMinusClick = { event?.onChangeProgress((uiState.progress ?: 0) - 1) },
                onPlusClick = { event?.onChangeProgress((uiState.progress ?: 0) + 1) }
            )

        }
    }


}