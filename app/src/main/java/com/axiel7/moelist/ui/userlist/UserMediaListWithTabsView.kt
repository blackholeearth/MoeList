package com.axiel7.moelist.ui.userlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.moelist.data.model.media.ListStatus
import com.axiel7.moelist.data.model.media.ListStatus.Companion.listStatusValues
import com.axiel7.moelist.data.model.media.MediaSort
import com.axiel7.moelist.data.model.media.MediaType
import com.axiel7.moelist.ui.base.TabRowItem
import com.axiel7.moelist.ui.base.navigation.NavActionManager
import com.axiel7.moelist.ui.composables.LoadingDialog
import com.axiel7.moelist.ui.composables.TabRowWithPager
import com.axiel7.moelist.ui.editmedia.EditMediaSheet
import com.axiel7.moelist.ui.theme.MoeListTheme
import com.axiel7.moelist.ui.userlist.composables.MediaListSortDialog
import com.axiel7.moelist.ui.userlist.composables.SetScoreDialog
import com.axiel7.moelist.utils.ContextExtensions.showToast
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMediaListWithTabsView(
    mediaType: MediaType,
    isCompactScreen: Boolean,
    navActionManager: NavActionManager,
    padding: PaddingValues,
) {
    val tabRowItems = remember {
        listStatusValues(mediaType)
            .map {
                //TabRowItem(value = it, title = it.stringRes)
                TabRowItem(value = it, title = it.stringResShort)
            }.toTypedArray()
    }

    TabRowWithPager(
        tabs = tabRowItems,
        modifier = Modifier
            .padding(
                top = padding.calculateTopPadding(),
            ),
        beyondBoundsPageCount = -1,
        // This is the fix to make tabs fit the screen without scrolling
        isTabScrollable = false
    ) { page ->
        val listStatus = tabRowItems[page].value
        val viewModel: UserMediaListViewModel = koinViewModel(
            key = listStatus.name,
            parameters = { parametersOf(mediaType, listStatus) }
        )
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // Delegate UI rendering to the Content composable
        UserMediaListPageContent(
            uiState = uiState,
            event = viewModel,
            navActionManager = navActionManager,
            isCompactScreen = isCompactScreen,
            padding = padding
        )
    }//:Pager
}

/**
 * A private composable that handles the UI for a single page in the pager.
 * It is decoupled from Koin and ViewModel creation, making it previewable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserMediaListPageContent(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    navActionManager: NavActionManager,
    isCompactScreen: Boolean,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val editSheetState = rememberModalBottomSheetState()
    var showEditSheet by remember { mutableStateOf(false) }

    fun hideEditSheet() {
        scope.launch { editSheetState.hide() }.invokeOnCompletion { showEditSheet = false }
    }

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    if (uiState.openSortDialog && uiState.listSort != null) {
        MediaListSortDialog(uiState, event)
    }
    if (uiState.openSetScoreDialog) {
        SetScoreDialog(
            onDismiss = { event?.toggleSetScoreDialog(false) },
            onConfirm = { event?.setScore(it) }
        )
    }
    if (uiState.isLoadingRandom) {
        LoadingDialog()
    }
    if (showEditSheet && uiState.mediaInfo != null) {
        EditMediaSheet(
            sheetState = editSheetState,
            mediaInfo = uiState.mediaInfo!!,
            myListStatus = uiState.myListStatus,
            bottomPadding = systemBarsPadding.calculateBottomPadding(),
            onEdited = { status, removed ->
                hideEditSheet()
                event?.onChangeItemMyListStatus(status, removed)
            },
            onDismissed = { hideEditSheet() }
        )
    }
    LaunchedEffect(uiState.randomId) {
        uiState.randomId?.let { id ->
            navActionManager.toMediaDetails(uiState.mediaType, id)
            event?.onRandomIdOpen()
        }
    }
    LaunchedEffect(uiState.message) {
        if (uiState.message != null) {
            context.showToast(uiState.message.orEmpty())
            event?.onMessageDisplayed()
        }
    }
    if (uiState.listSort != null) {
        UserMediaListView(
            uiState = uiState,
            event = event,
            navActionManager = navActionManager,
            isCompactScreen = isCompactScreen,
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding() + systemBarsPadding.calculateBottomPadding()
            ),
            onShowEditSheet = { item ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                event?.onItemSelected(item)
                showEditSheet = true
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserMediaListWithTabsViewPreview() {
    MoeListTheme {
        Surface {
            val mediaType = MediaType.ANIME
            val tabRowItems = remember {
                listStatusValues(mediaType)
                    .map {
                        TabRowItem(value = it, title = it.stringRes)
                    }.toTypedArray()
            }

            TabRowWithPager(
                tabs = tabRowItems,
                isTabScrollable = false
            ) { page ->
                // For each tab page in the preview, show the content with dummy data
                val listStatus = tabRowItems[page].value
                UserMediaListPageContent(
                    uiState = UserMediaListUiState(
                        mediaType = mediaType,
                        listStatus = listStatus,
                        listSort = MediaSort.UPDATED,
//                        items = flowOf() // Provide an empty flow for the preview
                    ),
                    event = null,
                    navActionManager = NavActionManager.rememberNavActionManager(),
                    isCompactScreen = true,
                    padding = PaddingValues(0.dp)
                )
            }
        }
    }
}
