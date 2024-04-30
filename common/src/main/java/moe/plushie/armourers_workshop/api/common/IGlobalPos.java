package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface IGlobalPos {

    IGlobalPos NULL = new IGlobalPos() {
        public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> transformer) {
            return Optional.empty();
        }
    };

    static IGlobalPos create(final Level level, final BlockPos blockPos) {
        return new IGlobalPos() {
            @Override
            public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> transformer) {
                return Optional.of(transformer.apply(level, blockPos));
            }
        };
    }

    <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> transformer);

    default <T> T evaluate(BiFunction<Level, BlockPos, T> transformer, T object) {
        return evaluate(transformer).orElse(object);
    }

    default void execute(BiConsumer<Level, BlockPos> consumer) {
        evaluate((level, blockPos) -> {
            consumer.accept(level, blockPos);
            return Optional.empty();
        });
    }
}
