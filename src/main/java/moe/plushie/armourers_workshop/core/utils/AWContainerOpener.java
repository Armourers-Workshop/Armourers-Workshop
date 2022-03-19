package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class AWContainerOpener {

    private AWContainerOpener() {
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

    public static void forEach(Consumer<ContainerType<?>> consumer) {
        registry.keySet().forEach(consumer);
    }

    @FunctionalInterface
    public interface Opener<I> {
        boolean open(PlayerEntity player, I host);
    }
}
