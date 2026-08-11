package com.lpzhcn;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;

/**
 * LP 中文汉化 mod 入口（纯客户端）。实际转换逻辑在 LPStringTransformer。
 * SupplyMode/PatternMode 枚举名（Bulk50 等）由 FullScreenIME 的
 * ClientProxy.translateLPEnumButtons 在渲染层翻译（常量池替换不安全）。
 */
@Mod(modid = LPChineseMod.MODID, name = LPChineseMod.MODNAME, version = LPChineseMod.VERSION)
public class LPChineseMod {

    public static final String MODID = "lpzhcn";
    public static final String MODNAME = "LP中文汉化";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 转换器已由 IFMLLoadingPlugin 注册，无需额外初始化
    }
}
