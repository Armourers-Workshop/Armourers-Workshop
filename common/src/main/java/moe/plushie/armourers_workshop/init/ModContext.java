package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.compatibility.core.AbstractSavedData;
import moe.plushie.armourers_workshop.core.data.TickTracker;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

public class ModContext extends AbstractSavedData {

    private static final DataSerializerKey<UUID> T0_KEY = DataSerializerKey.create("t0", DataTypeCodecs.UUID, null);
    private static final DataSerializerKey<UUID> T1_KEY = DataSerializerKey.create("t1", DataTypeCodecs.UUID, null);

    private static ModContext current;

    private UUID t0;
    private UUID t1;
    private byte[] x0;
    private byte[] x1;

    public ModContext() {
        random();
        setDirty();
    }

    public static void init(MinecraftServer server) {
        current = server.overworld().getDataStorage().computeIfAbsent(ModContext::new, 0, Constants.Key.SKIN);
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

    public static UUID t2(UUID p) {
        return md5(Objects.requireNonNull(t0()), p);
    }

    public static UUID t3(UUID p) {
        return md5(Objects.requireNonNull(t1()), p);
    }

    public static byte[] x0() {
        if (current != null) {
            return current.x0;
        }
        return null;
    }

    public static byte[] x1() {
        if (current != null) {
            return current.x1;
        }
        return null;
    }

    @NotNull
    private static UUID md5(UUID v1, UUID v2) {
        int v0 = 20220616;
        ByteBuffer buffer0 = ByteBuffer.allocate(8 * Long.BYTES);
        buffer0.putLong(v0 + 0xe08e99f7);
        buffer0.putLong(v1.getLeastSignificantBits());
        buffer0.putLong(v0 + 0x9ee714d5);
        buffer0.putLong(v2.getMostSignificantBits());
        buffer0.putLong(v0 + 0x3cf6f6ac);
        buffer0.putLong(v1.getLeastSignificantBits());
        buffer0.putLong(v0 + 0x6c8caf3c);
        buffer0.putLong(v2.getLeastSignificantBits());
        return UUID.nameUUIDFromBytes(buffer0.array());
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
    public void deserialize(IDataSerializer serializer) {
        TickTracker.server().deserialize(serializer);
        int count = 0;
        t0 = serializer.read(T0_KEY);
        if (t0 != null) {
            count += 1;
        }
        t1 = serializer.read(T1_KEY);
        if (t1 != null) {
            count += 1;
        }
        if (count != 2) {
            random();
        }
        apply(t0, t1);
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        TickTracker.server().serialize(serializer);
        serializer.write(T0_KEY, t0);
        serializer.write(T1_KEY, t1);
    }
}
