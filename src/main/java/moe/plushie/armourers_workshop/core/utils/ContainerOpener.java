package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public final class ContainerOpener {

    private ContainerOpener() {
    }

    private static final Map<ContainerType<? extends Container>, Opener<?>> registry = new HashMap<>();

    public static <T extends Container, I> void register(ContainerType<T> type, Opener<I> opener) {
        registry.put(type, opener);
    }

    @SuppressWarnings("unchecked")
    public static <I> boolean open(ContainerType<?> type, PlayerEntity player, I host) {
        Opener<I> opener = (Opener<I>) registry.get(type);
        if (opener == null) {
            AWLog.warn("Trying to open container for unknown container type {}", type);
            return false;
        }
        return opener.open(player, host);
    }

    @FunctionalInterface
    public interface Opener<I> {
        boolean open(PlayerEntity player, I host);
    }
}
