package io.github.kkoshin.muse.feature.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.chipColors
import com.mikepenz.aboutlibraries.ui.compose.libraryColors
import io.github.kkoshin.muse.designsystem.component.ScreenScaffold
import io.github.kkoshin.muse.designsystem.theme.AppTheme
import kotlinx.collections.immutable.toImmutableList

@Composable
actual fun OpenSourceScreen(modifier: Modifier, onOpenURL: (String) -> Unit) {
    val libs by produceLibraries(io.github.kkoshin.muse.R.raw.aboutlibraries)

    ScreenScaffold(
        modifier = modifier,
        title = "Open Source",
        content = { contentPadding, scrollBehavior ->
            libs?.let { loadedLibs ->
                LibrariesContainer(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior!!.nestedScrollConnection),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    colors = LibraryDefaults.libraryColors(
                        libraryBackgroundColor = AppTheme.colorScheme.background,
                        libraryContentColor = AppTheme.colorScheme.onBackground,
                        versionChipColors = LibraryDefaults.chipColors(
                            containerColor = AppTheme.colorScheme.background,
                            contentColor = AppTheme.colorScheme.onBackground,
                        )
                    ),
                    libraries = loadedLibs.copy(
                        libraries = loadedLibs.libraries
                            .filterNot {
                                // ignore androidx and kotlin libraries
                                it.uniqueId.startsWith("androidx.") || it.uniqueId.startsWith("org.jetbrains.kotlin")
                            }.toImmutableList(),
                    ),
                    onLibraryClick = { lib ->
                        lib.website?.let {
                            onOpenURL(it)
                        }
                    },
                )
            }
        },
    )
}