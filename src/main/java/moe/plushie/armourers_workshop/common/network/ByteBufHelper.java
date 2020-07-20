package moe.plushie.armourers_workshop.common.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.data.serialize.SkinIdentifierSerializer;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public final class ByteBufHelper {

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        long mostSigBits = buf.readLong();
        long leastSigBits = buf.readLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static void writeStringArrayToBuf(ByteBuf buf, String[] strings) {
        buf.writeInt(strings.length);
        for (int i = 0; i < strings.length; i++) {
            ByteBufUtils.writeUTF8String(buf, strings[i]);
        }
    }

    public static String[] readStringArrayFromBuf(ByteBuf buf) {
        int size = buf.readInt();
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = ByteBufUtils.readUTF8String(buf);
        }
        return strings;
    }

    public static void writeSkinToByteBuf(ByteBuf buf, Skin skin) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        boolean compress = ConfigHandler.serverCompressesSkins;
        buf.writeBoolean(compress);
        try {
            SkinSerializer.writeToStream(skin, dataOutputStream);
            SkinIdentifierSerializer.writeToStream(skin.requestId, dataOutputStream);
            dataOutputStream.flush();

            byte[] skinData = baos.toByteArray();
            if (compress) {
                skinData = compressedByteArray(skinData);
            }
            if (skinData == null) {
                ModLogger.log(Level.ERROR, "Failed to compress skin data.");
                return;
            }

            writeByteArrayToByteBuf(buf, skinData);

        } catch (IOException e2) {
            e2.printStackTrace();
            return;
        } finally {
            IOUtils.closeQuietly(dataOutputStream);
            IOUtils.closeQuietly(baos);
        }
    }

    public static Skin readSkinFromByteBuf(ByteBuf buf) {
        boolean compressed = buf.readBoolean();
        byte[] skinData = readByteArrayFromByteBuf(buf);

        if (compressed) {
            skinData = decompressByteArray(skinData);
        }

        if (skinData == null) {
            ModLogger.log(Level.ERROR, "Failed to decompress skin data.");
            return null;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(skinData);
        DataInputStream dataInputStream = new DataInputStream(bais);
        Skin skin = null;

        try {
            skin = SkinSerializer.readSkinFromStream(dataInputStream);
            skin.requestId = SkinIdentifierSerializer.readFromStream(dataInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dataInputStream);
            IOUtils.closeQuietly(bais);
        }

        return skin;
    }

    public static byte[] convertSkinToByteArray(Skin skin) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        byte[] skinData = null;
        try {
            SkinSerializer.writeToStream(skin, dataOutputStream);
            SkinIdentifierSerializer.writeToStream(skin.requestId, dataOutputStream);
            dataOutputStream.flush();
            skinData = baos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dataOutputStream);
            IOUtils.closeQuietly(baos);
        }
        return skinData;
    }

    public static Skin convertByteArrayToSkin(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(bais);
        Skin skin = null;
        try {
            skin = SkinSerializer.readSkinFromStream(dataInputStream);
            skin.requestId = SkinIdentifierSerializer.readFromStream(dataInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dataInputStream);
            IOUtils.closeQuietly(bais);
        }
        return skin;
    }

    public static byte[] compressedByteArray(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;

        try {
            gzos = new GZIPOutputStream(baos);
            gzos.write(data);
            gzos.close();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeQuietly(gzos);
            IOUtils.closeQuietly(baos);
            return null;
        } finally {
            IOUtils.closeQuietly(gzos);
            IOUtils.closeQuietly(baos);
        }

        byte[] compressedData = baos.toByteArray();

        // ModLogger.log("compress - old size:" + data.length + " new size:" +
        // compressedData.length);
        // ModLogger.log("compression ratio:" + String.format("%.2f",
        // ((float)compressedData.length / (float)data.length) * 100F) + "%%");
        return compressedData;
    }

    public static byte[] decompressByteArray(byte[] compressedData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream gzis = null;

        byte[] data;
        try {
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(compressedData)), baos);
        } catch (IOException e) {
            e.printStackTrace();
            data = null;
        } finally {
            IOUtils.closeQuietly(gzis);
            IOUtils.closeQuietly(baos);
        }

        // ModLogger.log("decompress - old size:" + baos.size() + " new size:" +
        // compressedData.length);
        return baos.toByteArray();
    }

    public static void writeByteArrayToByteBuf(ByteBuf buf, byte[] data) {
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static byte[] readByteArrayFromByteBuf(ByteBuf buf) {
        int size = buf.readInt();
        byte[] data = new byte[size];
        buf.readBytes(data);
        return data;
    }

    private static void writeByteArrayToStream(DataOutputStream dataOutputStream, byte[] data) throws IOException {
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
    }

    private static byte[] readByteArrayFromStream(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        byte[] data = new byte[size];
        dataInputStream.read(data);
        return data;
    }
}
