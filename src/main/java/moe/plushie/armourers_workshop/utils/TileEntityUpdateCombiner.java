package moe.plushie.armourers_workshop.utils;

import net.minecraft.tileentity.TileEntity;

import java.util.IdentityHashMap;
import java.util.Map;

public class TileEntityUpdateCombiner {

    private static boolean started = false;
    private static final Map<TileEntity, Runnable> pending = new IdentityHashMap<>();

    public static void begin() {
        started = true;
    }

    public static void end() {
        started = false;
        pending.values().forEach(Runnable::run);
        pending.clear();
    }

    public static <T extends TileEntity> void combine(T tileEntity, Runnable handler) {
        tileEntity.setChanged();
        pending.put(tileEntity, handler);
        if (!started) {
            end();
        }
    }
}
