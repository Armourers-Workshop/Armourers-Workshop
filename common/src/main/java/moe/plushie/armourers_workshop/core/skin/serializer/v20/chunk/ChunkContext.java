package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

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

    public byte[] securityKey = null;

    public boolean enablePartData = true;

    public boolean enablePreviewData = false;

    private boolean allowsFastEncoder = true;

    private final int fileVersion;

    public ChunkContext(int fileVersion) {
        this.fileVersion = fileVersion;
    }

    public InputStream createInputStream(ByteBuf buf, ChunkFlags flags) throws IOException {
        InputStream inputStream = new ByteBufInputStream(buf);
        if (flags.contains(ChunkFlag.ENCRYPT)) {
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
        if (flags.contains(ChunkFlag.GZIP)) {
            inputStream = new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    public OutputStream createOutputStream(ByteBuf buf, ChunkFlags flags) throws IOException {
        OutputStream outputStream = new ByteBufOutputStream(buf);
        if (flags.contains(ChunkFlag.ENCRYPT)) {
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
        if (flags.contains(ChunkFlag.GZIP)) {
            outputStream = new GZIPOutputStream(outputStream);
        }
        return outputStream;
    }

    public boolean isEnablePartData() {
        return enablePartData;
    }

    public boolean isEnablePreviewData() {
        return enablePreviewData;
    }

    public void setFastEncoder(boolean allowsFastEncoder) {
        this.allowsFastEncoder = allowsFastEncoder;
    }

    public boolean allowsFastEncoder() {
        return allowsFastEncoder;
    }

    public int getVersion() {
        return fileVersion;
    }
}
