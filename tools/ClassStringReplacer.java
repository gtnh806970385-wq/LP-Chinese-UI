import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Replaces exact-match constant-pool UTF8 strings in .class files using a
 * translation table (english=chinese per line, '#' = comment).
 * Rebuilds the constant pool so variable-length UTF8 entries are safe.
 * Usage: java ClassStringReplacer <translations.txt> <class-dir>
 */
public class ClassStringReplacer {
    static final Map<String, String> trans = new HashMap<>();
    static int replaced = 0;

    public static void main(String[] args) throws Exception {
        Path transFile = Paths.get(args[0]);
        Path root = Paths.get(args[1]);
        for (String line : Files.readAllLines(transFile, StandardCharsets.UTF_8)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            trans.put(line.substring(0, eq), line.substring(eq + 1));
        }
        System.err.println("translations loaded: " + trans.size());
        List<Path> files = new ArrayList<>();
        try (var stream = Files.walk(root)) {
            stream.filter(p -> p.toString().endsWith(".class")).forEach(files::add);
        }
        for (Path f : files) {
            try {
                byte[] out = process(Files.readAllBytes(f));
                if (out != null) {
                    Files.write(f, out);
                    System.err.println("modified: " + f);
                }
            } catch (Exception e) {
                System.err.println("SKIP " + f + ": " + e);
            }
        }
        System.err.println("TOTAL_REPLACED=" + replaced);
    }

    static byte[] process(byte[] b) throws Exception {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
        in.readInt(); // magic
        in.readUnsignedShort(); // minor
        in.readUnsignedShort(); // major
        int count = in.readUnsignedShort();
        List<Integer> tags = new ArrayList<>(count);
        List<byte[]> datas = new ArrayList<>(count);
        boolean changed = false;
        for (int i = 1; i < count; i++) {
            int tag = in.readUnsignedByte();
            tags.add(tag);
            byte[] data;
            switch (tag) {
                case 1: {
                    int len = in.readUnsignedShort();
                    byte[] bytes = new byte[len];
                    in.readFully(bytes);
                    String s = new String(bytes, StandardCharsets.UTF_8);
                    String r = trans.get(s);
                    ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                    if (r != null) {
                        byte[] nb = r.getBytes(StandardCharsets.UTF_8);
                        if (nb.length > 65535) throw new RuntimeException("translation too long: " + r);
                        tmp.write((nb.length >> 8) & 0xFF);
                        tmp.write(nb.length & 0xFF);
                        tmp.write(nb);
                        replaced++;
                        changed = true;
                    } else {
                        tmp.write((len >> 8) & 0xFF);
                        tmp.write(len & 0xFF);
                        tmp.write(bytes);
                    }
                    data = tmp.toByteArray();
                    break;
                }
                case 3: case 4: {
                    data = new byte[4]; in.readFully(data); break;
                }
                case 5: case 6: {
                    data = new byte[8]; in.readFully(data); i++; break;
                }
                case 7: case 8: case 16: case 19: case 20: {
                    data = new byte[2]; in.readFully(data); break;
                }
                case 9: case 10: case 11: case 12: case 17: case 18: {
                    data = new byte[4]; in.readFully(data); break;
                }
                case 15: {
                    data = new byte[3]; in.readFully(data); break;
                }
                default: throw new RuntimeException("unknown tag " + tag);
            }
            datas.add(data);
        }
        if (!changed) return null;
        byte[] rest = new byte[in.available()];
        in.readFully(rest);
        ByteArrayOutputStream out = new ByteArrayOutputStream(b.length + 256);
        DataOutputStream d = new DataOutputStream(out);
        d.writeInt(0xCAFEBABE);
        d.writeShort(((b[4] & 0xFF) << 8) | (b[5] & 0xFF));
        d.writeShort(((b[6] & 0xFF) << 8) | (b[7] & 0xFF));
        d.writeShort(count);
        for (int i = 0; i < tags.size(); i++) {
            d.writeByte(tags.get(i));
            d.write(datas.get(i));
        }
        d.write(rest);
        return out.toByteArray();
    }
}
