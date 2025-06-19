package io.github.kkoshin.muse.platformbridge

import androidx.compose.runtime.Composable

/**
 * Handles the system back button. Support Android only.
 *
 * Calling this in your composable adds the given lambda to the [OnBackPressedDispatcher] of the
 * [LocalOnBackPressedDispatcherOwner].
 *
 * If this is called by nested composables, if enabled, the inner most composable will consume
 * the call to system back and invoke its lambda. The call will continue to propagate up until it
 * finds an enabled BackHandler.
 *
 * @sample androidx.activity.compose.samples.BackHandler
 *
 * @param enabled if this BackHandler should be enabled
 * @param onBack the action invoked by pressing the system back
 */
@Composable
expect fun BackHandler(enable: Boolean = true, onBack: () -> Unit)