# LP-Chinese-UI（LogisticsPipes 中文界面）

面向 **Minecraft 1.7.10 / GT New Horizons** 的 LogisticsPipes 中文界面项目。提供 `zh_CN.lang` 翻译和客户端 coremod，用于翻译资源文件未覆盖的部分 GUI 文本。

## 下载与安装

从 [GitHub Releases](https://github.com/gtnh806970385-wq/LP-Chinese-UI/releases/latest) 下载 `lpzhcn` 模组 JAR，放入客户端实例的 `mods` 目录。模组以 coremod 方式加载，安装后需重启游戏。无需替换 LogisticsPipes 官方 JAR；不要同时安装仓库旧版发布中的 `logisticspipes-1.5.26-GTNH-hans.jar`。

## 功能

- 提供 LogisticsPipes 1.5.26-GTNH 的 `zh_CN.lang` 翻译文件
- 对 GUI 中硬编码的文本执行运行时常量池替换
- 只修改作为字符串内容使用、且未被字段/方法标识符引用的常量
- 保留网络频道名、存档目录名、unlocalizedName 和 NBT 键等逻辑标识符
- 部分枚举按钮不能安全地通过常量池替换处理，可能需要 FullScreenIME 的界面渲染翻译支持
- 客户端本地化；多人游戏仍由服务器决定实际游戏逻辑

## 仓库内容

- `src/com/lpzhcn/`：coremod、模组入口与翻译器源码
- `trans/`：语言文件及硬编码文本翻译表
- `tools/`：扫描、替换和校验辅助工具

## 构建

当前项目使用手动 `javac` 构建，需要 Java 编译器、Forge 1.7.10 依赖以及提供 `IClassTransformer` 的 GTNH/lwjgl3ify 编译依赖。请将依赖 JAR 的实际路径替换到命令中。

Linux/macOS：

```bash
mkdir -p build/classes/assets/logisticspipes/lang
javac -encoding UTF-8 -classpath "<forgePatches.jar>:<forge-universal.jar>" -d build/classes src/com/lpzhcn/*.java
cp trans/zh_CN.lang build/classes/assets/logisticspipes/lang/zh_CN.lang
jar cfm build/lpzhcn.jar MANIFEST.MF -C build/classes .
```

Windows PowerShell 的 classpath 分隔符为分号 `;`：

```powershell
New-Item -ItemType Directory -Force build/classes/assets/logisticspipes/lang | Out-Null
javac -encoding UTF-8 -classpath "<forgePatches.jar>;<forge-universal.jar>" -d build/classes src/com/lpzhcn/*.java
Copy-Item trans/zh_CN.lang build/classes/assets/logisticspipes/lang/zh_CN.lang
jar cfm build/lpzhcn.jar MANIFEST.MF -C build/classes .
```

JAR manifest 需要声明 `FMLCorePlugin` 与 `FMLCorePluginContainsFMLMod`。发布前应确认 manifest、依赖版本和构建命令与当前 GTNH 环境一致。

## 许可证与上游来源

本仓库目前**没有为自有代码声明许可证**。LogisticsPipes 上游使用 [Minecraft Mod Public License 1.0.1](https://github.com/GTNewHorizons/LogisticsPipes/blob/master/LICENSE.md)。本项目针对的上游版本是 `1.5.26-GTNH`；如分发包含上游代码的修改版，请同时提供对应源码并遵守该许可证。详见 [第三方来源说明](THIRD_PARTY_NOTICES.md)。
