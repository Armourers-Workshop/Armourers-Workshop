package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.ICreativeModeTabBuilder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntityCapabilityBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.api.registry.ITagKeyBuilder;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterialColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;
import java.util.function.Function;

public abstract class BuilderManager {

    private static final BuilderManager INSTANCE = PlatformLoader.load(BuilderManager.class);

    public static BuilderManager getInstance() {
        return INSTANCE;
    }

    public abstract <T extends Item> IItemBuilder<T> createItemBuilder(Function<Item.Properties, T> supplier);

    public abstract <T extends Item> ITagKeyBuilder<T> createItemTagBuilder();

    public abstract <T extends CreativeModeTab> ICreativeModeTabBuilder<T> createItemGroupBuilder();

    public abstract <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor);

    public abstract <T extends BlockEntity> IBlockEntityTypeBuilder<T> createBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer);

    public abstract <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory mobCategory);

    public abstract <T> IEntitySerializerBuilder<T> createEntitySerializerBuilder(IEntityDataSerializer<T> serializer);

    public abstract <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> createMenuTypeBuilder(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer);

    public abstract <T extends IArgumentType<?>> IArgumentTypeBuilder<T> createArgumentTypeBuilder(IArgumentSerializer<T> serializer);

    public abstract <T> IEntityCapabilityBuilder<T> createEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory);

    public abstract <T> IBlockEntityCapabilityBuilder<T> createBlockEntityCapabilityBuilder(Class<T> type, Function<Entity, Optional<T>> factory);

    public abstract <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key);

    public abstract <T extends ILootItemFunction> ILootFunctionTypeBuilder<T> createLootFunctionTypeBuilder(IDataMapCodec<T> codec);

    public abstract <T extends IPermissionNode> IPermissionNodeBuilder<T> createPermissionBuilder();

    public abstract <T> IDataComponentTypeBuilder<T> createDataComponentTypeBuilder(IDataCodec<T> codec);

    public abstract <T extends SoundEvent> ISoundEventBuilder<T> createSoundEventBuilder();
}
