package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IBlockBuilder;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class BlockBuilderImpl<T extends Block> implements IBlockBuilder<T> {

    private BlockBehaviour.Properties properties;
    private Supplier<Consumer<T>> binder;
    private final Function<BlockBehaviour.Properties, T> supplier;

    public BlockBuilderImpl(Function<BlockBehaviour.Properties, T> supplier, Material material, MaterialColor materialColor) {
        this.properties = BlockBehaviour.Properties.of(material, materialColor);
        this.supplier = supplier;
    }

    @Override
    public IBlockBuilder<T> noCollission() {
        this.properties = properties.noCollission();
        return this;
    }

    @Override
    public IBlockBuilder<T> noOcclusion() {
        this.properties = properties.noOcclusion();
        return this;
    }

    @Override
    public IBlockBuilder<T> friction(float f) {
        this.properties = properties.friction(f);
        return this;
    }

    @Override
    public IBlockBuilder<T> speedFactor(float f) {
        this.properties = properties.speedFactor(f);
        return this;
    }

    @Override
    public IBlockBuilder<T> jumpFactor(float f) {
        this.properties = properties.jumpFactor(f);
        return this;
    }

    @Override
    public IBlockBuilder<T> sound(SoundType soundType) {
        this.properties = properties.sound(soundType);
        return this;
    }

    @Override
    public IBlockBuilder<T> lightLevel(ToIntFunction<BlockState> toIntFunction) {
        this.properties = properties.lightLevel(toIntFunction);
        return this;
    }

    @Override
    public IBlockBuilder<T> strength(float f, float g) {
        this.properties = properties.strength(f, g);
        return this;
    }

    @Override
    public IBlockBuilder<T> instabreak() {
        this.properties = properties.instabreak();
        return this;
    }

    @Override
    public IBlockBuilder<T> strength(float f) {
        this.properties = properties.strength(f);
        return this;
    }

    @Override
    public IBlockBuilder<T> randomTicks() {
        this.properties = properties.randomTicks();
        return this;
    }

    @Override
    public IBlockBuilder<T> dynamicShape() {
        this.properties = properties.dynamicShape();
        return this;
    }

    @Override
    public IBlockBuilder<T> noDrops() {
        this.properties = properties.noDrops();
        return this;
    }

    @Override
    public IBlockBuilder<T> air() {
        this.properties = properties.air();
        return this;
    }

    @Override
    public IBlockBuilder<T> isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> stateArgumentPredicate) {
        this.properties = properties.isValidSpawn(stateArgumentPredicate);
        return this;
    }

    @Override
    public IBlockBuilder<T> isRedstoneConductor(BlockBehaviour.StatePredicate statePredicate) {
        this.properties = properties.isRedstoneConductor(statePredicate);
        return this;
    }

    @Override
    public IBlockBuilder<T> isSuffocating(BlockBehaviour.StatePredicate statePredicate) {
        this.properties = properties.isSuffocating(statePredicate);
        return this;
    }

    @Override
    public IBlockBuilder<T> isViewBlocking(BlockBehaviour.StatePredicate statePredicate) {
        this.properties = properties.isViewBlocking(statePredicate);
        return this;
    }

    @Override
    public IBlockBuilder<T> hasPostProcess(BlockBehaviour.StatePredicate statePredicate) {
        this.properties = properties.hasPostProcess(statePredicate);
        return this;
    }

    @Override
    public IBlockBuilder<T> emissiveRendering(BlockBehaviour.StatePredicate statePredicate) {
        this.properties = properties.emissiveRendering(statePredicate);
        return this;
    }

    @Override
    public IBlockBuilder<T> requiresCorrectToolForDrops() {
        this.properties = properties.requiresCorrectToolForDrops();
        return this;
    }

    @Override
    public IBlockBuilder<T> bind(Supplier<Supplier<RenderType>> provider) {
        this.binder = () -> block -> {
            // here is safe call client registry.
            ItemBlockRenderTypes.setRenderLayer(block, provider.get().get());
        };
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        IRegistryKey<T> object = Registry.BLOCK.register(name, () -> supplier.apply(properties));
        EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
        return object;
    }
}
