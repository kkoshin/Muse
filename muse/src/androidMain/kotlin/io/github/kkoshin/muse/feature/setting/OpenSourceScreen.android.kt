package io.github.kkoshin.muse.feature.setting

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import kotlinx.collections.immutable.toImmutableList
import museroot.muse.generated.resources.Res

@Composable
actual fun OpenSourceScreen(modifier: Modifier, onOpenURL: (String) -> Unit) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val libs by produceLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.statusBars,
                backgroundColor = MaterialTheme.colors.surface,
                navigationIcon = {
                    IconButton(onClick = {
                        backPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = "Open Source")
                },
            )
        },
        content = { contentPadding ->
            libs?.let { loadedLibs ->
                LibrariesContainer(
                    libraries = loadedLibs.copy(
                        libraries = loadedLibs.libraries
                            .filterNot {
                                // ignore androidx and kotlin libraries
                                it.uniqueId.startsWith("androidx.") || it.uniqueId.startsWith("org.jetbrains.kotlin")
                            }.toImmutableList(),
                    ),
                    modifier = Modifier.padding(contentPadding).fillMaxSize(),
                    onLibraryClick = { library ->
                        library.website?.let {
                            onOpenURL(it)
                        }
                    },
                )
            }
        },
    )
}
