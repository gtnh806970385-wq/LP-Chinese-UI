# LP-Chinese-UI（LP 管道界面中文汉化）

LogisticsPipes 1.5.26-GTNH 的中文汉化 coremod（Minecraft 1.7.10 / GTNH 整合包）。

## 功能

- **完整 `zh_CN.lang`（459/459 键）**：管道、模块、升级、物品、GUI、聊天、HUD 全覆盖
- **coremod 运行时常量池安全替换**：汉化 GUI 硬编码字符串（Sort / Refresh / Craft / Both 等约 150 条）
- **纯客户端**：不改 LP 原 jar，服务器零安装，联机可用
- **三把锁保证安全**（本项目的核心方法论）：
  1. **结构安全锁**：只替换被 `CONSTANT_String` 引用且不被任何标识符引用的 UTF8 —— 枚举常量名/方法名/字段名绝不改动
  2. **逻辑安全锁**：网络频道名、存档文件夹名、unlocalizedName、NBT 键一律不翻译（否则断频道/丢存档）
  3. **常量池重写锁**：long/double 双槽处理、UTF8 长度前缀写入

## 文件结构

```
src/com/lpzhcn/
├── LPChineseCorePlugin.java    # FML coremod 入口
├── LPChineseMod.java           # mod 主类
├── LPStringTransformer.java    # 常量池安全替换 transformer（核心）
└── TestLP.java                 # 单类 CJK 统计验证工具
trans/
├── zh_CN.lang                  # 完整翻译（459/459 键）
└── hardcoded_translations.txt  # 硬编码 GUI 字符串翻译表
```

## 构建

编译需要 JDK + `lwjgl3ify-3.0.23-forgePatches.jar`（提供 `net.minecraft.launchwrapper.IClassTransformer`）。

```
javac -encoding UTF-8 -classpath forgePatches.jar;forge-universal.jar -d classes src/com/lpzhcn/*.java
jar cfm lpzhcn.jar MANIFEST.MF -C classes .
```

MANIFEST 需声明 `FMLCorePlugin` 和 `FMLCorePluginContainsFMLMod`。

## 验证工具链

| 工具 | 作用 | 通过标准 |
|---|---|---|
| `_lp_tools/SweepLP.java` | 全量 1357 类验证 | `idViolations=0 / reparseFail=0 / PASS` |
| `_lp_tools/LPStringCollision.java` | 标识符冲突检测 | 修改类 0 冲突 |
| `_lp_tools/TestLP.java` | 单类 CJK 统计 | 关键 GUI 类出汉字 |
| `_lp_tools/ReportReplace.java` | 列出每类替换对 | 人工核对无逻辑字符串 |

## 注意事项

- 枚举常量名（Bulk50 / Bulk100 等）是字段标识符，常量池替换无法安全处理，由全屏输入法 [FullScreenIME](https://github.com/gtnh806970385-wq/FullScreenIME) 的渲染层翻译
- 部署后重启游戏（coremod 启动时加载），日志看 `logs/fml-client-latest.log`

## 授权

本汉化仅供学习交流使用，请尊重原 Mod 的许可证。
