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

1. 按 [SKILL.md](../SKILL.md) 准备发布，并完成 [发布验证清单](release-guide.md) 中的版本配置、changelog、构建及手动回归。
2. 手动运行 `package.yml`，或创建 GitHub Release 触发构建（单独推 tag 不会触发），产出两个 artifact：
   - `release-artifacts`：GitHub Release / F-Droid 用 APK（`assembleRelease`，含 xcrash）+ `mapping/release/mapping.txt`（**原流程不变**）
   - `play-aab`：`Muse-<version>.aab`（`bundlePlay`，**不含 xcrash**）+ `mapping/play/mapping.txt`（已用 Play 上传密钥签名）

   Play 包走独立的 `play` build type：xcrash 的预编译 `.so` 非 16 KB 对齐、会被 Play 拒收，故不打包（`releaseImplementation`/`debugImplementation` 限定依赖）。因此 Play 版的崩溃报告不在应用内，改由 Play Console 的 **Android vitals** 提供。流水线里有断言步骤把关，xcrash 若溜回去会直接 fail。
3. 下载 `play-aab`，在 Play Console → 测试 → 内部测试（先）或正式版 → 创建新版本 → 上传 AAB。
4. 同页上传 `mapping.txt` 作为混淆映射文件（Deobfuscation file）。
5. 版本说明粘贴 `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`。
6. 保存并逐步发布（建议先内部测试轨验证，再推生产）。

versionCode 必须单调递增（`setUpStableVersion` 已管理）；Play 不接受回退。

## 四、注意点

- **target API 要求**：提交前核对 Play Console 当前要求与 `gradle/libs.versions.toml` 中的 `android-targetSdk`；升级后按 [发布验证清单](release-guide.md) 完成构建及目标系统上的回归。
- **两套密钥**：`KEY_STORE_*` = GitHub Release APK（F-Droid 用）；`PLAY_KEYSTORE_*` = Play 上传密钥。
- **Android 开发者验证**：包名和**所有**签名密钥都要登记，只注册包名不足以解除送审拦截，处理顺序见第五节。
- **Play App Signing 不可逆**：注册后应用签名密钥由 Google 托管，上传密钥可重置；首次上传 AAB 即绑定。
- AAB 由 `jarsigner` 以 JAR 签名方式签名（Play 官方支持的手动签名方式）；PKCS12/JKS、RSA/EC 密钥均可，JDK 17 自动识别。
- **PKCS12 口令坑**：`keytool -genpair` 生成 PKCS12 时会忽略 `-keypass`，key 口令恒等于 store 口令。若两者填得不一致，`jarsigner` 会报"密钥不是私有密钥"。新 keystore 建议直接两处填同一个口令。
- 本地想预演签名流程：`./gradlew :composeApp:bundlePlay` 后用自签 keystore 跑一遍 `jarsigner -keystore ... composeApp/build/outputs/bundle/play/*.aab <alias>` 并 `jarsigner -verify`。

## 五、Android 开发者验证（ADV）

> 每个软件包名称及其**所有签名密钥**都必须注册到已验证的开发者身份下（2026-09-30 起分批强制，2027 年起全球推行，含 Play 之外的渠道）。没登记时 Play 送审会被拦：**"未注册的软件包名称……我们检测到以下密钥目前尚未注册"**。

### 5.1 一个包名为什么会有多把密钥

Muse 同时走"自签名分发（GitHub Release / F-Droid）+ Play 托管签名"，同一个 `io.github.kkoshin.muse` 下天然存在多把证书，**不是被谁冒用**（Android 的签名校验也保证现有用户不会被别的密钥的包覆盖升级）：

| 指纹（2026-10 首次登记时的实际值） | 归属 | 签的是什么 |
|---|---|---|
| `B7:80:E7:1C:0D:06:F9:AC:15:6E:D2:00:B1:32:48:C1:CE:39:C3:CB:5D:49:7A:53:FD:F0:41:C2:50:25:76:D9` | 本项目（CI `KEY_STORE_*`） | GitHub Release APK。F-Droid 官仓也直接分发这份**自签名** APK（fdroiddata 配方用 `Binaries:` + `AllowedAPKSigningKeys`），所以不存在第二把 F-Droid 密钥 |
| `53:3D:B3:7E:ED:D7:69:5E:49:C5:6F:84:DB:48:BA:A4:73:72:05:F3:3A:0A:65:E9:36:F0:CA:D9:D5:FA:1C:44` | 本项目（CI `PLAY_KEYSTORE_*`） | 上传到 Play 的 AAB。**不会出现在检测清单里**——它从不签出被安装的 APK |
| `7C:01:A8:60:E8:10:5C:79:2D:1A:23:DC:E2:E0:52:9D:17:FC:DE:A1:73:E5:49:73:AC:36:6B:B5:E2:98:05:A0` | Google | Play 应用签名密钥，即用户从 Play 下载到的 APK 的签名 |
| `A9:EC:89:7F:23:50:6E:47:7E:7D:AC:5F:1B:EB:A8:43:B8:14:D0:49:F6:8A:C1:4C:6F:5B:E5:0A:89:79:27:7B` | Google | 内部应用分享的"内部测试证书"——internal app sharing 会用 Google 为该应用自动生成的密钥重签上传的一切工件 |

> 更换签名密钥后请同步更新上表：密钥一变，Play 的检查会重新拦下来。

### 5.2 被拦时的处理顺序

关键教训：**光把包名注册下来不够**。Play 那条提示是**按密钥逐把列**的，包名注册完成后提示通常仍在，必须把清单里的密钥逐个补登记，三把都"已验证"后拦截才消失。

1. Play 管理中心 → **Android 开发者验证** → 注册软件包名称 → 在"符合条件的公钥"里选中自己那把（筛选规则按已知安装量，占比过半的密钥优先；本项目是 GitHub/F-Droid 那把）→ 走完"**签名并上传 APK**"，包名状态变"已注册"、该指纹"已验证"。
2. 回 Play 刷新，提示多半还在，只是少了刚注册的那把。回到验证页面点"**添加密钥**"，把剩下的逐个加进去。
3. 对自己不持有私钥的密钥（Google 的应用签名密钥、内部应用分享证书）**同样用"添加密钥"直接登记即可**：2026-10 实测手动添加后即时通过，无需上传平台重签的 APK。官方文档里"委托签名"那条路（把带代码段的版本发到平台 → 下载平台重签的 APK → 上传）是备选方案。
4. 清单清空后再送审。

### 5.3 生成"签名并上传 APK"用的证明包

三条要求：包名与注册的一致；`assets/adi-registration.properties`（**文件名一字不差**）内容为控制台给出的代码段（就是一串代码本身，无键名无注释）；用与所选指纹对应的私钥签名。**不需要是真实应用**——官方原文"您无需上传实际的 APK"，因此用 aapt2 直接搓个空壳包即可，不必跑 Gradle：

```bash
# 依赖：Android SDK build-tools（aapt2/apksigner/zipalign/d8）+ JDK 17
BT="$HOME/Library/Android/sdk/build-tools/36.1.0"
PLATFORM="$HOME/Library/Android/sdk/platforms/android-36/android.jar"
KEYSTORE=/path/to/release-keystore        # 与待验证指纹对应的私钥
ALIAS=default
SRC=$(mktemp -d); mkdir -p "$SRC/assets" "$SRC/java/io/github/kkoshin/muse" "$SRC/dex"

cat > "$SRC/AndroidManifest.xml" <<'XML'
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="io.github.kkoshin.muse" android:versionCode="1" android:versionName="1.0">
    <application android:label="Muse (verification)" android:hasCode="true"/>
</manifest>
XML
cp adi-registration.properties "$SRC/assets/"   # 控制台给的代码段，逐字粘贴

cat > "$SRC/java/io/github/kkoshin/muse/VerificationStub.java" <<'JAVA'
package io.github.kkoshin.muse;
public final class VerificationStub { private VerificationStub() {} }
JAVA
javac --release 8 -nowarn -d "$SRC/classes" "$SRC/java/io/github/kkoshin/muse/VerificationStub.java"
"$BT/d8" --release --min-api 29 --lib "$PLATFORM" --output "$SRC/dex" \
    "$SRC/classes/io/github/kkoshin/muse/VerificationStub.class"

"$BT/aapt2" link -o "$SRC/unsigned.apk" -I "$PLATFORM" --manifest "$SRC/AndroidManifest.xml" \
    -A "$SRC/assets" --min-sdk-version 29 --target-sdk-version 36 --version-code 1 --version-name 1.0
(cd "$SRC/dex" && zip -q "$SRC/unsigned.apk" classes.dex)
"$BT/zipalign" -f -p 4 "$SRC/unsigned.apk" "$SRC/aligned.apk"
"$BT/apksigner" sign --ks "$KEYSTORE" --ks-key-alias "$ALIAS" \
    --out muse-adv-verification.apk "$SRC/aligned.apk"

# 核对：SHA-256 应与待注册的指纹逐位一致
"$BT/apksigner" verify --print-certs muse-adv-verification.apk
```

> 该 APK 只用于所有权证明，**不要分发**，验证通过后删掉即可。另外 `apksigner 0.9`（build-tools 36）的 `verify` 只把最高的签名方案报成 `true`，看到 "v1/v2: false, v3: true" 属正常——文件里 v1/v2/v3 都在，与线上 APK 的签名剖面一致。
