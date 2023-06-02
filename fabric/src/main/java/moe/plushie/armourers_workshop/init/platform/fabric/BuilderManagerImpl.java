package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.ICapabilityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemGroupBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemTagBuilder;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionBuilder;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ArgumentTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.BlockBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.BlockEntityTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.CapabilityTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.EntitySerializerBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.EntityTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ItemBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ItemGroupBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ItemTagBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.KeyBindingBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.LootFunctionBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.MenuTypeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.PermissionNodeBuilderImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.SoundEventBuilderImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class BuilderManagerImpl implements BuilderManager.Impl {

    private static final BuilderManagerImpl INSTANCE = new BuilderManagerImpl();

    public static BuilderManager.Impl getInstance() {
        return INSTANCE;
    }

    @Override
    public <T extends Item> IItemBuilder<T> createItemBuilder(Function<Item.Properties, T> supplier) {
        return new ItemBuilderImpl<>(supplier);
    }

    @Override
    public <T extends IItemTag> IItemTagBuilder<T> createItemTagBuilder() {
        return new ItemTagBuilderImpl<>();
    }

    @Override
    public <T extends IItemGroup> IItemGroupBuilder<T> createItemGroupBuilder() {
        return new ItemGroupBuilderImpl<>();
    }

    @Override
    public <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
        return new BlockBuilderImpl<>(supplier, material, materialColor);
    }

    @Override
    public <T extends BlockEntity> IBlockEntityTypeBuilder<T> createBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer) {
        return new BlockEntityTypeBuilderImpl<>(serializer);
    }

    @Override
    public <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory mobCategory) {
        return new EntityTypeBuilderImpl<>(serializer, mobCategory);
    }

    @Override
    public <T> IEntitySerializerBuilder<T> createEntitySerializerBuilder(IEntitySerializer<T> serializer) {
        return new EntitySerializerBuilderImpl<>(serializer);
    }

    @Override
    public <T extends AbstractContainerMenu, D> IMenuTypeBuilder<T> createMenuTypeBuilder(IMenuProvider<T, D> factory, IPlayerDataSerializer<D> serializer) {
        return new MenuTypeBuilderImpl<>(factory, serializer);
    }

    @Override
    public <T extends IArgumentType<?>> IArgumentTypeBuilder<T> createArgumentTypeBuilder(Class<T> argumentType) {
        return new ArgumentTypeBuilderImpl<>(argumentType);
    }

    @Override
    public <T> ICapabilityTypeBuilder<T> createCapabilityTypeBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        return new CapabilityTypeBuilderImpl<>(type, factory);
    }

    @Override
    public <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key) {
        return new KeyBindingBuilderImpl<>(key);
    }

    @Override
    public <T extends ILootFunction> ILootFunctionBuilder<T> createLootFunctionBuilder(Supplier<ILootFunction.Serializer<T>> serializer) {
        return new LootFunctionBuilderImpl<>(serializer);
    }

    @Override
    public <T extends IPermissionNode> IPermissionNodeBuilder<T> createPermissionBuilder() {
        return new PermissionNodeBuilderImpl<>();
    }

    @Override
    public <T extends SoundEvent> ISoundEventBuilder<T> createSoundEventBuilder() {
        return new SoundEventBuilderImpl<>();
    }
}
