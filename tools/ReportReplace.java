import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.lpzhcn.LPStringTransformer;

/**
 * 报告每个被修改类的具体替换对（EN -> ZH），
 * 用于判断哪些翻译键被用在了物品/方块/注册/网络等逻辑路径。
 */
public class ReportReplace {
    public static void main(String[] args) throws Exception {
        // keys: 翻译键列表
        Set<String> keys = new HashSet<>();
        for (String line : Files.readAllLines(Paths.get(args[1]), StandardCharsets.UTF_8)) {
            String s = line.trim();
            if (!s.isEmpty()) keys.add(s);
        }
        List<Path> files = new ArrayList<>();
        Files.walk(Paths.get(args[0])).filter(p -> p.toString().endsWith(".class")).forEach(files::add);
        // 逐类扫描：找出"被 CONSTANT_String 引用且非标识符"且命中的键
        for (Path f : files) {
            if (args.length > 2 && !f.toString().contains(args[2])) continue;
            byte[] b = Files.readAllBytes(f);
            List<String> hits = scanKeys(b, keys);
            if (!hits.isEmpty()) {
                String rel = f.toString().substring(Paths.get(args[0]).toString().length() + 1);
                System.out.println("### " + rel);
                for (String h : hits) System.out.println("    " + h);
            }
        }
    }

    static List<String> scanKeys(byte[] b, Set<String> keys) {
        List<String> hits = new ArrayList<>();
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
            in.readInt(); in.readUnsignedShort(); in.readUnsignedShort();
            int count = in.readUnsignedShort();
            String[] utf8 = new String[count];
            int[] tags = new int[count];
            byte[][] data = new byte[count][];
            for (int i = 1; i < count; i++) {
                int t = in.readUnsignedByte();
                tags[i] = t;
                switch (t) {
                    case 1: {
                        int len = in.readUnsignedShort();
                        data[i] = new byte[len];
                        in.readFully(data[i]);
                        utf8[i] = new String(data[i], StandardCharsets.UTF_8);
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
            // 统计标识符引用（含间接）
            Set<Integer> idRefs = new HashSet<>();
            for (int i = 1; i < count; i++) {
                int t = tags[i];
                if (t == 7 || t == 16 || t == 19 || t == 20) idRefs.add(u16(data[i], 0));
                else if (t == 12) { idRefs.add(u16(data[i], 0)); idRefs.add(u16(data[i], 2)); }
            }
            for (int i = 1; i < count; i++) {
                int t = tags[i];
                if (t == 9 || t == 10 || t == 11) addIndirect(idRefs, tags, data, u16(data[i], 0), u16(data[i], 2));
                else if (t == 15) {
                    int ref = u16(data[i], 1);
                    if (ref < count && (tags[ref] == 9 || tags[ref] == 10 || tags[ref] == 11))
                        addIndirect(idRefs, tags, data, u16(data[ref], 0), u16(data[ref], 2));
                } else if (t == 17 || t == 18) {
                    int n = u16(data[i], 2);
                    if (n < count && tags[n] == 12) { idRefs.add(u16(data[n], 0)); idRefs.add(u16(data[n], 2)); }
                }
            }
            // 找被 String 引用的键
            for (int i = 1; i < count; i++) {
                if (tags[i] == 8) {
                    int idx = u16(data[i], 0);
                    if (idx < utf8.length && utf8[idx] != null && keys.contains(utf8[idx]) && !idRefs.contains(idx)) {
                        hits.add(utf8[idx]);
                    }
                }
            }
            return hits;
        } catch (Exception e) {
            return hits;
        }
    }

    static int u16(byte[] d, int off) {
        return ((d[off] & 0xFF) << 8) | (d[off + 1] & 0xFF);
    }

    static void addIndirect(Set<Integer> idRefs, int[] tags, byte[][] data, int cIdx, int nIdx) {
        if (cIdx < tags.length && tags[cIdx] == 7) idRefs.add(u16(data[cIdx], 0));
        if (nIdx < tags.length && tags[nIdx] == 12) {
            idRefs.add(u16(data[nIdx], 0));
            idRefs.add(u16(data[nIdx], 2));
        }
    }
}
