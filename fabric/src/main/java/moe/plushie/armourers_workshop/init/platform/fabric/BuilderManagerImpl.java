package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntitySupplier;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.common.builder.*;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Optional;
import java.util.function.Function;

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
    public <T extends Item> IItemTagBuilder<T> createItemTagBuilder() {
        return new ItemTagBuilderImpl<>();
    }

    @Override
    public <T extends CreativeModeTab> IItemGroupBuilder<T> createItemGroupBuilder() {
        return new ItemGroupBuilderImpl<>();
    }

    @Override
    public <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, Material material, MaterialColor materialColor) {
        return new BlockBuilderImpl<>(supplier, material, materialColor);
    }

    @Override
    public <T extends BlockEntity> IBlockEntityBuilder<T> createBlockEntityBuilder(IBlockEntitySupplier<T> supplier) {
        return new BlockEntityBuilderImpl<>(supplier);
    }

    @Override
    public <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
        return new EntityTypeBuilderImpl<>(entityFactory, mobCategory);
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
    public <T extends IPermissionNode> IPermissionNodeBuilder<T> createPermissionBuilder() {
        return new PermissionNodeBuilderImpl<>();
    }
}
