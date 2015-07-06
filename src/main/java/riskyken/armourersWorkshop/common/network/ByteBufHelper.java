package riskyken.armourersWorkshop.common.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.exception.NewerFileVersionException;
import riskyken.armourersWorkshop.common.skin.data.Skin;

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
    
    public static void writeSkinToByteBuf(ByteBuf buf, Skin skin) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        try {
            skin.writeToStream(out);
            out.writeInt(skin.requestId);
            out.close();
        } catch (IOException e2) {
            e2.printStackTrace();
            return;
        }
        byte[] skinData = baos.toByteArray();
        buf.writeInt(skinData.length);
        buf.writeBytes(skinData);
    }
    
    public static Skin readSkinFromByteBuf(ByteBuf buf) {
        int size = buf.readInt();
        byte[] skinData = new byte[size];
        buf.readBytes(skinData);
        ByteArrayInputStream bais = new ByteArrayInputStream(skinData);
        DataInputStream input = new DataInputStream(bais);
        Skin skin = null;
        try {
            skin = new Skin(input);
            skin.requestId = input.readInt();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            e.printStackTrace();
        } catch (InvalidCubeTypeException e) {
            e.printStackTrace();
        }
        return skin;
    }
}
