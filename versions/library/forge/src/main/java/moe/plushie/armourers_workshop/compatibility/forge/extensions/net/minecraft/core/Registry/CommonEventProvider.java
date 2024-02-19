package moe.plushie.armourers_workshop.compatibility.forge.extensions.net.minecraft.core.Registry;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEvents;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeConfig;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeEventBus;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class CommonEventProvider {

    public static void willServerTickFO(@ThisClass Class<?> clazz, Consumer<ServerLevel> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.TICK, event -> {
            if (event.side.isServer()) {
                consumer.accept((ServerLevel) event.level);
            }
        });
    }

    public static void willLoadCompleteFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.Dispatcher> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.FML_LOAD_COMPLETE, event -> consumer.accept(event::enqueueWork));
    }

    public static void willClientSetupFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.Dispatcher> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.FML_CLIENT_SETUP, event -> consumer.accept(event::enqueueWork));
    }

    public static void willCommonSetupFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.Dispatcher> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.FML_COMMON_SETUP, event -> consumer.accept(event::enqueueWork));
    }

    public static void willServerStartFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.SERVER_WILL_START, event -> consumer.accept(event.getServer()));
    }

    public static void didServerStartFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.SERVER_DID_START, event -> consumer.accept(event.getServer()));
    }

    public static void willServerStopFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.SERVER_WILL_STOP, event -> consumer.accept(event.getServer()));
    }

    public static void didServerStopFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.SERVER_DID_STOP, event -> consumer.accept(event.getServer()));
    }


    public static void willPlayerLoginFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.PLAYER_LOGIN, event -> consumer.accept(event.getEntity()));
    }

    public static void willPlayerLogoutFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.PLAYER_LOGOUT, event -> consumer.accept(event.getEntity()));
    }

    public static void willPlayerCloneFO(@ThisClass Class<?> clazz, BiConsumer<Player, Player> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.PLAYER_CLONE, event -> consumer.accept(event.getOriginal(), event.getEntity()));
    }

    public static void didEntityTackingFO(@ThisClass Class<?> clazz, BiConsumer<Entity, Player> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.PLAYER_TRACKING, event -> {
            consumer.accept(event.getTarget(), event.getEntity());
        });
    }

    public static void shouldEntityAttackFO(@ThisClass Class<?> clazz, BiFunction<Entity, Player, InteractionResult> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.ENTITY_ATTACK, event -> {
            if (consumer.apply(event.getTarget(), event.getEntity()) == InteractionResult.FAIL) {
                event.setCanceled(true);
            }
        });
    }

    public static void willEntityDeathFO(@ThisClass Class<?> clazz, Consumer<Entity> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.ENTITY_DROPS, event -> consumer.accept(event.getEntity()));
    }

    public static void didEntityJoinFO(@ThisClass Class<?> clazz, Consumer<Entity> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.ENTITY_JOIN, event -> {
            if (!event.getLevel().isClientSide()) {
                consumer.accept(event.getEntity());
            }
        });
    }

    public static void willBlockPlaceFO(@ThisClass Class<?> clazz, CommonNativeProvider.BlockSnapshot consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.BLOCK_PLACE, event -> {
            if (event.getEntity() instanceof Player) {
                return;
            }
            Player player = (Player) event.getEntity();
            LevelAccessor level = event.getLevel();
            consumer.snapshot(player, level, event.getPos(), event.getState(), new IBlockSnapshot() {
                @Override
                public BlockState getState() {
                    return event.getBlockSnapshot().getReplacedBlock();
                }

                @Override
                public CompoundTag getTag() {
                    return event.getBlockSnapshot().getTag();
                }
            });
        });
    }

    public static void willBlockBreakFO(@ThisClass Class<?> clazz, CommonNativeProvider.BlockSnapshot consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.BLOCK_BREAK, event -> {
            Player player = event.getPlayer();
            LevelAccessor level = event.getLevel();
            consumer.snapshot(player, level, event.getPos(), null, new IBlockSnapshot() {
                @Override
                public BlockState getState() {
                    return event.getState();
                }

                @Override
                public CompoundTag getTag() {
                    BlockEntity blockEntity = level.getBlockEntity(event.getPos());
                    if (blockEntity != null) {
                        return blockEntity.saveWithFullMetadata();
                    }
                    return null;
                }
            });
        });
    }

    public static void willConfigReloadFO(@ThisClass Class<?> clazz, Consumer<AbstractForgeConfig.Spec> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.CONFIG, event -> {
            consumer.accept(new AbstractForgeConfig.Spec(event.getConfig().getSpec()));
        });
    }

    public static void willRegisterEntityAttributesFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.EntityAttributesRegistry> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.ENTITY_ATTRIBUTE_REGISTRY, consumer, event -> (entity, builder) -> event.put(entity, builder.build()));
    }

    public static void willRegisterCustomDataPackFO(@ThisClass Class<?> clazz, Supplier<PreparableReloadListener> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.DATA_PACK_REGISTRY, event -> event.addListener(consumer.get()));
    }


    public static void willRegisterCommandFO(@ThisClass Class<?> clazz, Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeCommonEvents.COMMAND_REGISTRY, event -> consumer.accept(event.getDispatcher()));
    }

    public static void willRegisterArgumentFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.ArgumentRegistry> consumer) {
        consumer.accept(new CommonNativeProvider.ArgumentRegistry() {
            @Override
            public <T extends IArgumentType<?>> void register(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
                ArgumentTypeInfo<?, ?> info1 = ArgumentTypeInfos.registerByClass(argumentType, new AbstractArgumentTypeInfo<>(argumentSerializer));
                AbstractForgeRegistries.COMMAND_ARGUMENT_TYPES.register(registryName.getPath(), () -> info1);
            }
        });
    }
}
