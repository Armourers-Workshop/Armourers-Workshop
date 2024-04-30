package moe.plushie.armourers_workshop.init.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.ICapabilityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.api.registry.IEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemGroupBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemTagBuilder;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.api.registry.ILootFunctionTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
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

public class BuilderManager {

    @ExpectPlatform
    public static Impl getInstance() {
        throw new AssertionError();
    }

    public interface Impl {

        <T extends Item> IItemBuilder<T> createItemBuilder(Function<Item.Properties, T> supplier);

        <T extends IItemTag> IItemTagBuilder<T> createItemTagBuilder();

        <T extends IItemGroup> IItemGroupBuilder<T> createItemGroupBuilder();

        <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor);

        <T extends BlockEntity> IBlockEntityTypeBuilder<T> createBlockEntityTypeBuilder(IBlockEntityType.Serializer<T> serializer);

        <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory mobCategory);

        <T> IEntitySerializerBuilder<T> createEntitySerializerBuilder(IEntitySerializer<T> serializer);

        <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> createMenuTypeBuilder(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer);

        <T extends IArgumentType<?>> IArgumentTypeBuilder<T> createArgumentTypeBuilder(Class<T> argumentType);

        <T> ICapabilityTypeBuilder<T> createCapabilityTypeBuilder(Class<T> type, Function<Entity, Optional<T>> factory);

        <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key);

        <T extends ILootFunction> ILootFunctionTypeBuilder<T> createLootFunctionTypeBuilder(MapCodec<T> codec);

        <T extends IPermissionNode> IPermissionNodeBuilder<T> createPermissionBuilder();

        <T> IDataComponentTypeBuilder<T> createDataComponentTypeBuilder(Codec<T> codec);

        <T extends SoundEvent> ISoundEventBuilder<T> createSoundEventBuilder();
    }
}
