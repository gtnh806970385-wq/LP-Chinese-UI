import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * v2: only extracts UTF8 entries that are referenced by CONSTANT_String
 * (i.e. real string literals in bytecode), not identifiers/descriptors.
 */
public class ClassStringScanner {
    public static void main(String[] args) throws Exception {
        Path root = Paths.get(args[0]);
        Set<String> interesting = new TreeSet<>();
        List<Path> files = new ArrayList<>();
        try (var stream = Files.walk(root)) {
            stream.filter(p -> p.toString().endsWith(".class")).forEach(files::add);
        }
        for (Path f : files) {
            try {
                for (String s : parseStringConstants(Files.readAllBytes(f))) {
                    if (isUserText(s)) interesting.add(s);
                }
            } catch (Exception ignored) {}
        }
        for (String s : interesting) System.out.println(s);
        System.err.println("TOTAL_INTERESTING=" + interesting.size());
    }

    static List<String> parseStringConstants(byte[] b) throws Exception {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
        in.readInt(); // magic
        in.readUnsignedShort(); in.readUnsignedShort(); // minor, major
        int count = in.readUnsignedShort();
        String[] utf = new String[count];
        boolean[] isStringRef = new boolean[count];
        for (int i = 1; i < count; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1: {
                    int len = in.readUnsignedShort();
                    byte[] bytes = new byte[len];
                    in.readFully(bytes);
                    utf[i] = new String(bytes, StandardCharsets.UTF_8);
                    break;
                }
                case 3: case 4: in.readInt(); break;
                case 5: case 6: in.readLong(); i++; break;
                case 7: case 16: case 19: case 20: in.readUnsignedShort(); break;
                case 8: isStringRef[in.readUnsignedShort()] = true; break; // CONSTANT_String
                case 9: case 10: case 11: case 12: case 17: case 18: in.readUnsignedShort(); in.readUnsignedShort(); break;
                case 15: in.readUnsignedByte(); in.readUnsignedShort(); break;
                default: throw new RuntimeException("unknown tag " + tag);
            }
        }
        List<String> out = new ArrayList<>();
        for (int i = 1; i < count; i++) {
            if (isStringRef[i] && utf[i] != null) out.add(utf[i]);
        }
        return out;
    }

    static boolean isUserText(String s) {
        if (s == null || s.length() < 2 || s.length() > 120) return false;
        if (s.indexOf('/') >= 0 || s.indexOf(';') >= 0 || s.indexOf('(') >= 0) return false;
        if (s.startsWith("L") && s.endsWith(";")) return false;
        if (s.startsWith("gui.") || s.startsWith("item.") || s.startsWith("lp.") || s.startsWith("misc.")
            || s.startsWith("tooltip.") || s.startsWith("tile.") || s.startsWith("itemGroup.")) return false; // lang keys
        if (s.matches(".*\\\\u[0-9a-fA-F]{4}.*")) return false;
        // skip package names / version-like / url / java refs
        if (s.matches("[a-zA-Z0-9_.$\\[\\]<>]*\\.[a-zA-Z][a-zA-Z0-9]*") && s.indexOf(' ') < 0 && s.indexOf(' ') < 0) {
            // might be method calls like "getName" - keep only if contains space or starts uppercase and not ending with () pattern
        }
        int letters = 0;
        for (char c : s.toCharArray()) if (Character.isLetter(c)) letters++;
        if (letters < 2) return false;
        return true;
    }
}
