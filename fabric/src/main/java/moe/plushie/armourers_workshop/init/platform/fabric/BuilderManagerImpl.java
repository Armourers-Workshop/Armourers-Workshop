package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.ICapabilityType;
import moe.plushie.armourers_workshop.api.common.*;
import moe.plushie.armourers_workshop.api.registry.*;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.capability.CapabilityStorage;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

@SuppressWarnings("unused")
public class BuilderManagerImpl implements BuilderManager.Impl {

    private static final BuilderManagerImpl INSTANCE = new BuilderManagerImpl();

    public static BuilderManager.Impl getInstance() {
        return INSTANCE;
    }

    @Override
    public <T extends Item> IItemBuilder<T> createItemBuilder(Function<Item.Properties, T> supplier) {
        return new IItemBuilder<T>() {
            Item.Properties properties = new Item.Properties();
            Supplier<Consumer<T>> binder;

            @Override
            public IItemBuilder<T> stacksTo(int i) {
                this.properties = properties.stacksTo(i);
                return this;
            }

            @Override
            public IItemBuilder<T> defaultDurability(int i) {
                this.properties = properties.defaultDurability(i);
                return this;
            }

            @Override
            public IItemBuilder<T> durability(int i) {
                this.properties = properties.durability(i);
                return this;
            }

            @Override
            public IItemBuilder<T> craftRemainder(Item item) {
                this.properties = properties.craftRemainder(item);
                return this;
            }

            @Override
            public IItemBuilder<T> tab(CreativeModeTab creativeModeTab) {
                this.properties = properties.tab(creativeModeTab);
                return this;
            }

            @Override
            public IItemBuilder<T> rarity(Rarity rarity) {
                this.properties = properties.rarity(rarity);
                return this;
            }

            @Override
            public IItemBuilder<T> fireResistant() {
                this.properties = properties.fireResistant();
                return this;
            }

            @Override
            public IItemBuilder<T> bind(Supplier<IItemStackRendererProvider> provider) {
                this.binder = () -> item -> {
                    // here is safe call client registry.
                    BuiltinItemRendererRegistry.INSTANCE.register(item, provider.get().getItemModelRenderer()::renderByItem);
                };
                return this;
            }

            @Override
            public IRegistryObject<T> build(String name) {
                IRegistryObject<T> object = Registry.ITEM.register(name, () -> supplier.apply(properties));
                EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
                return object;
            }
        };
    }

    @Override
    public <T extends Item> IItemTagBuilder<T> createItemTagBuilder() {
        return name -> ObjectUtils.unsafeCast(TagRegistry.item(ArmourersWorkshop.getResource(name)));
    }

    @Override
    public <T extends CreativeModeTab> IItemGroupBuilder<T> createItemGroupBuilder() {
        return new IItemGroupBuilder<T>() {
            Supplier<Supplier<ItemStack>> icon = () -> () -> ItemStack.EMPTY;
            BiConsumer<List<ItemStack>, CreativeModeTab> appendItems;

            @Override
            public IItemGroupBuilder<T> icon(Supplier<Supplier<ItemStack>> icon) {
                this.icon = icon;
                return this;
            }

            @Override
            public IItemGroupBuilder<T> appendItems(BiConsumer<List<ItemStack>, CreativeModeTab> appendItems) {
                this.appendItems = appendItems;
                return this;
            }

            @Override
            public T build(String name) {
                return ObjectUtils.unsafeCast(creativeModeTab(name));
            }

            private CreativeModeTab creativeModeTab(String name) {
                return FabricItemGroupBuilder.create(ArmourersWorkshop.getResource(name))
                        .icon(() -> icon.get().get())
                        .appendItems(appendItems)
                        .build();
            }
        };
    }

    @Override
    public <T extends Block> IBlockBuilder<T> createBlockBuilder(Function<BlockBehaviour.Properties, T> supplier, Material material, MaterialColor materialColor) {
        return new IBlockBuilder<T>() {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(material, materialColor);
            Supplier<Consumer<T>> binder;
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
                    BlockRenderLayerMap.INSTANCE.putBlock(block, provider.get().get());
                };
                return this;
            }

            @Override
            public IRegistryObject<T> build(String name) {
                IRegistryObject<T> object = Registry.BLOCK.register(name, () -> supplier.apply(properties));
                EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
                return object;
            }
        };
    }

    @Override
    public <T extends BlockEntity> IBlockEntityBuilder<T> createBlockEntityBuilder(Supplier<T> supplier) {
        return new IBlockEntityBuilder<T>() {
            Supplier<Consumer<BlockEntityType<T>>> binder;
            final LinkedList<Supplier<Block>> blocks = new LinkedList<>();

            @Override
            public IBlockEntityBuilder<T> of(Supplier<Block> block) {
                this.blocks.add(block);
                return this;
            }

            @Override
            public IBlockEntityBuilder<T> bind(Supplier<IBlockEntityRendererProvider<T>> provider) {
                this.binder = () -> blockEntityType -> {
                    // here is safe call client registry.
                    BlockEntityRendererRegistry.INSTANCE.register(blockEntityType, provider.get()::getBlockEntityRenderer);
                };
                return this;
            }

            @Override
            public IRegistryObject<BlockEntityType<T>> build(String name) {
                IRegistryObject<BlockEntityType<T>> object = Registry.BLOCK_ENTITY_TYPE.register(name, () -> {
                    Block[] blocks1 = blocks.stream().map(Supplier::get).toArray(Block[]::new);
                    return BlockEntityType.Builder.of(supplier, blocks1).build(null);
                });
                EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
                return object;
            }
        };
    }

    @Override
    public <T extends Entity> IEntityTypeBuilder<T> createEntityTypeBuilder(EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
        return new IEntityTypeBuilder<T>() {
            FabricEntityTypeBuilder<T> builder = FabricEntityTypeBuilder.create(mobCategory, entityFactory);
            Supplier<Consumer<EntityType<T>>> binder;
            @Override
            public IEntityTypeBuilder<T> fixed(float f, float g) {
                this.builder = builder.dimensions(EntityDimensions.fixed(f, g));
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> noSummon() {
                this.builder = builder.disableSummon();
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> noSave() {
                this.builder = builder.disableSaving();
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> fireImmune() {
                this.builder = builder.fireImmune();
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks) {
                this.builder = builder.specificSpawnBlocks(blocks);
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> spawnableFarFromPlayer() {
                this.builder = builder.spawnableFarFromPlayer();
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> clientTrackingRange(int i) {
                this.builder = builder.trackRangeBlocks(i);
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> updateInterval(int i) {
                this.builder = builder.trackedUpdateRate(i);
                return this;
            }

            @Override
            public IEntityTypeBuilder<T> bind(Supplier<IEntityRendererProvider<T>> provider) {
                this.binder = () -> entityType -> {
                    // here is safe call client registry.
                    EntityRendererRegistry.INSTANCE.register(entityType, (manager, context) -> provider.get().getEntityRenderer(manager));
                };
                return this;
            }

            @Override
            public IRegistryObject<EntityType<T>> build(String name) {
                IRegistryObject<EntityType<T>> object = Registry.ENTITY_TYPE.register(name, () -> builder.build());
                EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
                return object;
            }
        };
    }

    @Override
    public <T extends AbstractContainerMenu, D> IMenuTypeBuilder<T> createMenuTypeBuilder(IMenuExtendFactory<T, D> factory, IPlayerDataSerializer<D> serializer) {
        return new IMenuTypeBuilder<T>() {
            Supplier<Consumer<MenuType<T>>> binder;

            @Override
            public <U extends Screen & MenuAccess<T>> IMenuTypeBuilder<T> bind(Supplier<IMenuScreenProvider<T, U>> provider) {
                this.binder = () -> menuType -> {
                    // here is safe call client registry.
                    ScreenRegistry.register(menuType, provider.get()::getMenuScreen);
                };
                return this;
            }

            @Override
            public IRegistryObject<MenuType<T>> build(String name) {
                ResourceLocation registryName = ArmourersWorkshop.getResource(name);
                MenuType<T> menuType = new ExtendedScreenHandlerType<>((id, inv, buf) -> factory.createMenu(id, inv, serializer.read(buf, inv.player)));
                IRegistryObject<MenuType<T>> object = Registry.MENU_TYPE.register(name, () -> menuType);
                MenuManager.registerMenuOpener(menuType, serializer, (player, title, value) -> {
                    SimpleMenuProvider menuProvider = new SimpleMenuProvider((window, inv, player2) -> factory.createMenu(window, inv, value), title);
                    player.openMenu(new ExtendedScreenHandlerFactory() {

                        @Override
                        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                            return menuProvider.createMenu(i, inventory, player);
                        }

                        @Override
                        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                            serializer.write(buf, player, value);
                        }

                        @Override
                        public Component getDisplayName() {
                            return menuProvider.getDisplayName();
                        }
                    });
                    return true;
                });
                EnvironmentExecutor.setupOn(EnvironmentType.CLIENT, binder, object);
                return object;
            }
        };
    }

    @Override
    public <T> IEntryBuilder<IRegistryObject<ICapabilityType<T>>> createCapabilityTypeBuilder(Class<T> type, Function<Entity, Optional<T>> factory) {
        return name -> {
            ResourceLocation registryName = ArmourersWorkshop.getResource(name);
            ICapabilityType<T> capabilityType = new ICapabilityType<T>() {
                @Override
                public Optional<T> get(Entity entity) {
                    return CapabilityStorage.getCapability(entity, this);
                }
            };
            CapabilityStorage.registerCapability(registryName, capabilityType, factory);
            return new IRegistryObject<ICapabilityType<T>>() {
                @Override
                public ResourceLocation getRegistryName() {
                    return registryName;
                }

                @Override
                public ICapabilityType<T> get() {
                    return capabilityType;
                }
            };
        };
    }
}
