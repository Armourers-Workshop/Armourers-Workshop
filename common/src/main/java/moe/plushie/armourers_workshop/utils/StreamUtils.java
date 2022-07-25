package moe.plushie.armourers_workshop.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * because `commons.io` versions on some servers are too low,
 * so we can't directly reference `common.io` in the other source code.
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    public static void writeString(DataOutputStream stream, Charset charset, String string) throws IOException {
        byte[] bytes = string.getBytes(charset);
        int size = bytes.length;
        writeUnsignedShort(stream, size);
        stream.write(bytes);
    }

    public static String readString(DataInputStream stream, Charset charset) throws IOException {
        int size = readUnsignedShort(stream);
        byte[] bytes = new byte[size];
        stream.readFully(bytes, 0, size);
        return new String(bytes, charset);
    }

    public static void writeStringUtf8(DataOutputStream stream, String string) throws IOException {
        writeString(stream, StandardCharsets.UTF_8, string);
    }

    public static void writeStringAscii(DataOutputStream stream, String string) throws IOException {
        writeString(stream, StandardCharsets.US_ASCII, string);
    }

    public static String readStringUtf8(DataInputStream stream) throws IOException {
        return readString(stream, StandardCharsets.UTF_8);
    }

    public static String readStringAscii(DataInputStream stream) throws IOException {
        return readString(stream, StandardCharsets.US_ASCII);
    }

    public static int readBuffer(InputStream stream, byte[] buffer, int offset, int length) throws IOException {
        int index = 0;
        while (index < length) {
            int readSize = stream.read(buffer, offset + index, length - index);
            if (readSize <= 0) {
                throw new EOFException();
            }
            index += length;
        }
        return index;
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        return IOUtils.toByteArray(input);
    }

    public static String toString(final InputStream input, final Charset encoding) throws IOException {
        return IOUtils.toString(input, encoding);
    }

    public static void closeQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (final IOException ioe) {
                // ignore
            }
        }
    }

    private static void writeUnsignedShort(DataOutputStream stream, int value) throws IOException {
        if (value > 65535) {
            throw new IOException("String is over the max length allowed.");
        }
        stream.writeShort((short) value);
    }

    private static int readUnsignedShort(DataInputStream stream) throws IOException {
        return stream.readShort() & 0xFFFF;
    }
}
