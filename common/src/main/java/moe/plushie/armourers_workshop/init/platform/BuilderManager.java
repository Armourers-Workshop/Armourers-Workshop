package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import moe.plushie.armourers_workshop.api.other.builder.*;
import moe.plushie.armourers_workshop.api.other.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.other.menu.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.other.key.IKeyBinding;
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
import java.util.function.Supplier;

public class BuilderManager {

    @ExpectPlatform
    public static Impl getInstance() {
        throw new AssertionError();
    }

    public interface Impl {

        <T extends Item> IItemBuilder<T> createItemBuilder(Function<Item.Properties, T> supplier);

        <T extends Item> IItemTagBuilder<T> createItemTagBuilder();

        <T extends CreativeModeTab> IItemGroupBuilder<T> createItemGroupBuilder();

        <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, Material material, MaterialColor materialColor);

        <T extends BlockEntity> IBlockEntityBuilder<T> createBlockEntityBuilder(Supplier<T> supplier);

        <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory);

        <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> createMenuTypeBuilder(IMenuProvider<T, V> factory, IPlayerDataSerializer<V> serializer);

        <T> ICapabilityTypeBuilder<T> createCapabilityTypeBuilder(Class<T> type, Function<Entity, Optional<T>> factory);

        <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key);
    }
}
