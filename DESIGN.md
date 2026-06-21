# Muse Design System

> Muse Design System 是基于 Material Design 3 (M3) 的设计系统，为 Muse TTS App 提供统一的主题 Token、组件库和使用约束。

---

## 目录

- [概述](#概述)
- [模块结构](#模块结构)
- [设计 Token](#设计-token)
- [组件使用指南](#组件使用指南)
- [扩展约束](#扩展约束)
- [迁移指南（M2 → M3）](#迁移指南m2--m3)
- [维护与 Checklist](#维护与-checklist)

---

## 概述

### 目标

1. **一致性** — 所有页面共享同一套颜色、字体、间距、形状体系
2. **可维护性** — 业务代码不再硬编码视觉常量，全部通过 Token/组件引用
3. **渐进迁移** — 封装层让 M2→M3 迁移可以逐页面进行，无需一次性重写

### 架构分层

```
┌──────────────────────────────────────────────┐
│              业务层 (muse module)              │
│  DashboardScreen, EditorScreen, …            │
├──────────────────────────────────────────────┤
│          设计系统层 (designsystem module)       │
│  ┌──────────┐ ┌──────────────────────────┐   │
│  │ Muse*     │ │ Foundation                │   │
│  │ 组件封装   │ │ ComponentDefaults        │   │
│  │ (12 件)   │ │ ModifierExt              │   │
│  └──────────┘ └──────────────────────────┘   │
│  ┌──────────────────────────────────────┐    │
│  │ Theme                                 │    │
│  │ MuseTheme, MuseColor, MuseTypography  │    │
│  │ MuseShapes, MuseSpacing, MuseElevation│    │
│  └──────────────────────────────────────┘    │
├──────────────────────────────────────────────┤
│         底层 M3 (jetpack compose material3)    │
└──────────────────────────────────────────────┘
```

### 设计原则

- **Signal alignment** — 组件签名与 M3 原始组件保持一致，降低学习成本
- **Token internalization** — 颜色、间距、形状通过 Token 引用，不在组件内硬编码
- **Modifier passthrough** — 所有组件接受 `Modifier` 参数
- **Default convergence** — 常用默认值集中在组件内部设定（如 TopAppBar `elevation=0`）
- **Zero business logic** — 组件是纯 UI 表现封装，不包含业务逻辑

---

## 模块结构

### `designsystem` 模块

```
designsystem/src/commonMain/kotlin/io/github/kkoshin/muse/designsystem/
├── theme/                # 设计 Token
│   ├── AppTheme.kt       # MuseTheme — 根主题 Composable
│   ├── Color.kt          # MuseColor — 语义色 + 扩展色
│   ├── Elevation.kt      # MuseElevation — 海拔 token
│   ├── Shape.kt          # MuseShapes + MuseCorner — 圆角 token
│   ├── Spacing.kt        # MuseSpacing — 间距 token
│   └── Typography.kt     # MuseTypography — 字阶
├── foundation/           # 基础工具
│   ├── ComponentDefaults.kt  # 组件默认值（动画时长、触摸目标等）
│   └── ModifierExt.kt        # Modifier 扩展（musePadding 等）
├── component/            # 组件封装（12 个）
│   ├── MuseTopAppBar.kt
│   ├── MuseScaffold.kt
│   ├── MuseButton.kt
│   ├── MuseIconButton.kt
│   ├── MuseSwitch.kt
│   ├── MuseTextField.kt
│   ├── MuseCard.kt
│   ├── MuseDialog.kt
│   ├── MuseChip.kt
│   ├── MuseSlider.kt
│   ├── MuseTabRow.kt
│   └── MuseProgressIndicator.kt
└── index/
    └── DesignSystemIndex.kt  # 组件索引文档
```

### 使用方式

```kotlin
import io.github.kkoshin.muse.designsystem.theme.MuseTheme
import io.github.kkoshin.muse.designsystem.component.MuseScaffold

// 在根节点使用：
MuseTheme {
    MuseScaffold(...) { ... }
}
```

> 注：业务代码通过 `AppTheme` 使用设计系统，无需直接引用 `MuseTheme`。
> `AppTheme` 是 `MuseTheme` + `LocalToaster` DI 注入的薄封装。

---

## 设计 Token

### 颜色体系

采用**两层结构**：私有调色板 → 公开语义色。

#### 调色板（Palette — 内部使用）

| Token | Light | Dark |
|-------|-------|------|
| Blue50 | `#E3F2FD` | — |
| Blue100 | `#BBDEFB` | — |
| Blue200 | `#90CAF9` | — |
| Blue300 | `#64B5F6` | — |
| Blue400 | `#42A5F5` | — |
| Blue500 | `#5D9CED` | — |
| Blue600 | `#4A8CD8` | — |
| Blue700 | `#3A7CC4` | — |
| Blue800 | `#2A6CB0` | — |
| Blue900 | `#1A5C9C` | — |
| Neutral0 | `#000000` | — |
| Neutral10 | `#1C1C1E` | — |
| Neutral20 | `#2C2C2E` | — |
| Neutral50 | `#48484A` | — |
| Neutral80 | `#8E8E93` | — |
| Neutral90 | `#AEAEB2` | — |
| Neutral95 | `#D1D1D6` | — |
| Neutral99 | `#F5F5F7` | — |
| Neutral100 | `#FFFFFF` | — |
| Red500 | `#FF3B30` | — |

#### 语义色（Light / Dark）

| Token | Light | Dark |
|-------|-------|------|
| `primary` | `#5D9CED` | `#64B5F6` |
| `onPrimary` | `#FFFFFF` | `#1C1C1E` |
| `primaryContainer` | `#BBDEFB` | `#3A7CC4` |
| `onPrimaryContainer` | `#1A5C9C` | `#BBDEFB` |
| `secondary` | `#64B5F6` | `#90CAF9` |
| `onSecondary` | `#FFFFFF` | `#1C1C1E` |
| `secondaryContainer` | `#E3F2FD` | `#2A6CB0` |
| `onSecondaryContainer` | `#2A6CB0` | `#E3F2FD` |
| `background` | `#F5F5F7` | `#1C1C1E` |
| `onBackground` | `#1C1C1E` | `#F5F5F7` |
| `surface` | `#FFFFFF` | `#2C2C2E` |
| `onSurface` | `#1C1C1E` | `#F5F5F7` |
| `surfaceVariant` | `#D1D1D6` | `#48484A` |
| `onSurfaceVariant` | `#48484A` | `#AEAEB2` |
| `error` | `#FF3B30` | `#FF6961` |
| `onError` | `#FFFFFF` | `#1C1C1E` |
| `outline` | `#AEAEB2` | `#8E8E93` |
| `outlineVariant` | `#D1D1D6` | `#48484A` |

#### 扩展语义色

| Token | Light | Dark | 替代 |
|-------|-------|------|------|
| `secondaryText` | `#8E8E93` | `#AEAEB2` | `onSurface.copy(alpha = 0.5f)` |
| `divider` | `#D1D1D6` | `#48484A` | `onSurface.copy(alpha = 0.12f)` |
| `highlightBg` | `#1A5D9CED` | `#1A64B5F6` | `primary.copy(alpha = 0.1f)` |
| `disabledText` | `#AEAEB2` | `#48484A` | `onBackground.copy(alpha = 0.5f)` |
| `scrim` | `#66000000` | `#99000000` | `Color.Black.copy(alpha = 0.4f)` |

> **规则**：业务代码中禁止出现 `Color(...)` 或 `.copy(alpha = ...)` 的硬编码。
> 所有颜色引用应通过 `MuseColor` 或 `MaterialTheme.colorScheme` 获取。

---

### 字体层级 Typography

```kotlin
// 通过 MuseTheme 提供：
Text("Display", style = MaterialTheme.typography.displayLarge)
Text("Headline", style = MaterialTheme.typography.headlineMedium)  
Text("Title",   style = MaterialTheme.typography.titleMedium)
Text("Body",    style = MaterialTheme.typography.bodyLarge)
Text("Label",   style = MaterialTheme.typography.labelMedium)
```

| Style | Weight | Size | Line Height | Letter Spacing |
|-------|--------|------|-------------|----------------|
| `displayLarge` | Normal | 57.sp | 64.sp | -0.25.sp |
| `displayMedium` | Normal | 45.sp | 52.sp | — |
| `displaySmall` | Normal | 36.sp | 44.sp | — |
| `headlineLarge` | Normal | 32.sp | 40.sp | — |
| `headlineMedium` | Normal | 28.sp | 36.sp | — |
| `headlineSmall` | Normal | 24.sp | 32.sp | — |
| `titleLarge` | Normal | 22.sp | 28.sp | — |
| `titleMedium` | Medium | 16.sp | 24.sp | 0.15.sp |
| `titleSmall` | Medium | 14.sp | 20.sp | 0.1.sp |
| `bodyLarge` | Normal | 16.sp | 24.sp | 0.5.sp |
| `bodyMedium` | Normal | 14.sp | 20.sp | 0.25.sp |
| `bodySmall` | Normal | 12.sp | 16.sp | 0.4.sp |
| `labelLarge` | Medium | 14.sp | 20.sp | 0.1.sp |
| `labelMedium` | Medium | 12.sp | 16.sp | 0.5.sp |
| `labelSmall` | Medium | 11.sp | 16.sp | 0.5.sp |

---

### 间距系统 Spacing

| Token | dp | 用途 |
|-------|----|------|
| `MuseSpacing.xs` | 4.dp | 紧密内边距，图标间距 |
| `MuseSpacing.sm` | 8.dp | 默认内边距，小组件间距 |
| `MuseSpacing.md` | 12.dp | 中等间距，卡片内边距 |
| `MuseSpacing.lg` | 16.dp | 标准外边距，列表项间距 |
| `MuseSpacing.xl` | 24.dp | 区块间距，大外边距 |
| `MuseSpacing.xxl` | 32.dp | 主要区块分隔 |
| `MuseSpacing.xxxl` | 48.dp | 超大间距 |

```kotlin
Modifier.padding(MuseSpacing.lg)     // ✅ 正确
Modifier.padding(16.dp)              // ❌ 禁止（除非有明确理由）
```

---

### 形状/圆角 Shape

```kotlin
// 通过 MuseTheme 提供（MaterialTheme.shapes）
MaterialTheme.shapes.small     // 8.dp round
MaterialTheme.shapes.medium    // 12.dp round

// 独立使用（非 MaterialTheme 上下文）
MuseCorner.small               // 8.dp
MuseCorner.full                // 50% (pill shape)
```

| Token | 值 | 用途 |
|-------|----|------|
| `MuseCorner.none` | `0.dp` | 直角 |
| `MuseCorner.extraSmall` | `4.dp` | 极细微圆角 |
| `MuseCorner.small` / `MuseShapes.small` | `8.dp` | 卡片/输入框 |
| `MuseCorner.medium` / `MuseShapes.medium` | `12.dp` | 卡片/对话框 |
| `MuseCorner.large` / `MuseShapes.large` | `16.dp` | 大卡片 |
| `MuseCorner.extraLarge` / `MuseShapes.extraLarge` | `24.dp` | 超大圆角 |
| `MuseCorner.full` | `50%` | Pill/Capsule 形状 |

---

### 海拔/阴影 Elevation

| Token | dp | 用途 |
|-------|----|------|
| `MuseElevation.none` | 0.dp | 平面（TopAppBar, 静态卡片） |
| `MuseElevation.low` | 2.dp | 微弱提升（常规卡片） |
| `MuseElevation.medium` | 4.dp | 中等（Floating 元素） |
| `MuseElevation.high` | 8.dp | 偏高（BottomSheet, Dialog） |
| `MuseElevation.highest` | 12.dp | 最高（active FAB） |

---

## 组件使用指南

### 通用规则

```kotlin
import io.github.kkoshin.muse.designsystem.component.MuseButton
import io.github.kkoshin.muse.designsystem.component.MuseTopAppBar
```

> **避免**：不直接导入 M3 原始组件（`androidx.compose.material3.Button`），除非 DS 未提供封装。

---

### P0 组件（高频使用）

#### MuseTopAppBar

```kotlin
MuseTopAppBar(
    title = { Text("Title") },
    navigationIcon = {
        MuseIconButton(onClick = {}) {
            Icon(Icons.Default.ArrowBack, "back")
        }
    },
    actions = {
        MuseIconButton(onClick = {}) {
            Icon(Icons.Default.Settings, "settings")
        }
    },
)
```

- 默认 `containerColor = surface`（平底）
- 默认 `scrollBehavior = null`（无滚动隐藏）
- `@OptIn(ExperimentalMaterial3Api::class)` 需要

#### MuseScaffold

```kotlin
MuseScaffold(
    topBar = { MuseTopAppBar(title = { Text("Title") }) },
    floatingActionButton = { ... },
) { padding ->
    // content
}
```

- 默认 `contentWindowInsets = WindowInsets.systemBars`
- 无需在每个页面重复设置

#### MuseButton / MuseOutlinedButton / MuseTextButton

```kotlin
MuseButton(onClick = {}) { Text("Filled") }
MuseOutlinedButton(onClick = {}) { Text("Outlined") }
MuseTextButton(onClick = {}) { Text("Text") }
```

- 颜色通过 `MuseTheme` 自动注入
- 如需自定义颜色：传入 `colors = ButtonDefaults.buttonColors(containerColor = ...)`

---

### P1 组件（中频使用）

#### MuseIconButton

```kotlin
MuseIconButton(onClick = {}) {
    Icon(Icons.Default.Settings, "settings")
}
```

- 默认 48.dp 触摸目标

#### MuseSwitch

```kotlin
var checked by remember { mutableStateOf(false) }
MuseSwitch(checked = checked, onCheckedChange = { checked = it })
```

#### MuseOutlinedTextField

```kotlin
MuseOutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Label") },
    singleLine = true,
)
```

- `@OptIn(ExperimentalMaterial3Api::class)` 需要

#### MuseCard

```kotlin
MuseCard(modifier = Modifier.fillMaxWidth()) {
    Text("Content", modifier = Modifier.musePadding())
}
```

- 默认 `elevation = MuseElevation.low`（2.dp）

---

### P2 组件（低频使用）

#### MuseAlertDialog

```kotlin
MuseAlertDialog(
    onDismissRequest = { /* close */ },
    title = { Text("Title") },
    text = { Text("Body") },
    confirmButton = { MuseButton(onClick = {}) { Text("OK") } },
    dismissButton = { MuseTextButton(onClick = {}) { Text("Cancel") } },
)
```

- 默认 `usePlatformDefaultWidth = false`（防止移动端过窄）

#### MuseFilterChip

```kotlin
var selected by remember { mutableStateOf(false) }
MuseFilterChip(
    selected = selected,
    onClick = { selected = !selected },
    label = { Text("Option") },
)
```

#### MuseSlider

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }
MuseSlider(value = value, onValueChange = { value = it })
```

#### MuseTabRow + MuseTab

```kotlin
var tabIndex by remember { mutableIntStateOf(0) }
MuseTabRow(selectedTabIndex = tabIndex) {
    MuseTab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Tab A") })
    MuseTab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Tab B") })
}
```

#### MuseCircularProgressIndicator

```kotlin
MuseCircularProgressIndicator()                        // 不确定模式
MuseCircularProgressIndicator(progress = 0.65f)        // 确定模式
```

---

### 常见组合模式

```kotlin
// Scaffold + TopAppBar
MuseScaffold(
    topBar = {
        MuseTopAppBar(
            title = { Text("Screen") },
            navigationIcon = { AppBackButton() },
        )
    },
) { padding ->
    Box(Modifier.padding(padding)) {
        // 内容区域
    }
}
```

---

## 扩展约束

### 如何新增组件

1. 在 `designsystem/.../component/` 下新建 `Muse*.kt`
2. 包装 M3 对应组件，签名保持对齐
3. 默认值引用 Token（`MuseColor.*`、`MuseSpacing.*`、`MuseShapes.*`、`MuseElevation.*`）
4. 更新 `DesignSystemIndex.kt` 添加组件条目
5. 在 `DesignSystemPreview.kt` 添加示例

### Token 扩展流程

1. 在 `Color.kt` / `Spacing.kt` / 等中添加新 Token
2. 更新 `MuseTheme` 若需注入
3. 跑 Playground 验证

### 禁止事项

| ❌ 禁止 | ✅ 替代 |
|---------|---------|
| 直接使用 `androidx.compose.material3.Button` | 使用 `MuseButton` |
| 硬编码 `Color(0xFF....)` | 使用 `MuseColor.*` 或 `MaterialTheme.colorScheme.*` |
| 硬编码 `.dp` 间距 | 使用 `MuseSpacing.*` |
| 硬编码 `RoundedCornerShape(x.dp)` | 使用 `MuseCorner.*` 或 `MaterialTheme.shapes.*` |
| 使用 `alpha = 0.5f` 等 | 使用扩展语义色（如 `MuseColor.secondaryText`） |
| 组件内包含业务逻辑 | 保持纯 UI 表现封装 |

---

## 迁移指南（M2 → M3）

### M2 → M3 组件映射

| M2 组件 | DS 替换组件 | 状态 |
|---------|-------------|------|
| `TopAppBar` | `MuseTopAppBar` | ✅ 已迁移 |
| `Scaffold` | `MuseScaffold` | ✅ 已迁移 |
| `Button` | `MuseButton` | ✅ 已迁移 |
| `OutlinedButton` | `MuseOutlinedButton` | ✅ 已迁移 |
| `TextButton` | `MuseTextButton` | ✅ 已迁移 |
| `IconButton` | `MuseIconButton` | ✅ 已迁移 |
| `Switch` | `MuseSwitch` | ✅ 已迁移 |
| `Slider` | `MuseSlider` | ✅ 已迁移 |
| `CircularProgressIndicator` | `MuseCircularProgressIndicator` | ✅ 已迁移 |
| `OutlinedTextField` | `MuseOutlinedTextField` | ✅ 已迁移 |
| `Card` | `MuseCard` | ✅ 已迁移 |
| `AlertDialog` | `MuseAlertDialog` | ✅ 已迁移 |
| `Chip` (Filter) | `MuseFilterChip` | ✅ 已迁移 |
| `TabRow` / `Tab` | `MuseTabRow` / `MuseTab` | ✅ 已迁移 |
| `Text` | `androidx.compose.material3.Text` | ✅ 已迁移 |
| `Icon` | `androidx.compose.material3.Icon` | ✅ 已迁移 |
| `FloatingActionButton` | `androidx.compose.material3.FloatingActionButton` | ✅ 已迁移 |
| `Surface` | `androidx.compose.material3.Surface` | ✅ 已迁移 |
| `SwipeToDismiss` | `androidx.compose.material3.SwipeToDismissBox` | ⏳ 待评估 |

### M2 → M3 主题映射

```kotlin
// Color
MaterialTheme.colors.surface      → MaterialTheme.colorScheme.surface
MaterialTheme.colors.primary      → MaterialTheme.colorScheme.primary
MaterialTheme.colors.onSurface    → MaterialTheme.colorScheme.onSurface
MaterialTheme.colors.background   → MaterialTheme.colorScheme.background
MaterialTheme.colors.error        → MaterialTheme.colorScheme.error
MaterialTheme.colors.isLight      → !isSystemInDarkTheme()

// Typography
MaterialTheme.typography.h6       → MaterialTheme.typography.titleMedium
MaterialTheme.typography.subtitle1 → MaterialTheme.typography.titleMedium
MaterialTheme.typography.body1    → MaterialTheme.typography.bodyLarge
MaterialTheme.typography.body2    → MaterialTheme.typography.bodyMedium
MaterialTheme.typography.caption  → MaterialTheme.typography.bodySmall
MaterialTheme.typography.button   → MaterialTheme.typography.labelLarge
MaterialTheme.typography.overline → MaterialTheme.typography.labelSmall

// Button/Switch/Slider colors
M2 backgroundColor               → M3 containerColor
M2 disabledBackgroundColor       → M3 disabledContainerColor
M2 contentColor                  → M3 contentColor (通常不变)
```

### 渐进迁移步骤

1. **AppTheme 改用 MuseTheme** — 已完成（`AppTheme` 现为 `MuseTheme` 的薄封装）
2. **逐页面替换 M2 组件** — 已完成（9 个页面已全部迁移）
3. **处理特殊组件** — ⏳ `SwipeToDismiss` 保留 M2，后续单独评估
4. **移除 M2 依赖** — ⏳ SwipeToDismiss 迁移后，移除 `muse/build.gradle.kts` 中的 `compose.material`

---

## 维护与 Checklist

### 新增页面时的 Checklist

- [ ] `MuseTheme` 已由 `AppTheme` 提供（无需手动添加）
- [ ] 使用 `MuseScaffold` + `MuseTopAppBar` 作为布局骨架
- [ ] 使用 `MuseButton` 系列替代原生 Button
- [ ] 间距使用 `MuseSpacing.*`
- [ ] 圆角使用 `MuseShapes.*` 或 `MuseCorner.*`
- [ ] 颜色通过 `MaterialTheme.colorScheme.*` 引用
- [ ] 无 `import androidx.compose.material.*`（保留 SwipeToDismiss 除外）
- [ ] 无 `Color(...)` 或 `.copy(alpha = ...)` 硬编码

### Code Review Checklist

- [ ] 是否有硬编码的视觉常量（颜色、间距、圆角）？
- [ ] 是否直接 import 了 M3 原始组件而非 DS 封装？
- [ ] 是否使用了 M2 `MaterialTheme.colors.*` 而非 M3 `colorScheme.*`？
- [ ] 新组件是否更新了 `DesignSystemIndex.kt`？

### 版本发布

设计系统版本与 App 版本同步。修改 Token 或组件后需：
1. 跑 `./gradlew :muse:test` 确保无回归
2. 跑 Playground 预览验证视觉变化
3. 更新 `DESIGN.md` 中对应章节
