package com.lpzhcn;

import java.nio.file.Files;
import java.nio.file.Paths;

/** 验证 LPStringTransformer.process 对真实 LP class 的替换效果。 */
public class TestLP {
    public static void main(String[] args) throws Exception {
        byte[] b = Files.readAllBytes(Paths.get(args[0]));
        byte[] out = LPStringTransformer.process(b);
        if (out == null) {
            System.out.println("RESULT: no change");
            return;
        }
        String s = new String(out, "UTF-8");
        int cjk = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x4e00 && c <= 0x9fff) cjk++;
        }
        System.out.println("RESULT: changed, cjk_chars=" + cjk);
        if (args.length > 1) Files.write(Paths.get(args[1]), out);
    }
}
