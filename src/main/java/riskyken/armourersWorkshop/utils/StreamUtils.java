package riskyken.armourersWorkshop.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.Charsets;

public final class StreamUtils {
    
    private StreamUtils() {}
    
    public static void writeString(DataOutputStream stream, Charset charset, String string) throws IOException {
        byte[] bytes = string.getBytes(Charsets.UTF_8);
        stream.writeInt(bytes.length);
        stream.write(bytes);
    }
    
    public static String readString(DataInputStream stream, Charset charset) throws IOException {
        int size = stream.readInt();
        byte[] bytes = new byte[size];
        stream.read(bytes, 0, size);
        return new String(bytes, charset);
    }
    
    public static void writeStringUtf8(DataOutputStream stream, String string) throws IOException {
        writeString(stream, Charsets.UTF_8, string);
    }
    
    public static String readStringUtf8(DataInputStream stream) throws IOException {
        return readString(stream, Charsets.UTF_8);
    }
}
