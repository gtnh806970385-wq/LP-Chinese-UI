package com.lpzhcn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * 运行时把 LogisticsPipes 类常量池中的硬编码 GUI 字符串替换为中文。
 * 零外部依赖（不使用 ASM 库，直接重建 class 常量池），任何异常安全回退不崩溃。
 */
public class LPStringTransformer implements IClassTransformer {

    private static final Map<String, String> TRANS = new HashMap<String, String>();

    private static void put(String en, String zh) {
        TRANS.put(en, zh);
    }

    static {
        // ===== gui/ 通用按钮与标签 =====
        put("Add Macro", "添加宏");
        put("Amount", "数量");
        put("Analyse Slot", "分析槽位");
        put("Authorization:", "授权：");
        put("Authorized", "已授权");
        put("Both", "两者");
        put("Charge Items", "充电物品");
        put("Charge:", "充能：");
        put("Clipboard doesn't", "剪贴板中无");
        put("Close", "关闭");
        put("Components:", "组件：");
        put("Content", "内容");
        put("Craft", "合成");
        put("CraftOnly", "仅合成");
        put("Deauthorized", "已取消授权");
        put("Default:", "默认：");
        put("Delete", "删除");
        put("Discharge Items", "放电物品");
        put("Disk", "磁盘");
        put("Done", "完成");
        put("Edit Logic Controller", "编辑逻辑控制器");
        put("Exclude", "排除");
        put("Excluded", "已排除");
        put("Exit", "退出");
        put("Expected:", "预期：");
        put("Extract", "提取");
        put("Extract Mode: off", "提取模式：关");
        put("Extract Mode: on", "提取模式：开");
        put("Fluid", "流体");
        put("FluidName", "流体名称");
        put("Hide", "隐藏");
        put("ID:", "ID：");
        put("Id:", "ID：");
        put("Include", "包含");
        put("Included", "已包含");
        put("Inv", "背包");
        put("Limited", "受限");
        put("List", "列表");
        put("Log", "日志");
        put("Macro Items", "宏物品");
        put("Missing:", "缺少：");
        put("Most likely", "最可能");
        put("Name:", "名称：");
        put("No", "否");
        put("No Group", "无组");
        put("Nothing", "无");
        put("Now", "现在");
        put("OK", "确定");
        put("Page", "页");
        put("Please enter a name", "请输入名称");
        put("Refresh", "刷新");
        put("Remove", "移除");
        put("Request", "请求");
        put("Request Fluid", "请求流体");
        put("Request ID:", "请求ID：");
        put("Request Type:", "请求类型：");
        put("Request successful!", "请求成功！");
        put("Result:", "结果：");
        put("Save", "保存");
        put("Save as Image", "另存为图片");
        put("Saved tree view as", "已保存树状视图为");
        put("Search:", "搜索：");
        put("Select some items", "选择一些物品");
        put("Send to Router ID:", "发送到路由器ID：");
        put("Set", "设置");
        put("Show", "显示");
        put("Side", "方向");
        put("Sinks", "接收器");
        put("Sneaky", "潜行");
        put("Sort", "排序");
        put("Stored Energy:", "储存能量：");
        put("Supply", "供应");
        put("SupplyOnly", "仅供应");
        put("Switch", "切换");
        put("Timeout:", "超时：");
        put("Todo:", "待办：");
        put("Unknown", "未知");
        put("Unlimited", "无限");
        put("Yes", "是");
        put("You are missing:", "你缺少：");
        put("contain a number.", "需包含数字。");
        put("from:", "从：");
        put("pending:", "进行中：");
        put("ticks", "刻");
        put("more", "更多");
        put("GroupColor: Blue", "组颜色：蓝色");
        put("GroupColor: Cyan", "组颜色：青色");
        put("GroupColor: Green", "组颜色：绿色");
        put("GroupColor: Purple", "组颜色：紫色");
        put("GroupColor: RED", "组颜色：红色");
        put("GroupColor: Yellow", "组颜色：黄色");
        // ===== modules/ =====
        put("Aspects:", "属性：");
        put("BeeAllele", "蜜蜂等位基因");
        put("Buffer", "缓冲");
        put("Bulk100", "批量100");
        put("Bulk50", "批量50");
        put("Cave", "洞穴");
        put("Cleanup Filer Items", "清理过滤物品");
        put("Couldn't extract the already sended items from the inventory.", "无法从容器中提取已发送的物品。");
        put("Drone", "雄蜂");
        put("Extract Mode:", "提取模式：");
        put("Extraction:", "提取：");
        put("Filter:", "过滤器：");
        put("Fluid items", "流体物品");
        put("Flyer", "飞行");
        put("Full", "满");
        put("Infinite", "无限");
        put("ItemSink", "物品接收");
        put("Mod", "模组");
        put("Mode:", "模式：");
        put("Mods:", "模组：");
        put("No prefixes", "无前缀");
        put("Nocturnal", "夜行");
        put("Ore", "矿石");
        put("Ores:", "矿石：");
        put("Partial", "部分");
        put("Prefixes:", "前缀：");
        put("Princess", "蜂后");
        put("PureCave", "纯洞穴");
        put("PureFlyer", "纯飞行");
        put("PureNocturnal", "纯夜行");
        put("Purebred", "纯种");
        put("Queen", "蜂王");
        put("Requested Items", "已请求物品");
        put("Requested liquids", "已请求流体");
        put("Terminated:", "已终止：");
        // ===== pipes/ =====
        put("ALL ITEMS:", "所有物品：");
        put("Amount:", "数量：");
        put("Center", "中心");
        put("Chassi pipe", "底盘管道");
        put("Console", "控制台");
        put("Crafting Matrix", "合成矩阵");
        put("Crafting Resources", "合成资源");
        put("Crafting Result", "合成结果");
        put("Disk Slot", "磁盘槽位");
        put("Empty", "空");
        put("Extra has to be an item for a chassis pipe", "附加必须是底盘管道的物品");
        put("Fast", "快速");
        put("Filter Inv", "过滤容器");
        put("Fluid Multiplication", "流体倍增");
        put("Fluids to keep stocked", "要补给的流体");
        put("Freq Slot", "频率槽位");
        put("Freq. card", "频率卡");
        put("In Transit To Me", "正在运往我");
        put("Internal LogisticsPipes Error", "物流管道内部错误");
        put("Invalid ItemIdentifier", "无效的物品标识符");
        put("Invalid ItemIdentifierID", "无效的物品标识符ID");
        put("Item queue mismatch", "物品队列不匹配");
        put("LP-Version", "LP 版本");
        // ⚠️ 不翻译 "LogisticsPipes"（MainProxy 网络频道名 + LP 存档文件夹名，翻译会断频道/丢存档数据）
        // ⚠️ 不翻译 "LogisticsPipes Pipe Block"（方块 unlocalizedName）
        put("Normal", "普通");
        put("Orientation", "方向");
        put("Permission denied", "权限被拒绝");
        put("Pipe", "管道");
        put("Pluggable", "可插接");
        put("Router", "路由器");
        put("Sat ID:", "卫星 ID：");
        put("Send Queue", "发送队列");
        put("Sorting Slot", "整理槽位");
        put("State Information", "状态信息");
        put("Status List", "状态列表");
        put("This is no firewall pipe", "这不是防火墙管道");
        put("This is no routing pipe", "这不是路由管道");
        put("Time:", "时间：");
        put("Transport", "运输");
        put("Unknown texture to power, ", "未知纹理对应能量：");
        // ===== 2026/8/11 补充：遗漏的 GUI 硬编码字符串 =====
        // 合成管道 GuiCraftingPipe
        put("Import", "导入");
        put("Output", "输出");
        put("Inputs", "输入");
        put("Satellite", "卫星");
        put("Priority", "优先级");
        put("Extra", "附加");
        // 防火墙 GuiFirewall
        put("Blocked", "已阻止");
        put("Allowed", "已允许");
        put("Firewall", "防火墙");
        put("Filter", "过滤");
        put("Providing", "提供");
        put("Sorting", "排序");
        put("Powerflow", "能量流");
        // 供应/提供管道
        put("TargetInv", "目标容器");
        put("Partialrequests", "部分请求");
        put("TargetInvPattern", "目标容器配方");
        put("RequestMode", "请求模式");
        // 管道控制器 GuiPipeController
        put("Id: ", "ID：");
        put("Authorization: ", "授权：");
        put("Session", "会话");
        put("Lifetime", "生命周期");
        put("Recieved", "已接收");
        put("Relayed", "已转发");
        put("RoutingTableSize", "路由表大小");
        // 能量 GuiPowerJunction / GuiPowerProvider
        put("StoredEnergy", "储存能量");
        put("PowerLevel", "能量等级");
        put("MaxStorage", "最大储存");
        put("ConversionEnergyRF", "能量转换 RF");
        put("ConversionEnergyEU", "能量转换 EU");
        // 库存系统连接器 GuiInvSysConnector
        put("InventorySystemConnector", "库存系统连接器");
        put("ConnectionCard", "连接卡");
        put("Waitingfor", "等待");
        put("Resistance", "阻力");
        // 安全站 GuiSecurityStation
        put("EditTable", "编辑表");
        put("Authorize", "授权");
        put("Deauthorize", "取消授权");
        put("SecurityStation", "安全站");
        put("PlayerKey", "玩家密钥");
        put("SecurityCards", "安全卡");
        put("PlayerList", "玩家列表");
        // 安全站弹窗 GuiSecurityStationPopup
        put("ConfigureSettings", "配置设置");
        put("ActiveRequesting", "激活请求");
        put("UpgradePipes", "升级管道");
        put("CheckNetwork", "检查网络");
        put("RemovePipes", "移除管道");
        // 升级管理 GuiUpgradeManager
        put("Upgrades", "升级");
        put("SneakyUpgrades", "潜行升级");
        // HUD 设置 GuiHUDSettings
        put("ChassiePipe", "底盘管道");
        put("CraftingPipe", "合成管道");
        put("InvSysConPipe", "库存系统连接管道");
        put("PowerJunction", "能量节点");
        put("ProviderPipe", "提供管道");
        put("SatellitePipe", "卫星管道");
        // 模块
        put("DEFAULT", "默认");
        put("Defaultroute", "默认路由");
        put("IgnoreData", "忽略数据");
        put("IgnoreNBT", "忽略 NBT");
        // 请求相关（带尾随空格精确匹配）
        put("Components: ", "组件：");
        put("Missing: ", "缺少：");
        put("Request Type: ", "请求类型：");
        put("Send to Router ID: ", "发送到路由器ID：");
        put("Request ID: ", "请求ID：");
        put("ID: ", "ID：");
        put("Page ", "页 ");
        put("Saved tree view as ", "已保存树状视图为 ");
        put("Timeout: ", "超时：");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || name == null) return basicClass;
        String dot = name.replace('/', '.');
        if (!dot.startsWith("logisticspipes.")) return basicClass;
        try {
            byte[] out = process(basicClass);
            return out == null ? basicClass : out;
        } catch (Throwable t) {
            return basicClass; // 安全兜底：任何异常都返回原始字节，绝不导致崩溃
        }
    }


    /** 重建 class 常量池。只替换"被 CONSTANT_String 引用、且不被任何标识符引用"的 UTF8（纯显示字符串）。
     *  枚举常量名/方法名/字段名/类名/描述符等标识符一律跳过，避免破坏类结构。返回 null 表示无变化。 */
    public static byte[] process(byte[] b) throws Exception {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
        in.readInt(); // magic
        in.readUnsignedShort(); // minor
        in.readUnsignedShort(); // major
        int count = in.readUnsignedShort();
        int[] tags = new int[count];
        byte[][] data = new byte[count][];
        String[] utf8 = new String[count];
        for (int i = 1; i < count; i++) {
            int t = in.readUnsignedByte();
            tags[i] = t;
            switch (t) {
                case 1: {
                    int len = in.readUnsignedShort();
                    byte[] dd = new byte[len];
                    in.readFully(dd);
                    data[i] = dd;
                    utf8[i] = new String(dd, StandardCharsets.UTF_8);
                    break;
                }
                case 3: case 4: data[i] = new byte[4]; in.readFully(data[i]); break;
                case 5: case 6: data[i] = new byte[8]; in.readFully(data[i]); i++; tags[i] = -1; break;
                case 7: case 8: case 16: case 19: case 20: data[i] = new byte[2]; in.readFully(data[i]); break;
                case 9: case 10: case 11: case 12: case 17: case 18: data[i] = new byte[4]; in.readFully(data[i]); break;
                case 15: data[i] = new byte[3]; in.readFully(data[i]); break;
                default: throw new RuntimeException("unknown tag " + t);
            }
        }
        byte[] rest = new byte[in.available()];
        in.readFully(rest);

        // ---- 统计引用关系 ----
        Set<Integer> idRefs = new HashSet<Integer>();  // 被当作标识符引用的 utf8 index
        Set<Integer> strRefs = new HashSet<Integer>(); // 被 CONSTANT_String 引用的 utf8 index
        for (int i = 1; i < count; i++) {
            int t = tags[i];
            if (t == 7 || t == 16 || t == 19 || t == 20) { // Class/MethodType/Module/Package -> utf8
                idRefs.add(u16(data[i], 0));
            } else if (t == 12) { // NameAndType -> name + descriptor
                idRefs.add(u16(data[i], 0));
                idRefs.add(u16(data[i], 2));
            } else if (t == 8) { // String -> utf8
                strRefs.add(u16(data[i], 0));
            }
        }
        for (int i = 1; i < count; i++) {
            int t = tags[i];
            if (t == 9 || t == 10 || t == 11) { // Fieldref/Methodref/InterfaceMethodref -> Class + NameAndType
                addIndirect(idRefs, tags, data, u16(data[i], 0), u16(data[i], 2));
            } else if (t == 15) { // MethodHandle -> ref 到 9/10/11
                int ref = u16(data[i], 1);
                if (ref < count) {
                    int rt = tags[ref];
                    if (rt == 9 || rt == 10 || rt == 11) addIndirect(idRefs, tags, data, u16(data[ref], 0), u16(data[ref], 2));
                }
            } else if (t == 17 || t == 18) { // Dynamic/InvokeDynamic -> NameAndType
                int n = u16(data[i], 2);
                if (n < count && tags[n] == 12) {
                    idRefs.add(u16(data[n], 0));
                    idRefs.add(u16(data[n], 2));
                }
            }
        }

        // ---- 替换（仅纯显示字符串）----
        boolean changed = false;
        for (int i = 1; i < count; i++) {
            if (tags[i] == 1 && strRefs.contains(i) && !idRefs.contains(i)) {
                String r = TRANS.get(utf8[i]);
                if (r != null) {
                    byte[] nb = r.getBytes(StandardCharsets.UTF_8);
                    if (nb.length > 65535) throw new RuntimeException("too long");
                    data[i] = nb; // 只存内容，长度前缀在写入时统一补写
                    changed = true;
                }
            }
        }
        if (!changed) return null;

        ByteArrayOutputStream out = new ByteArrayOutputStream(b.length + 256);
        DataOutputStream d = new DataOutputStream(out);
        d.writeInt(0xCAFEBABE);
        d.writeShort(((b[4] & 0xFF) << 8) | (b[5] & 0xFF));
        d.writeShort(((b[6] & 0xFF) << 8) | (b[7] & 0xFF));
        d.writeShort(count);
        for (int i = 1; i < count; i++) {
            if (tags[i] == -1) continue; // long/double 第二槽
            d.writeByte(tags[i]);
            if (tags[i] == 1) { // CONSTANT_Utf8 必须写 2 字节长度前缀
                d.writeShort(data[i].length);
                d.write(data[i]);
            } else {
                d.write(data[i]);
            }
        }
        d.write(rest);
        return out.toByteArray();
    }

    private static int u16(byte[] d, int off) {
        return ((d[off] & 0xFF) << 8) | (d[off + 1] & 0xFF);
    }

    private static void addIndirect(Set<Integer> idRefs, int[] tags, byte[][] data, int cIdx, int nIdx) {
        if (cIdx < tags.length && tags[cIdx] == 7) idRefs.add(u16(data[cIdx], 0));
        if (nIdx < tags.length && tags[nIdx] == 12) {
            idRefs.add(u16(data[nIdx], 0));
            idRefs.add(u16(data[nIdx], 2));
        }
    }
}

