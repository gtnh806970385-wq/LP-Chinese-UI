import java.io.*;
import java.nio.file.*;
import com.lpzhcn.LPStringTransformer;

/** 单类调试：调用 LPStringTransformer.process()，打印完整异常堆栈，输出产物供 javap 验证。 */
public class DebugLP {
    public static void main(String[] args) throws Exception {
        Path in = Paths.get(args[0]);
        byte[] b = Files.readAllBytes(in);
        System.out.println("input bytes: " + b.length);
        byte[] out = null;
        try {
            out = LPStringTransformer.process(b);
        } catch (Throwable t) {
            t.printStackTrace();
            return;
        }
        if (out == null) {
            System.out.println("RESULT: no change");
            return;
        }
        System.out.println("RESULT: changed, output bytes: " + out.length);
        if (args.length > 1) Files.write(Paths.get(args[1]), out);
    }
}
