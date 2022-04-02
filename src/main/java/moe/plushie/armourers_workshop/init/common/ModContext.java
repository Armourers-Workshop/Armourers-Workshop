package moe.plushie.armourers_workshop.init.common;


import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.commons.codec.binary.Hex;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ModContext extends WorldSavedData {

    private static UUID T0;
    private static UUID T1;
    private static byte[] X0;
    private static byte[] X1;

    public ModContext() {
        super(AWConstants.NBT.SKIN);
        T0 = UUID.randomUUID();
        T1 = UUID.randomUUID();
        setDirty();
    }

    public static UUID t0() {
        return T0;
    }

    public static UUID t1() {
        return T1;
    }

    public static void init(MinecraftServer server) {
        server.overworld().getDataStorage().computeIfAbsent(ModContext::new, AWConstants.NBT.SKIN);
        init(T0, T1);
    }

    public static void init(UUID t0, UUID t1) {
        ModLog.debug("init context");
        T0 = t0;
        T1 = t1;
        X0 = null;
        X1 = null;
        if (T0 == null || T1 == null) {
            return;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            ByteBuffer buffer0 = ByteBuffer.allocate(8 * Long.BYTES);
            ByteBuffer buffer1 = ByteBuffer.allocate(24 * Long.BYTES);
            buffer0.putLong(0xe08e99f9);
            buffer0.putLong(T0.getLeastSignificantBits());
            buffer0.putLong(0x9ee714d5);
            buffer0.putLong(T0.getMostSignificantBits());
            buffer0.putLong(0x3cf6f6ac);
            buffer0.putLong(T1.getMostSignificantBits());
            buffer0.putLong(0x6c8caf3c);
            X0 = md.digest(buffer0.array());
            buffer1.putLong(0xe08e99f9);
            buffer1.putLong(T1.getMostSignificantBits());
            buffer1.putLong(T0.getLeastSignificantBits());
            buffer1.putLong(0x9ee714d5);
            buffer1.put(X0);
            buffer1.putLong(0x3cf6f6ac);
            buffer1.putLong(T1.getMostSignificantBits());
            buffer1.putLong(T0.getLeastSignificantBits());
            buffer1.putLong(0x6c8caf3c);
            X1 = md.digest(buffer1.array());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        T0 = null;
        T1 = null;
        X0 = null;
        X1 = null;
    }

    @Override
    public void load(CompoundNBT nbt) {
        int count = 0;
        if (nbt.hasUUID("t0")) {
            T0 = nbt.getUUID("t0");
            count += 1;
        }
        if (nbt.hasUUID("t1")) {
            T1 = nbt.getUUID("t1");
            count += 1;
        }
        setDirty(count != 2);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putUUID("t0", T0);
        nbt.putUUID("t1", T1);
        return nbt;
    }

    @Nullable
    public static byte[] x0() {
        return X0;
    }

    @Nullable
    public static byte[] x1() {
        return X1;
    }

    public static String md5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] sig = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return new String(Hex.encodeHex(sig, true));
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }
}
