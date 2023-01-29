package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ChunkContext {

    private byte[] securityKey = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    public InputStream createInputStream(ByteBuf buf, int flags) throws IOException {
        InputStream inputStream = new ByteBufInputStream(buf);
        if ((flags & 0x01) != 0) {
            if (securityKey == null || securityKey.length == 0) {
                throw new IOException("missing security key!!");
            }
            try {
                SecretKeySpec key = new SecretKeySpec(securityKey, "AES");
                Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aes.init(Cipher.DECRYPT_MODE, key);
                inputStream = new CipherInputStream(inputStream, aes);
            } catch (Exception e) {
                // continue throwing error of io exception.
                throw new IOException(e);
            }
        }
        if ((flags & 0x02) != 0) {
            inputStream = new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    public OutputStream createOutputStream(ByteBuf buf, int flags) throws IOException {
        OutputStream outputStream = new ByteBufOutputStream(buf);
        if ((flags & 0x01) != 0) {
            if (securityKey == null || securityKey.length == 0) {
                throw new IOException("missing security key!!");
            }
            try {
                SecretKeySpec key = new SecretKeySpec(securityKey, "AES");
                Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aes.init(Cipher.ENCRYPT_MODE, key);
                outputStream = new CipherOutputStream(outputStream, aes);
            } catch (Exception exception) {
                // continue throwing error of io exception.
                throw new IOException(exception);
            }
        }
        if ((flags & 0x02) != 0) {
            outputStream = new GZIPOutputStream(outputStream);
        }
        return outputStream;
    }
}
