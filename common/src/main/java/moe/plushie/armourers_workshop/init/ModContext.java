package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedData;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedDataStorage;
import moe.plushie.armourers_workshop.compat.core.data.AbstractSavedDataType;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.OpenClock;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

public class ModContext extends AbstractSavedData {

    public static final AbstractSavedDataType<ModContext> TYPE = AbstractSavedDataType.create(ModContext::new, "ArmourersWorkshop", "capabilities");

    private static final OpenClock CLOCK = new OpenClock();
    private static ModContext CURRENT;

    private UUID t0;
    private UUID t1;
    private byte[] x0;
    private byte[] x1;

    public ModContext() {
        random();
        setDirty();
    }

    public static void init(MinecraftServer server) {
        CURRENT = AbstractSavedDataStorage.of(server).computeIfAbsent(TYPE);
    }

    public static void init(UUID t0, UUID t1) {
        CURRENT = new ModContext();
        CURRENT.apply(t0, t1);
    }

    public static void reset() {
        CURRENT = null;
    }

    public static UUID t0() {
        if (CURRENT != null) {
            return CURRENT.t0;
        }
        return null;
    }

    public static UUID t1() {
        if (CURRENT != null) {
            return CURRENT.t1;
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
        if (CURRENT != null) {
            return CURRENT.x0;
        }
        return null;
    }

    public static byte[] x1() {
        if (CURRENT != null) {
            return CURRENT.x1;
        }
        return null;
    }

    public static long time() {
        return CLOCK.time();
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
        CLOCK.setTime(serializer.read(CodingKeys.CLOCK));
        int count = 0;
        t0 = serializer.read(CodingKeys.T0);
        if (t0 != null) {
            count += 1;
        }
        t1 = serializer.read(CodingKeys.T1);
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
        serializer.write(CodingKeys.CLOCK, CLOCK.time());
        serializer.write(CodingKeys.T0, t0);
        serializer.write(CodingKeys.T1, t1);
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<UUID> T0 = IDataSerializerKey.create("t0", ExtraCodecs.UUID, null);
        public static final IDataSerializerKey<UUID> T1 = IDataSerializerKey.create("t1", ExtraCodecs.UUID, null);

        public static final IDataSerializerKey<Long> CLOCK = IDataSerializerKey.create("clock", IDataCodec.LONG, 0L);
    }
}
