# Google Play 上架指南

> 范围：把 Muse 上架到 Google Play 并完成发版。代码侧已备好 AAB 构建/签名（`package.yml`）与隐私政策（`PRIVACY_POLICY.md`）；本指南覆盖 **GitHub Secrets** 与 **Play Console 手动操作**。

## 一、GitHub Secrets（发布流水线需要）

Play 上传密钥与 GitHub Release APK 的签名密钥（`KEY_STORE_*`）是**两套**，不要混用：

| Secret | 说明 |
|---|---|
| `PLAY_KEYSTORE_BASE64` | Play 上传 keystore 文件的 Base64（整文件编码为单行） |
| `PLAY_KEYSTORE_PASSWORD` | keystore 口令 |
| `PLAY_KEY_ALIAS` | key alias |
| `PLAY_KEY_PASSWORD` | key 口令。**PKCS12 keystore 必须与 store 口令相同**（keytool 不支持分开）；留空则默认用 store 口令 |

生成 Base64：

```bash
# macOS
base64 -i play-upload.jks | tr -d '\n' | pbcopy
# Linux
base64 -w0 play-upload.jks
```

> keystore 与口令请自行保管：**不要**提交进仓库，不要发给任何人（包括 AI agent）。仓库 Settings → Secrets and variables → Actions 中添加即可。

## 二、Play Console 一次性配置

在 [Play Console](https://play.google.com/console) 创建应用（名称 Muse、语言 English（美国）、免费、类别建议 Tools 或 Productivity、联系邮箱）后完成以下配置：

1. **Play App Signing**（测试和发布 → 应用完整性）：按引导注册，选择 **Google 托管的应用签名密钥**（推荐）。上面的 keystore 作为**上传密钥（upload key）**，首次上传 AAB 后自动登记。
2. **商店首页素材**（仓库已备好，路径 `fastlane/metadata/android/en-US/`）：
   - 应用图标 512×512：`images/icon.png`
   - Feature graphic 1024×500：`images/featureGraphic.png`
   - 手机截图 6 张 1080×2400：`images/phoneScreenshots/*.jpg`
   - 标题 / 简介 / 详细说明：`title.txt` / `short_description.txt` / `full_description.txt`
3. **隐私政策 URL**（应用内容 → 隐私政策）：

   ```
   https://github.com/kkoshin/Muse/blob/main/PRIVACY_POLICY.md
   ```

   （若之后启用 GitHub Pages，可换成 Pages 地址；政策正文在仓库根目录 `PRIVACY_POLICY.md`。）
4. **数据安全（Data safety）问卷**，依据 `PRIVACY_POLICY.md` 如实填写：
   - 开发者**不收集也不共享**用户数据（无账号、无分析、无广告、无遥测）；API key、文本、音频、崩溃日志均仅存本机。
   - 使用合成功能时，用户输入的文本/音频会经**用户自己的** ElevenLabs API key 发往 `api.elevenlabs.io`。问卷中涉及"数据是否离开设备发往第三方"的选项请按此事实勾选（共享 → 用途"App functionality"，或按问卷对"用户主动选择的服务"的表述理解），预览结果与 `PRIVACY_POLICY.md` 保持一致。
   - 传输加密（HTTPS）= 是；数据删除 = 不适用（本机数据，卸载即删）。
5. **内容分级（IARC 问卷）**：无暴力、无互动 UGC、无账号 → 预计 Everyone；按问卷如实回答。
6. **目标群体 / 广告 / 新闻 / COVID 等声明**：不面向儿童、无广告、非新闻应用。
7. **应用功能声明（App access）与审核说明**：应用无登录，但**需要自备 ElevenLabs API key** 才能出声。为避免审核以"无法使用"拒收：
   - 在 App access / Instructions for review 中写明：需用户自己的 ElevenLabs API key（ElevenLabs 有免费档），首启提供配置引导；
   - 建议额外提供一个测试用 API key 给审核团队。
8. **应用内商品 / 订阅**：无。

## 三、每次发版流程（Play 渠道）

1. 按 [SKILL.md](../SKILL.md) 完成版本号、changelog、测试。
2. 推 tag / 创建 GitHub Release → `package.yml` 产出两个 artifact：
   - `release-artifacts`：GitHub Release / F-Droid 用 APK + mapping.txt（**原流程不变**）
   - `play-aab`：`Muse-<version>.aab` + `mapping.txt`（已用 Play 上传密钥签名）
3. 下载 `play-aab`，在 Play Console → 测试 → 内部测试（先）或正式版 → 创建新版本 → 上传 AAB。
4. 同页上传 `mapping.txt` 作为混淆映射文件（Deobfuscation file）。
5. 版本说明粘贴 `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`。
6. 保存并逐步发布（建议先内部测试轨验证，再推生产）。

versionCode 必须单调递增（`setUpStableVersion` 已管理）；Play 不接受回退。

## 四、注意点

- **targetSdk 当前为 35**：若 Play 以 target API 过低拒收（2026-08-31 起新提交大概率要求 36），届时把 `gradle/libs.versions.toml` 的 `android-targetSdk` 升到 `36` 并跑测试（`compileSdk` 已是 36，改动很小），单独开 PR 处理。
- **两套密钥**：`KEY_STORE_*` = GitHub Release APK（F-Droid 用）；`PLAY_KEYSTORE_*` = Play 上传密钥。
- **Play App Signing 不可逆**：注册后应用签名密钥由 Google 托管，上传密钥可重置；首次上传 AAB 即绑定。
- AAB 由 `jarsigner` 以 JAR 签名方式签名（Play 官方支持的手动签名方式）；PKCS12/JKS、RSA/EC 密钥均可，JDK 17 自动识别。
- **PKCS12 口令坑**：`keytool -genpair` 生成 PKCS12 时会忽略 `-keypass`，key 口令恒等于 store 口令。若两者填得不一致，`jarsigner` 会报"密钥不是私有密钥"。新 keystore 建议直接两处填同一个口令。
- 本地想预演签名流程：`./gradlew :composeApp:bundleRelease` 后用自签 keystore 跑一遍 `jarsigner -keystore ... composeApp/build/outputs/bundle/release/*.aab <alias>` 并 `jarsigner -verify`。
