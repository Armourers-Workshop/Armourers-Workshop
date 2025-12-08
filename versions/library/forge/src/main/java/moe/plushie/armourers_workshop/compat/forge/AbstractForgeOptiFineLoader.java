package moe.plushie.armourers_workshop.compat.forge;

import com.google.common.primitives.Bytes;
import moe.plushie.armourers_workshop.api.annotation.Available;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Available("[1.16, )")
public class AbstractForgeOptiFineLoader {

    /// String(0x01) + Length(0x00, 0x07) + VERSION(0x56, 0x45, 0x52, 0x53, 0x49, 0x4F, 0x4E) + String(0x01)
    private static final byte[] PATTERN = {0x01, 0x00, 0x07, 0x56, 0x45, 0x52, 0x53, 0x49, 0x4F, 0x4E, 0x01};

    /// Get the optifine version from the environment.
    public static String getVersion() {
        try {
            var clazz = (Class<?>) System.getProperties().get("optifine.OptiFineResourceLocator.class");
            if (clazz == null) {
                return null;
            }
            var method = clazz.getDeclaredMethod("getOptiFineResourceStream", String.class);
            var inputStream = (InputStream) method.invoke(clazz, "net/optifine/Config.class");
            if (inputStream == null) {
                return null;
            }
            return readVersion(inputStream);
        } catch (Exception ignored) {
            return null;
        }
    }

    ///  Get the optifine version from the byte code.
    private static String readVersion(InputStream inputStream) throws IOException {
        var size = 0;
        var chunk = new byte[8192];
        var buffer = new ByteArrayOutputStream();
        while ((size = inputStream.read(chunk)) != -1) {
            buffer.write(chunk, 0, size);
        }
        var bytes = buffer.toByteArray();
        var pos = Bytes.indexOf(bytes, PATTERN);
        if (pos == -1) {
            return null;
        }
        pos += PATTERN.length + 2;
        var length = bytes[pos - 2] << 8 | bytes[pos - 1];
        return new String(bytes, pos, length, StandardCharsets.UTF_8);
    }
}
