package moe.plushie.armourers_workshop.utils;

import net.minecraft.tileentity.TileEntity;

import java.util.IdentityHashMap;
import java.util.Map;

public class TileEntityUpdateCombiner {

    private static final ThreadLocal<Map<TileEntity, Runnable>> pending = ThreadLocal.withInitial(() -> null);

    public static void begin() {
        Map<TileEntity, Runnable> queue = pending.get();
        if (queue == null) {
            pending.set(new IdentityHashMap<>());
        }
    }

    public static void end() {
        Map<TileEntity, Runnable> queue = pending.get();
        if (queue == null) {
            return;
        }
        queue.forEach((k, v) -> {
            v.run();
            k.setChanged();
        });
        pending.set(null);
    }

    public static <T extends TileEntity> void combine(T tileEntity, Runnable handler) {
        Map<TileEntity, Runnable> queue = pending.get();
        if (queue == null) {
            handler.run();
            tileEntity.setChanged();
            return;
        }
        queue.put(tileEntity, handler);
    }
}
