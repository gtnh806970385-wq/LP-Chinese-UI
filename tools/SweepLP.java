import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.lpzhcn.LPStringTransformer;

/**
 * 全量验证：对 LP 所有类运行 LPStringTransformer.process()，
 * 1) 输出能重新解析（结构合法）
 * 2) 被替换的 UTF8 绝不被标识符条目（Class/NameAndType/MethodType/Module/Package 及间接引用）引用
 * 3) 统计：修改类数 / 未变化类数 / 异常类数 / 总汉字数 / 被跳过的标识符类串数
 */
public class SweepLP {

    public static void main(String[] args) throws Exception {
        List<Path> files = new ArrayList<>();
        Files.walk(Paths.get(args[0])).filter(p -> p.toString().endsWith(".class")).forEach(files::add);
        int modified = 0, unchanged = 0, failed = 0, reparseFail = 0, idViolations = 0, totalCJK = 0;
        int totalSkippedIdRefs = 0; // 因标识符被跳过的翻译键出现次数
        for (Path f : files) {
            byte[] b = Files.readAllBytes(f);
            byte[] out;
            try {
                out = LPStringTransformer.process(b);
            } catch (Throwable t) {
                failed++;
                System.out.println("PROCESS-EXCEPTION: " + f + " : " + t);
                continue;
            }
            if (out == null) { unchanged++; continue; }
            modified++;
            Parsed po = parse(out);
            if (po == null) { reparseFail++; System.out.println("REPARSE-FAIL: " + f); continue; }
            Set<Integer> idRefs = identifierRefs(po);
            Parsed pi = parse(b);
            for (int i = 1; i < po.count; i++) {
                if (po.tags[i] == 1 && pi.tags[i] == 1) {
                    String a = po.utf8[i], c = pi.utf8[i];
                    if (a == null || c == null) continue;
                    if (!a.equals(c)) {
                        // 这个 utf8 被替换了，必须不是标识符引用
                        if (idRefs.contains(i)) {
                            idViolations++;
                            System.out.println("ID-VIOLATION: " + f + " utf8#" + i + " '" + c + "'->'" + a + "' is identifier!");
                        }
                        for (int j = 0; j < a.length(); j++) {
                            char ch = a.charAt(j);
                            if (ch >= 0x4e00 && ch <= 0x9fff) totalCJK++;
                        }
                    }
                }
            }
            // 统计被跳过的标识符翻译键
            for (int i = 1; i < po.count; i++) {
                if (po.tags[i] == 1 && idRefs.contains(i)) {
                    String s = po.utf8[i];
                    if (s != null && LPStringTransformer_KEYS.contains(s)) totalSkippedIdRefs++;
                }
            }
        }
        System.out.println("=== SWEEP RESULT ===");
        System.out.println("modified=" + modified + " unchanged=" + unchanged + " processFailed=" + failed
                + " reparseFail=" + reparseFail + " idViolations=" + idViolations
                + " skippedIdRefKeys=" + totalSkippedIdRefs + " totalCJK=" + totalCJK);
        System.out.println(idViolations == 0 && reparseFail == 0 ? "PASS: 安全" : "FAIL: 仍有问题");
    }

    // 提取翻译键，供统计跳过的标识符用
    private static final Set<String> LPStringTransformer_KEYS = new HashSet<>();
    static {
        try {
            byte[] b = Files.readAllBytes(Paths.get("D:\\GTNH++mod\\_lp_tools\\keys.txt"));
            String s = new String(b, StandardCharsets.UTF_8);
            for (String line : s.split("\\r?\\n")) {
                String t = line.trim();
                if (!t.isEmpty()) LPStringTransformer_KEYS.add(t);
            }
        } catch (Exception e) { }
    }

    static class Parsed {
        int count;
        int[] tags;
        byte[][] data;
        String[] utf8;
    }

    static Parsed parse(byte[] b) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
            if (in.readInt() != 0xCAFEBABE) return null;
            in.readUnsignedShort(); in.readUnsignedShort();
            int count = in.readUnsignedShort();
            Parsed p = new Parsed();
            p.count = count;
            p.tags = new int[count];
            p.data = new byte[count][];
            p.utf8 = new String[count];
            for (int i = 1; i < count; i++) {
                int t = in.readUnsignedByte();
                p.tags[i] = t;
                switch (t) {
                    case 1: { int len = in.readUnsignedShort(); p.data[i] = new byte[len]; in.readFully(p.data[i]); p.utf8[i] = new String(p.data[i], StandardCharsets.UTF_8); break; }
                    case 3: case 4: p.data[i] = new byte[4]; in.readFully(p.data[i]); break;
                    case 5: case 6: p.data[i] = new byte[8]; in.readFully(p.data[i]); i++; p.tags[i] = -1; break;
                    case 7: case 8: case 16: case 19: case 20: p.data[i] = new byte[2]; in.readFully(p.data[i]); break;
                    case 9: case 10: case 11: case 12: case 17: case 18: p.data[i] = new byte[4]; in.readFully(p.data[i]); break;
                    case 15: p.data[i] = new byte[3]; in.readFully(p.data[i]); break;
                    default: throw new RuntimeException("unknown tag " + t);
                }
            }
            return p;
        } catch (Exception e) { return null; }
    }

    static Set<Integer> identifierRefs(Parsed p) {
        Set<Integer> idRefs = new HashSet<>();
        for (int i = 1; i < p.count; i++) {
            int t = p.tags[i];
            if (t == 7 || t == 16 || t == 19 || t == 20) idRefs.add(u16(p.data[i], 0));
            else if (t == 12) { idRefs.add(u16(p.data[i], 0)); idRefs.add(u16(p.data[i], 2)); }
        }
        for (int i = 1; i < p.count; i++) {
            int t = p.tags[i];
            if (t == 9 || t == 10 || t == 11) indirect(p, idRefs, u16(p.data[i], 0), u16(p.data[i], 2));
            else if (t == 15) {
                int ref = u16(p.data[i], 1);
                if (ref < p.count && (p.tags[ref] == 9 || p.tags[ref] == 10 || p.tags[ref] == 11))
                    indirect(p, idRefs, u16(p.data[ref], 0), u16(p.data[ref], 2));
            } else if (t == 17 || t == 18) {
                int n = u16(p.data[i], 2);
                if (n < p.count && p.tags[n] == 12) { idRefs.add(u16(p.data[n], 0)); idRefs.add(u16(p.data[n], 2)); }
            }
        }
        return idRefs;
    }

    static void indirect(Parsed p, Set<Integer> idRefs, int cIdx, int nIdx) {
        if (cIdx < p.count && p.tags[cIdx] == 7) idRefs.add(u16(p.data[cIdx], 0));
        if (nIdx < p.count && p.tags[nIdx] == 12) { idRefs.add(u16(p.data[nIdx], 0)); idRefs.add(u16(p.data[nIdx], 2)); }
    }

    static int u16(byte[] d, int off) {
        return ((d[off] & 0xFF) << 8) | (d[off + 1] & 0xFF);
    }
}

