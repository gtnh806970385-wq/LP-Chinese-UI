import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * 检查 LPStringTransformer 的翻译键是否与 class 常量池中的标识符引用冲突。
 * 若某翻译串同时被 CONSTANT_Class(类名)/CONSTANT_NameAndType(方法/字段名+描述符)
 * /CONSTANT_MethodType/CONSTANT_Module/CONSTANT_Package 引用，则替换会破坏该类。
 */
public class LPStringCollision {

    public static void main(String[] args) throws Exception {
        Set<String> keys = new HashSet<>();
        for (String line : Files.readAllLines(Paths.get(args[0]), StandardCharsets.UTF_8)) {
            String s = line.trim();
            if (!s.isEmpty()) keys.add(s);
        }
        System.out.println("Translation keys: " + keys.size());

        List<Path> files = new ArrayList<>();
        Files.walk(Paths.get(args[1])).filter(p -> p.toString().endsWith(".class")).forEach(files::add);
        System.out.println("Class files: " + files.size());

        int collisionClasses = 0;
        int modifiedClasses = 0;
        int totalCollisions = 0;
        int keyMatchedUtf8s = 0;
        for (Path f : files) {
            byte[] b = Files.readAllBytes(f);
            Parsed p = parse(b);
            if (p == null) {
                System.out.println("PARSE-FAIL: " + f);
                continue;
            }
            // which utf8 indices are used as identifiers?
            boolean changed = false;
            List<String> coll = new ArrayList<>();
            Set<Integer> idRefs = new HashSet<>();
            for (int i = 1; i < p.count; i++) {
                int t = p.tags[i];
                if (t == 7 || t == 16 || t == 19 || t == 20) { // Class/MethodType/Module/Package -> utf8
                    int idx = ((p.data[i][0] & 0xFF) << 8) | (p.data[i][1] & 0xFF);
                    idRefs.add(idx);
                } else if (t == 12) { // NameAndType -> name utf8 + desc utf8
                    int ni = ((p.data[i][0] & 0xFF) << 8) | (p.data[i][1] & 0xFF);
                    int di = ((p.data[i][2] & 0xFF) << 8) | (p.data[i][3] & 0xFF);
                    idRefs.add(ni);
                    idRefs.add(di);
                }
            }
            // check keys against identifier-referenced utf8s
            for (int idx : idRefs) {
                String s = idx < p.utf8.length ? p.utf8[idx] : null;
                if (s != null && keys.contains(s)) {
                    coll.add(s + " (utf8#" + idx + " used as identifier)");
                }
            }
            // also count how many key-matching utf8 exist at all in this class (string or not)
            for (int i = 1; i < p.count; i++) {
                if (p.utf8[i] != null && keys.contains(p.utf8[i])) {
                    keyMatchedUtf8s++;
                    // does this class also have an actual CONSTANT_String referencing a key? if so it would be transformed
                }
            }
            // detect whether process() would change the class (any CONSTANT_String-referenced key)
            boolean hasStringKey = false;
            for (int i = 1; i < p.count; i++) {
                if (p.tags[i] == 8) { // String -> utf8
                    int idx = ((p.data[i][0] & 0xFF) << 8) | (p.data[i][1] & 0xFF);
                    if (idx < p.utf8.length && p.utf8[idx] != null && keys.contains(p.utf8[idx])) {
                        hasStringKey = true;
                        break;
                    }
                }
            }
            if (hasStringKey) modifiedClasses++;
            if (!coll.isEmpty()) {
                collisionClasses++;
                totalCollisions += coll.size();
                System.out.println("COLLISION in " + f.getFileName() + " [" + f.getParent().getFileName() + "]: " + coll);
            }
        }
        System.out.println("Classes that would be modified by transformer: " + modifiedClasses);
        System.out.println("Classes with identifier collisions: " + collisionClasses + " (total collisions=" + totalCollisions + ")");
        System.out.println("Key-matched utf8 entries total: " + keyMatchedUtf8s);
        System.out.println("DONE");
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
            int magic = in.readInt();
            if (magic != 0xCAFEBABE) return null;
            in.readUnsignedShort();
            in.readUnsignedShort();
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
                    case 1: {
                        int len = in.readUnsignedShort();
                        p.data[i] = new byte[len];
                        in.readFully(p.data[i]);
                        p.utf8[i] = new String(p.data[i], StandardCharsets.UTF_8);
                        break;
                    }
                    case 3: case 4: p.data[i] = new byte[4]; in.readFully(p.data[i]); break;
                    case 5: case 6: p.data[i] = new byte[8]; in.readFully(p.data[i]); i++; p.tags[i] = -1; break;
                    case 7: case 8: case 16: case 19: case 20: p.data[i] = new byte[2]; in.readFully(p.data[i]); break;
                    case 9: case 10: case 11: case 12: case 17: case 18: p.data[i] = new byte[4]; in.readFully(p.data[i]); break;
                    case 15: p.data[i] = new byte[3]; in.readFully(p.data[i]); break;
                    default: throw new RuntimeException("unknown tag " + t);
                }
            }
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}
