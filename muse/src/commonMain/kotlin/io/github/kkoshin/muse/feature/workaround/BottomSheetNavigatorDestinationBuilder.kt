package io.github.kkoshin.muse.feature.workaround

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavDestinationDsl
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.get
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import kotlin.reflect.KClass
import kotlin.reflect.KType

// 待 compose 版本升上去后删除
@OptIn(ExperimentalMaterialNavigationApi::class)
inline fun <reified T : Any> NavGraphBuilder.bottomSheet(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    destination(
        BottomSheetNavigatorDestinationBuilder(
            provider[BottomSheetNavigator::class],
            T::class,
            typeMap,
            content,
        ).apply { deepLinks.forEach { deepLink -> deepLink(deepLink) } },
    )
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@NavDestinationDsl
class BottomSheetNavigatorDestinationBuilder : NavDestinationBuilder<BottomSheetNavigator.Destination> {
    private val bottomSheetNavigator: BottomSheetNavigator
    private val content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit

    /**
     * DSL for constructing a new [DialogNavigator.Destination]
     *
     * @param navigator navigator used to create the destination
     * @param route the destination's unique route
     * @param dialogProperties properties that should be passed to
     *   [androidx.compose.ui.window.Dialog].
     * @param content composable for the destination
     */
    constructor(
        navigator: BottomSheetNavigator,
        route: String,
        content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
    ) : super(navigator, route) {
        this.bottomSheetNavigator = navigator
        this.content = content
    }

    /**
     * DSL for constructing a new [DialogNavigator.Destination]
     *
     * @param navigator navigator used to create the destination
     * @param route the destination's unique route from a [KClass]
     * @param typeMap map of destination arguments' kotlin type [KType] to its respective custom
     *   [NavType]. May be empty if [route] does not use custom NavTypes.
     * @param dialogProperties properties that should be passed to
     *   [androidx.compose.ui.window.Dialog].
     * @param content composable for the destination
     */
    constructor(
        navigator: BottomSheetNavigator,
        route: KClass<*>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
        content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
    ) : super(navigator, route, typeMap) {
        this.bottomSheetNavigator = navigator
        this.content = content
    }

    override fun instantiateDestination(): BottomSheetNavigator.Destination =
        BottomSheetNavigator.Destination(bottomSheetNavigator, content)
}