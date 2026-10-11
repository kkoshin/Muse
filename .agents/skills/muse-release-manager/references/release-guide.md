# Muse 发布验证清单

本清单保存长期适用的发布检查项。每次发布的构建结果、测试设备、系统版本和遗留问题记录在对应 PR 或发布说明中。

## 发布准备

- 确认 `composeApp/build.gradle.kts` 中的版本配置。每次上传新的 Android 包都需使用更大的 `versionCode`；`versionName` 按发布内容决定，仅修复上架兼容性时可以保留。
- 如果修改 `versionName`，同步 `muse/build.gradle.kts` 中的 CocoaPods 版本。
- 在 `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt` 中记录此次发布的变化；商店界面有变化时更新截图。
- 如果涉及数据库结构，检查 schema 版本和迁移。
- 提交前核对 Play Console 当前的 target API 要求及应用的提交资格。

## 构建与产物检查

Android 发布候选包的基础验证：

```bash
./gradlew :muse:testReleaseUnitTest :composeApp:bundlePlay
```

- 检查构建、测试、R8 和 lint 结果，将需要跟进的警告记录在 PR 中。
- 使用 `bundletool validate` 检查最终 Play AAB，并通过其 manifest 确认应用 ID、SDK 和版本信息。
- Play 使用 `play` build type：本地 AAB 为 `composeApp/build/outputs/bundle/play/composeApp-play.aab`，对应映射文件为 `composeApp/build/outputs/mapping/play/mapping.txt`。
- 确认 Play 包不包含 xCrash；发布流水线会检查归档和 dex 中的 xCrash 内容。`release` build type 保留 xCrash，用于 GitHub Release / F-Droid。
- 涉及原生库或打包工具链变化时，检查最终包的 16 KB 页面兼容性，并在 16 KB 页面设备或模拟器上验证。ELF 对齐检查不能替代运行测试。
- 如果涉及 iOS，构建对应的 framework，并在 iOS 设备或模拟器上验证受影响的功能。

## Android 手动回归

使用 Play 发布候选版本，在目标 Android 版本和前一个主要版本上验证。升级到 target API 36 时，覆盖 API 36 和 API 35。

- [ ] 主页面和子页面导航正常；预测性返回手势能够取消和完成。
- [ ] 应用弹窗、系统权限提示可见且可操作，边到边布局没有遮挡。
- [ ] 返回键可关闭底部弹层；键盘展开和收起时，顶部栏及底部操作区域正常。
- [ ] 手势导航和三键导航下的系统边距正常。
- [ ] 音频生成、播放、导出和分享成功，导出的文件可重新播放。
- [ ] 平板旋转、分屏及窗口缩放后，页面状态和上述核心操作正常。

记录设备或模拟器型号、系统版本、测试结果和未完成项。构建成功不代表这些运行检查已完成。

## 发布与渠道验证

- 将发布变更合入主分支。需要新 GitHub Release 时使用对应版本 tag；仅增加 `versionCode` 的 Play 更新可以手动运行发布工作流，无需重复创建同名 tag。
- 手动运行 `.github/workflows/package.yml`，或创建 GitHub Release 触发构建；单独推送 tag 不会触发该工作流。
- 从工作流下载已使用 Play 上传密钥签名的 `play-aab` 产物及其 `mapping.txt`，上传内部测试轨，并验证实际安装的版本。
- 检查 Play Console 的兼容性报告及 Android vitals，完成内部测试后发布正式版。

签名配置、商店资料和 Play Console 操作见 [Google Play 上架指南](play-store-guide.md)。
