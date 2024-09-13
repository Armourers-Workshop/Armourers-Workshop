package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.ChunkSerializer;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ChunkContext {

    private static final ImmutableList<ChunkType> SIMPLE_ENCRYPT = new ImmutableList.Builder<ChunkType>()
            .add(ChunkType.SKIN_PART)
            .build();

    private boolean enablePartData = true;
    private boolean enablePreviewData = false;
    private boolean enableFastEncoder = true;

    private byte[] securityKey = null;
    private List<ChunkType> securityTypes;

    private final SkinFileOptions options;

    public ChunkContext(SkinFileOptions options) {
        this.options = options;
        try {
            this.setupWithOptions(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupWithOptions(SkinFileOptions options) throws Exception {
        // decode security key from options.
        if (options.getSecurityData() != null && options.getSecurityKey() != null) {
            securityKey = Hex.decodeHex(options.getSecurityKey().toCharArray());
            securityTypes = SIMPLE_ENCRYPT;
        }
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

    public <V, T> ChunkFlags createSerializerFlags(ChunkSerializer<V, T> serializer, V value) {
        var flags = new ChunkFlags();
        if (securityTypes != null && securityTypes.contains(serializer.getChunkType())) {
            flags.add(ChunkFlag.ENCRYPT);
        }
        return flags;
    }

    public boolean isEnablePartData() {
        return enablePartData;
    }

    public boolean isEnablePreviewData() {
        return enablePreviewData;
    }

    public void setFastEncoder(boolean enableFastEncoder) {
        this.enableFastEncoder = enableFastEncoder;
    }

    public boolean isEnableFastEncoder() {
        return enableFastEncoder;
    }

    public int getFileVersion() {
        return options.getFileVersion();
    }

    public String getSecurityData() {
        return options.getSecurityData();
    }

    public SkinFileOptions getOptions() {
        return options;
    }
}
