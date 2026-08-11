package com.lpzhcn;

import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * LP 中文汉化 coremod 入口（纯客户端，运行时替换 LogisticsPipes 硬编码 GUI 字符串）。
 * 不改 LP jar，服务器与 BetterLooting 同理可正常连接。
 */
@IFMLLoadingPlugin.TransformerExclusions({ "com.lpzhcn." })
public class LPChineseCorePlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.lpzhcn.LPStringTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // no-op
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
