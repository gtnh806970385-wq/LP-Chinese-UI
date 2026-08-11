import java.io.*;
import java.nio.file.*;

/** 逐条打印 class 常量池（tag + 内容长度），定位重写后的错位点。 */
public class DumpCP {
    public static void main(String[] args) throws Exception {
        byte[] b = Files.readAllBytes(Paths.get(args[0]));
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
        int magic = in.readInt();
        int minor = in.readUnsignedShort();
        int major = in.readUnsignedShort();
        int count = in.readUnsignedShort();
        System.out.printf("magic=%08x minor=%d major=%d count=%d file=%d bytes%n", magic, minor, major, count, b.length);
        long pos = 8;
        for (int i = 1; i < count; i++) {
            int t = in.readUnsignedByte();
            int len;
            switch (t) {
                case 1: len = in.readUnsignedShort(); in.readFully(new byte[len]); break;
                case 3: case 4: len = 4; in.readFully(new byte[4]); break;
                case 5: case 6: len = 8; in.readFully(new byte[8]); i++; break;
                case 7: case 8: case 16: case 19: case 20: len = 2; in.readFully(new byte[2]); break;
                case 9: case 10: case 11: case 12: case 17: case 18: len = 4; in.readFully(new byte[4]); break;
                case 15: len = 3; in.readFully(new byte[3]); break;
                default: System.out.println("BAD TAG " + t + " at index " + i + " offset " + pos); return;
            }
            if (i <= 5 || i % 200 == 0 || i > count - 5) {
                System.out.printf("idx=%d tag=%d len=%d%n", i, t, len);
            }
        }
        System.out.println("pool parsed OK, remaining=" + in.available() + " bytes");
    }
}
