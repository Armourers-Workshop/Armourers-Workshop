package moe.plushie.armourers_workshop.api.registry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public interface IBlockBuilder<T extends Block> extends IRegistryBuilder<T> {

    IBlockBuilder<T> noCollission();

    IBlockBuilder<T> noOcclusion();

    IBlockBuilder<T> friction(float f);

    IBlockBuilder<T> speedFactor(float f);

    IBlockBuilder<T> jumpFactor(float f);

    IBlockBuilder<T> sound(SoundType soundType);

    IBlockBuilder<T> lightLevel(ToIntFunction<BlockState> toIntFunction);

    IBlockBuilder<T> strength(float f, float g);

    IBlockBuilder<T> instabreak();

    IBlockBuilder<T> strength(float f);

    IBlockBuilder<T> randomTicks();

    IBlockBuilder<T> dynamicShape();

    IBlockBuilder<T> noDrops();

    IBlockBuilder<T> air();

    IBlockBuilder<T> isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> stateArgumentPredicate);

    IBlockBuilder<T> isRedstoneConductor(BlockBehaviour.StatePredicate statePredicate);

    IBlockBuilder<T> isSuffocating(BlockBehaviour.StatePredicate statePredicate);

    IBlockBuilder<T> isViewBlocking(BlockBehaviour.StatePredicate statePredicate);

    IBlockBuilder<T> hasPostProcess(BlockBehaviour.StatePredicate statePredicate);

    IBlockBuilder<T> emissiveRendering(BlockBehaviour.StatePredicate statePredicate);

    IBlockBuilder<T> requiresCorrectToolForDrops();

    IBlockBuilder<T> bind(Supplier<Supplier<RenderType>> provider);

    default IBlockBuilder<T> lightLevel(int level) {
        return lightLevel(state -> level);
    }
}
