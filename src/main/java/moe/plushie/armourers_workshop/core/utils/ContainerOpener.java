package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import java.util.HashMap;
import java.util.Map;

public final class ContainerOpener {

    private ContainerOpener() {
    }

    private static final Map<ContainerType<? extends Container>, Opener<?>> registry = new HashMap<>();

    public static <T extends Container, I> void addOpener(ContainerType<T> type, Opener<I> opener) {
        registry.put(type, opener);
    }

    public static <I> boolean openContainer(ContainerType<?> type, PlayerEntity player, I host) {
        Opener<I> opener = (Opener<I>) registry.get(type);
        if (opener == null) {
            SkinLog.warn("Trying to open container for unknown container type {}", type);
            return false;
        }
        return opener.open(player, host);
    }

    @FunctionalInterface
    public interface Opener<I> {
        boolean open(PlayerEntity player, I host);
    }
}
