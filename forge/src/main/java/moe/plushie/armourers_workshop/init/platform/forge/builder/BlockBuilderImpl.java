package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.compat.builder.AbstractBlockBuilder;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@SuppressWarnings("Convert2MethodRef")
public class BlockBuilderImpl<T extends Block> implements IBlockBuilder<T> {

    private final AbstractBlockBuilder<T> builder;

    public BlockBuilderImpl(Function<BlockBehaviour.Properties, T> factory, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
        this.builder = new AbstractBlockBuilder<>(factory, material, materialColor);
    }

    @Override
    public IBlockBuilder<T> noCollission() {
        this.builder.apply(it -> it.noCollission());
        return this;
    }

    @Override
    public IBlockBuilder<T> noOcclusion() {
        this.builder.apply(it -> it.noOcclusion());
        return this;
    }

    @Override
    public IBlockBuilder<T> friction(float f) {
        this.builder.apply(it -> it.friction(f));
        return this;
    }

    @Override
    public IBlockBuilder<T> speedFactor(float f) {
        this.builder.apply(it -> it.speedFactor(f));
        return this;
    }

    @Override
    public IBlockBuilder<T> jumpFactor(float f) {
        this.builder.apply(it -> it.jumpFactor(f));
        return this;
    }

    @Override
    public IBlockBuilder<T> sound(SoundType soundType) {
        this.builder.apply(it -> it.sound(soundType));
        return this;
    }

    @Override
    public IBlockBuilder<T> lightLevel(ToIntFunction<BlockState> provider) {
        this.builder.apply(it -> it.lightLevel(provider));
        return this;
    }

    @Override
    public IBlockBuilder<T> strength(float f, float g) {
        this.builder.apply(it -> it.strength(f, g));
        return this;
    }

    @Override
    public IBlockBuilder<T> randomTicks() {
        this.builder.apply(it -> it.randomTicks());
        return this;
    }

    @Override
    public IBlockBuilder<T> dynamicShape() {
        this.builder.apply(it -> it.dynamicShape());
        return this;
    }

    @Override
    public IBlockBuilder<T> noLootTable() {
        this.builder.apply(it -> it.noLootTable());
        return this;
    }

    @Override
    public IBlockBuilder<T> air() {
        this.builder.apply(it -> it.air());
        return this;
    }

    @Override
    public IBlockBuilder<T> forceSolid() {
        this.builder.apply(it -> it.forceSolidOn());
        return this;
    }

    @Override
    public IBlockBuilder<T> isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> state) {
        this.builder.apply(it -> it.isValidSpawn(state));
        return this;
    }

    @Override
    public IBlockBuilder<T> isRedstoneConductor(BlockBehaviour.StatePredicate state) {
        this.builder.apply(it -> it.isRedstoneConductor(state));
        return this;
    }

    @Override
    public IBlockBuilder<T> isSuffocating(BlockBehaviour.StatePredicate state) {
        this.builder.apply(it -> it.isSuffocating(state));
        return this;
    }

    @Override
    public IBlockBuilder<T> isViewBlocking(BlockBehaviour.StatePredicate state) {
        this.builder.apply(it -> it.isViewBlocking(state));
        return this;
    }

    @Override
    public IBlockBuilder<T> emissiveRendering(BlockBehaviour.StatePredicate state) {
        this.builder.apply(it -> it.emissiveRendering(state));
        return this;
    }

    @Override
    public IBlockBuilder<T> requiresCorrectToolForDrops() {
        this.builder.apply(it -> it.requiresCorrectToolForDrops());
        return this;
    }

    @Override
    public IRegistryHolder<T> build(String name) {
        return Registries.BLOCKS.register(name, builder::build);
    }
}
