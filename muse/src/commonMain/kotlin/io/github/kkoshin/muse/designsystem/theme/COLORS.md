# Miuix 用色规范

## 设计原则

miuix 的颜色体系围绕**单一品牌色 + 灰阶分层**设计，不遵循 Material Design 的 multi-color 体系。理解这一点是正确用色的前提。

## 颜色类别

### Primary（品牌色）

primary 是整个 UI 中**唯一的有彩色**，用于表达活跃/选中/强调状态。

| 颜色槽 | 角色 |
|---|---|
| `primary` | 品牌强调色 — Switch 选中轨道、Button 填充、Slider 进度 |
| `primaryVariant` | primary 的变体色 |
| `primaryContainer` | primary 的容器底色 |
| `onPrimary` | primary 上的文字/图标色 |
| `onPrimaryContainer` | primaryContainer 上的文字色 |

不要将 secondary/tertiary 当作另一个品牌色使用。

### Secondary（容器色系）

secondary 及其家族**全部是中性灰底**，不是强调色。这是和 Material 最核心的区别。

| 颜色槽 | 亮色值 | 角色 |
|---|---|---|
| `secondary` | `#E6E6E6` | 非活跃态底色 — Checkbox 未选中、Switch 关闭轨道 |
| `secondaryVariant` | `#F0F0F0` | Button 默认背景、次要容器 |
| `secondaryContainer` | `#F0F0F0` | TextField 背景、ProgressIndicator 背景 |
| `onSecondary` | `#FFF` | secondary 上的文字色 |
| `onSecondaryVariant` | `#303030` | secondaryVariant 上的文字色 |

不要在表达"强调"意图时使用 secondary。

### 背景层级（由底到面）

```
background (#FFF)           → 页面最底层
  └ surface (#F7F7F7)       → 列表/内容区背景
      └ surfaceVariant (#FFF) → 卡片/容器
          └ surfaceContainerHigh (#E8E8E8) → 浮层底部栏
```

| 颜色槽 | 亮色 | 暗色 | 用于 |
|---|---|---|---|
| `background` | `#FFF` | `#242424` | 页面根背景 |
| `surface` | `#F7F7F7` | `#000` | 列表/内容区 |
| `surfaceVariant` | `#FFF` | `#242424` | 卡片/容器 |
| `surfaceContainer` | `#FFF` | `#242424` | 白色容器 |
| `surfaceContainerHigh` | `#E8E8E8` | `#242424` | 浮层底部栏、高于 surface 的层级 |
| `surfaceContainerHighest` | `#E8E8E8` | `#2D2D2D` | 最高层级容器 |

### 文字层级

miuix 的文字层级靠**透明度**区分，而非色相：

```
onSurface            (#000 100%)   → 主标题
onSurfaceSecondary   (#000 80%)    → 次要文字
surfaceVariantSummary (#000 60%)   → 摘要/说明文字
surfaceVariantActions (#000 40%)   → 操作辅助提示
onBackgroundVariant  (#8C93B0)     → 灰色辅助文字
```

### Error

| 颜色槽 | 角色 |
|---|---|
| `error` | 错误状态色 |
| `errorContainer` | 错误容器底色 |
| `onError` | error 上的文字色 |

### 边框与分割线

| 颜色槽 | 亮色 | 用于 |
|---|---|---|
| `outline` | `#D9D9D9` | 控件边框 |
| `dividerLine` | `#E0E0E0` | 列表分割线 |

### 特殊用途

| 颜色槽 | 用于 |
|---|---|
| `windowDimming` | Dialog/Dropdown/BottomSheet 背景遮罩 |
| `sliderKeyPoint` | Slider 刻度点 |
| `sliderKeyPointForeground` | Slider 刻度点前景 |

## 自定义组件用色指引

当需要为自定义组件选择颜色时，按以下优先级判断：

1. **品牌色意图** → `primary` / `primaryContainer`
2. **表面层级** → `surface` → `surfaceContainerHigh` → `surfaceContainerHighest`
3. **中性容器底** → `secondary` / `secondaryVariant` / `secondaryContainer`
4. **边框** → `outline` / `dividerLine`
5. **文字** → 按层级选对应 `onSurface*`

### 常见场景

| 场景 | 推荐色 |
|---|---|
| 列表项背景 | 不设背景，继承 surface |
| 分组标题背景 | `onBackground.copy(alpha = 0.08f)` |
| 底部操作栏 | `surfaceContainerHigh` |
| 卡片/容器 | `surfaceVariant` |
| 输入框背景 | `secondaryContainer` |
| 选中态底色 | `primary` |
| 未选中态底色 | `secondary` |
| 浮层遮罩 | `windowDimming` |
| 分割线 | `dividerLine` |

## 暗色模式

暗色模式下各颜色值自动翻转，不要手动指定暗色值。所有颜色通过 `AppTheme.colorScheme.*` 读取即可自动适配亮暗。

## 禁用态

miuix 对禁用态有细粒度控制：

```
disabledPrimary         → Switch/通用禁用
disabledPrimaryButton   → Button 禁用
disabledPrimarySlider   → Slider 禁用
disabledSecondary       → 次要元素禁用
disabledOnSurface       → 表面文字禁用
```

优先使用组件自带的 disable 参数，仅在自定义组件时引用这些色值。
