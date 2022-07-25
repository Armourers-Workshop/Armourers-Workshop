package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

public class ModContext extends SavedData {

    private static ModContext current;

    private UUID t0;
    private UUID t1;
    private byte[] x0;
    private byte[] x1;

    public ModContext() {
        super(Constants.Key.SKIN);
        random();
        setDirty();
    }

    public static void init(MinecraftServer server) {
        current = server.overworld().getDataStorage().computeIfAbsent(ModContext::new, Constants.Key.SKIN);
    }

    public static void init(UUID t0, UUID t1) {
        current = new ModContext();
        current.apply(t0, t1);
    }

    public static void reset() {
        current = null;
    }

    public static UUID t0() {
        if (current != null) {
            return current.t0;
        }
        return null;
    }

    public static UUID t1() {
        if (current != null) {
            return current.t1;
        }
        return null;
    }

    @Nullable
    public static byte[] x0() {
        if (current != null) {
            return current.x0;
        }
        return null;
    }

    @Nullable
    public static byte[] x1() {
        if (current != null) {
            return current.x1;
        }
        return null;
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

    private void apply(UUID t0, UUID t1) {
        ModLog.debug("apply context");
        this.t0 = t0;
        this.t1 = t1;
        this.x0 = null;
        this.x1 = null;
        if (t0 == null || t1 == null) {
            return;
        }
        try {
            int v0 = 20220616;
            MessageDigest md = MessageDigest.getInstance("MD5");
            ByteBuffer buffer0 = ByteBuffer.allocate(8 * Long.BYTES);
            ByteBuffer buffer1 = ByteBuffer.allocate(24 * Long.BYTES);
            buffer0.putLong(v0 + 0xe08e99f7);
            buffer0.putLong(t0.getLeastSignificantBits());
            buffer0.putLong(v0 + 0x9ee714d5);
            buffer0.putLong(t0.getMostSignificantBits());
            buffer0.putLong(v0 + 0x3cf6f6ac);
            buffer0.putLong(t1.getLeastSignificantBits());
            buffer0.putLong(v0 + 0x6c8caf3c);
            x0 = md.digest(buffer0.array());
            buffer1.putLong(v0 + 0xe08e99f9);
            buffer1.putLong(t1.getMostSignificantBits());
            buffer1.putLong(t0.getLeastSignificantBits());
            buffer1.putLong(v0 + 0x9ee714d5);
            buffer1.put(x0);
            buffer1.putLong(v0 + 0x3cf6f6ac);
            buffer1.putLong(t1.getLeastSignificantBits());
            buffer1.putLong(t0.getMostSignificantBits());
            buffer1.putLong(v0 + 0x6c8caf3c);
            x1 = md.digest(buffer1.array());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void random() {
        t0 = UUID.randomUUID();
        t1 = UUID.randomUUID();
    }

    @Override
    public void load(CompoundTag nbt) {
        int count = 0;
        if (nbt.hasUUID("t0")) {
            t0 = nbt.getUUID("t0");
            count += 1;
        }
        if (nbt.hasUUID("t1")) {
            t1 = nbt.getUUID("t1");
            count += 1;
        }
        if (count != 2) {
            random();
            setDirty();
        }
        apply(t0, t1);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putUUID("t0", t0);
        nbt.putUUID("t1", t1);
        return nbt;
    }
}
