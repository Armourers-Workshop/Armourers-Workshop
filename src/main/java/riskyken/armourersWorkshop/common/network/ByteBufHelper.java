package riskyken.armourersWorkshop.common.network;

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

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;

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
        
        try {
            skin.writeToStream(dataOutputStream);
            dataOutputStream.writeInt(skin.requestId);
            dataOutputStream.flush();
            
            byte[] skinData = baos.toByteArray();
            //skinData = compressedByteArray(skinData);
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
        byte[] skinData = readByteArrayFromByteBuf(buf);
        
        //skinData = decompressByteArray(skinData);
        if (skinData == null) {
            ModLogger.log(Level.ERROR, "Failed to decompress skin data.");
            return null;
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(skinData);
        DataInputStream dataInputStream = new DataInputStream(bais);
        Skin skin = null;
        
        try {
            skin = new Skin(dataInputStream);
            skin.requestId = dataInputStream.readInt();
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
            skin.writeToStream(dataOutputStream);
            dataOutputStream.writeInt(skin.requestId);
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
            skin = new Skin(dataInputStream);
            skin.requestId = dataInputStream.readInt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dataInputStream);
            IOUtils.closeQuietly(bais);
        }
        return skin;
    }
    
    private static byte[] compressedByteArray(byte[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;
        DataOutputStream dataOutputStream = null;
        
        try {
            gzos = new GZIPOutputStream(baos);
            dataOutputStream = new DataOutputStream(gzos);
            writeByteArrayToStream(dataOutputStream, data);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeQuietly(dataOutputStream);
            IOUtils.closeQuietly(gzos);
            IOUtils.closeQuietly(baos);
            return null;
        } finally {
            IOUtils.closeQuietly(dataOutputStream);
            IOUtils.closeQuietly(gzos);
        }
        
        byte[] compressedData = baos.toByteArray();
        IOUtils.closeQuietly(baos);
        
        ModLogger.log("compress - old size:" + data.length + " new size:" + compressedData.length);
        return compressedData;
    }
    
    private static byte[] decompressByteArray(byte[] compressedData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzis = null;
        DataInputStream dataInputStream = null;
        
        byte[] data;
        try {
            gzis = new GZIPInputStream(bais);
            dataInputStream = new DataInputStream(gzis);
            data = readByteArrayFromStream(dataInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            data = null;
        } finally {
            IOUtils.closeQuietly(dataInputStream);
            IOUtils.closeQuietly(gzis);
            IOUtils.closeQuietly(bais);
        }
        
        ModLogger.log("decompress - old size:" + data.length + " new size:" + compressedData.length);
        return data;
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
