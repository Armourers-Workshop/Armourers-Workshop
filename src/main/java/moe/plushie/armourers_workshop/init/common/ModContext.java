package moe.plushie.armourers_workshop.init.common;


import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldSavedData;

import java.util.UUID;

public class ModContext extends WorldSavedData {

    private static UUID T0;
    private static UUID T1;

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
        ModLog.debug("Init context {}", T0);
    }

    public static void init(UUID t0, UUID t1) {
        T0 = t0;
        T1 = t1;
        ModLog.debug("Init context {}", T0);
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
}
