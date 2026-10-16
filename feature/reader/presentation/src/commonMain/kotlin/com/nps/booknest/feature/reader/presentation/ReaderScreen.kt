package com.nps.booknest.feature.reader.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nps.booknest.core.designsystem.BookNestSpacing
import com.nps.booknest.core.designsystem.BookNestTheme
import com.nps.booknest.core.presentation.ObserveAsEvents
import com.nps.booknest.core.presentation.asString
import com.nps.booknest.core.settings.ReaderFontSize
import com.nps.booknest.core.settings.ReaderThemeMode
import com.nps.booknest.feature.reader.domain.ReadableBook
import com.nps.booknest.feature.reader.domain.ReadableChapter
import com.nps.booknest.feature.reader.domain.ReadableParagraph
import com.nps.booknest.feature.reader.presentation.generated.resources.Res
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_appearance_title
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_back_description
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_chapters_description
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_chapters_title
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_decrease_font_description
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_font_size
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_increase_font_description
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_loading
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_missing
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_next_chapter
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_previous_chapter
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_progress_description
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_settings_description
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme_dark
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme_light
import com.nps.booknest.feature.reader.presentation.generated.resources.reader_theme_sepia
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val ReaderMaxContentWidth = 680.dp

@Composable
fun ReaderRoot(
    bookId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReaderViewModel = koinViewModel(parameters = { parametersOf(bookId) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingSnackbar by remember { mutableStateOf<com.nps.booknest.core.presentation.UiText?>(null) }
    val snackbarMessage = pendingSnackbar?.asString()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ReaderEvent.NavigateBack -> onNavigateBack()
            is ReaderEvent.ShowSnackbar -> pendingSnackbar = event.message
        }
    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            pendingSnackbar = null
        }
    }

    ReaderScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    state: ReaderState,
    onAction: (ReaderAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = state.themeMode.readerColors()
    val book = state.book
    val chapter = book?.chapters?.getOrNull(state.chapterIndex)

    Scaffold(
        modifier = modifier.fillMaxSize().testTag(ReaderTestTags.READER_SCREEN),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ReaderTopBar(
                title = chapter?.title ?: book?.title.orEmpty(),
                colors = colors,
                onAction = onAction,
            )
        },
        bottomBar = {
            if (book != null && chapter != null) {
                ReaderBottomBar(
                    state = state,
                    colors = colors,
                    onAction = onAction,
                )
            }
        },
    ) { contentPadding ->
        ReaderBody(
            state = state,
            book = book,
            chapter = chapter,
            colors = colors,
            contentPadding = contentPadding,
            onAction = onAction,
        )
    }

    if (state.isChapterSheetVisible && book != null) {
        ChapterListSheet(book = book, selectedChapterIndex = state.chapterIndex, onAction = onAction)
    }
    if (state.isSettingsSheetVisible) {
        ReaderSettingsSheet(state = state, onAction = onAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderTopBar(
    title: String,
    colors: ReaderColors,
    onAction: (ReaderAction) -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.content,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        navigationIcon = {
            IconButton(onClick = { onAction(ReaderAction.OnBackClick) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.reader_back_description),
                    tint = colors.content,
                )
            }
        },
        actions = {
            IconButton(onClick = { onAction(ReaderAction.OnChapterListClick) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                    contentDescription = stringResource(Res.string.reader_chapters_description),
                    tint = colors.content,
                )
            }
            IconButton(onClick = { onAction(ReaderAction.OnSettingsClick) }) {
                Icon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = stringResource(Res.string.reader_settings_description),
                    tint = colors.content,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
    )
}

@Composable
private fun ReaderBody(
    state: ReaderState,
    book: ReadableBook?,
    chapter: ReadableChapter?,
    colors: ReaderColors,
    contentPadding: androidx.compose.foundation.layout.PaddingValues,
    onAction: (ReaderAction) -> Unit,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = colors.accent)
                    Text(
                        text = stringResource(Res.string.reader_loading),
                        modifier = Modifier.padding(top = BookNestSpacing.medium),
                        color = colors.content,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        book == null || chapter == null -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.reader_missing),
                    modifier = Modifier.padding(BookNestSpacing.section),
                    color = colors.contentMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        else -> {
            ReaderContent(
                state = state,
                book = book,
                chapter = chapter,
                colors = colors,
                contentPadding = contentPadding,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun ReaderContent(
    state: ReaderState,
    book: ReadableBook,
    chapter: ReadableChapter,
    colors: ReaderColors,
    contentPadding: androidx.compose.foundation.layout.PaddingValues,
    onAction: (ReaderAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val currentOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(state.scrollRequestId) {
        if (state.scrollRequestId > 0) {
            listState.scrollToItem(state.paragraphIndex + 1)
        }
    }
    LaunchedEffect(listState, chapter.id) {
        snapshotFlow { (listState.firstVisibleItemIndex - 1).coerceAtLeast(0) }
            .distinctUntilChanged()
            .collect { paragraphIndex ->
                currentOnAction(ReaderAction.OnVisibleParagraphChange(paragraphIndex))
            }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .background(colors.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .widthIn(
                        max = ReaderMaxContentWidth,
                    ).testTag(ReaderTestTags.READER_CONTENT),
            contentPadding =
                androidx.compose.foundation.layout.PaddingValues(
                    horizontal = BookNestSpacing.screenCompact,
                    vertical = BookNestSpacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.large),
        ) {
            item(key = "${chapter.id}-heading") {
                Column(verticalArrangement = Arrangement.spacedBy(BookNestSpacing.small)) {
                    Text(
                        text = book.title,
                        color = colors.contentMuted,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = chapter.title,
                        color = colors.content,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            items(
                count = chapter.paragraphs.size,
                key = { index -> chapter.paragraphs[index].id },
            ) { index ->
                Text(
                    text = chapter.paragraphs[index].text,
                    color = colors.content,
                    fontSize = (16f * state.fontSize.scale).sp,
                    lineHeight = (28f * state.fontSize.scale).sp,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item(key = "${chapter.id}-ending") { Spacer(modifier = Modifier.height(BookNestSpacing.extraLarge)) }
        }
    }
}

@Composable
private fun ReaderBottomBar(
    state: ReaderState,
    colors: ReaderColors,
    onAction: (ReaderAction) -> Unit,
) {
    val book = requireNotNull(state.book)
    val progressPercent = (state.overallProgress * 100).toInt()
    val progressDescription = stringResource(Res.string.reader_progress_description, progressPercent)
    Surface(color = colors.surface, tonalElevation = 2.dp) {
        Column(
            modifier =
                Modifier.fillMaxWidth().padding(
                    horizontal = BookNestSpacing.medium,
                    vertical = BookNestSpacing.small,
                ),
        ) {
            LinearProgressIndicator(
                progress = { state.overallProgress },
                modifier = Modifier.fillMaxWidth(),
                color = colors.accent,
                trackColor = colors.track,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    onClick = { onAction(ReaderAction.OnPreviousChapterClick) },
                    enabled = state.chapterIndex > 0,
                    modifier = Modifier.testTag(ReaderTestTags.PREVIOUS_CHAPTER),
                ) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = null)
                    Text(stringResource(Res.string.reader_previous_chapter))
                }
                Text(
                    text = "$progressPercent%",
                    color = colors.contentMuted,
                    style = MaterialTheme.typography.labelMedium,
                    modifier =
                        Modifier.semantics {
                            contentDescription = progressDescription
                        },
                )
                TextButton(
                    onClick = { onAction(ReaderAction.OnNextChapterClick) },
                    enabled = state.chapterIndex < book.chapters.lastIndex,
                    modifier = Modifier.testTag(ReaderTestTags.NEXT_CHAPTER),
                ) {
                    Text(stringResource(Res.string.reader_next_chapter))
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterListSheet(
    book: ReadableBook,
    selectedChapterIndex: Int,
    onAction: (ReaderAction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onAction(ReaderAction.OnChapterSheetDismiss) },
        modifier = Modifier.testTag(ReaderTestTags.CHAPTER_LIST),
    ) {
        Text(
            text = stringResource(Res.string.reader_chapters_title),
            modifier = Modifier.padding(horizontal = BookNestSpacing.large),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(BookNestSpacing.medium))
        book.chapters.forEachIndexed { index, chapter ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = BookNestSpacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = index == selectedChapterIndex,
                    onClick = { onAction(ReaderAction.OnChapterClick(index)) },
                )
                TextButton(
                    onClick = { onAction(ReaderAction.OnChapterClick(index)) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(chapter.title, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        Spacer(Modifier.height(BookNestSpacing.large))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderSettingsSheet(
    state: ReaderState,
    onAction: (ReaderAction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onAction(ReaderAction.OnSettingsSheetDismiss) },
        modifier = Modifier.testTag(ReaderTestTags.SETTINGS),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = BookNestSpacing.large),
            verticalArrangement = Arrangement.spacedBy(BookNestSpacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.reader_appearance_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = stringResource(Res.string.reader_font_size), style = MaterialTheme.typography.titleSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BookNestSpacing.small),
            ) {
                IconButton(onClick = { onAction(ReaderAction.OnDecreaseFontSizeClick) }) {
                    Icon(Icons.Outlined.Remove, stringResource(Res.string.reader_decrease_font_description))
                }
                Text(
                    text = "အ က ခ",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = (16f * state.fontSize.scale).sp,
                )
                IconButton(onClick = { onAction(ReaderAction.OnIncreaseFontSizeClick) }) {
                    Icon(Icons.Outlined.Add, stringResource(Res.string.reader_increase_font_description))
                }
            }
            HorizontalDivider()
            Text(text = stringResource(Res.string.reader_theme), style = MaterialTheme.typography.titleSmall)
            ReaderThemeMode.entries.forEach { themeMode ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.themeMode == themeMode,
                        onClick = { onAction(ReaderAction.OnThemeModeSelect(themeMode)) },
                    )
                    TextButton(
                        onClick = { onAction(ReaderAction.OnThemeModeSelect(themeMode)) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = stringResource(themeMode.labelResource()), modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Spacer(Modifier.height(BookNestSpacing.large))
        }
    }
}

@Preview
@Composable
private fun ReaderScreenPreview() {
    BookNestTheme {
        ReaderScreen(
            state =
                ReaderState(
                    isLoading = false,
                    book =
                        ReadableBook(
                            id = "preview",
                            title = "တောင်တန်းနောက်က နံနက်ခင်း",
                            author = "သီဟနိုင်",
                            chapters =
                                listOf(
                                    ReadableChapter(
                                        id = "chapter-1",
                                        title = "အခန်း (၁) — မြူကြားမှ လမ်းကလေး",
                                        paragraphs =
                                            listOf(
                                                ReadableParagraph(
                                                    "paragraph-1",
                                                    "မနက်ခင်းအလင်းရောင်က တောင်တန်းအစွန်းပေါ်သို့ ဖြည်းဖြည်းရောက်လာသည်။",
                                                ),
                                            ),
                                    ),
                                ),
                        ),
                ),
            onAction = {},
        )
    }
}
