package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;

public interface IContainerLevelAccess extends ContainerLevelAccess {

    static IContainerLevelAccess create(final Level level, final BlockPos blockPos, @Nullable CompoundTag extraData) {
        return new IContainerLevelAccess() {
            @Override
            public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> biFunction) {
                return Optional.of(biFunction.apply(level, blockPos));
            }
        };
    }
}
