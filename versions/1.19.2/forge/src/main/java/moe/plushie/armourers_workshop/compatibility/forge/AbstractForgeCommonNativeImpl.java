package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IRegistryProvider;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractCommonNativeProviderExt_V19;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AbstractForgeCommonNativeImpl implements AbstractForgeCommonNativeProvider, AbstractCommonNativeProviderExt_V19 {

    private static final IRegistryProvider<ArgumentTypeInfo<?, ?>> ARGUMENT_REGISTRY = AbstractForgeRegistries.wrap(ForgeRegistries.COMMAND_ARGUMENT_TYPES);

    @Override
    public void willRegisterArgumentInfo(Consumer<ArgumentInfoRegistry> consumer) {
        consumer.accept(new ArgumentInfoRegistry() {
            @Override
            public <T extends IArgumentType<?>> void register(ResourceLocation registryName, Class<T> argumentType, ArgumentTypeInfo<T, ?> argumentInfo) {
                ArgumentTypeInfo<?, ?> info1 = ArgumentTypeInfos.registerByClass(argumentType, argumentInfo);
                ARGUMENT_REGISTRY.register(registryName.getPath(), () -> info1);
            }
        });
    }

    @Override
    public void willServerTick(Consumer<ServerLevel> consumer) {
        NotificationCenterImpl.observer(TickEvent.LevelTickEvent.class, event -> {
            if (event.side == LogicalSide.SERVER) {
                consumer.accept((ServerLevel) event.level);
            }
        });
    }

    @Override
    public void willServerStart(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerAboutToStartEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void didServerStart(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerStartedEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void willServerStop(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerStoppingEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void didServerStop(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerStoppedEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void willPlayerLogin(Consumer<Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.PlayerLoggedInEvent.class, consumer, PlayerEvent::getEntity);
    }

    @Override
    public void willPlayerLogout(Consumer<Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.PlayerLoggedOutEvent.class, consumer, PlayerEvent::getEntity);
    }

    @Override
    public void willPlayerClone(BiConsumer<Player, Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.Clone.class, event -> consumer.accept(event.getOriginal(), event.getEntity()));
    }

    @Override
    public void didPlayerTacking(BiConsumer<Entity, Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.StartTracking.class, event -> {
            consumer.accept(event.getTarget(), event.getEntity());
        });
    }

    @Override
    public void willEntityDrop(Consumer<Entity> consumer) {
        NotificationCenterImpl.observer(LivingDropsEvent.class, consumer, LivingEvent::getEntity);
    }

    @Override
    public void didEntityJoin(Consumer<Entity> consumer) {
        NotificationCenterImpl.observer(EntityJoinLevelEvent.class, event -> {
            if (!event.getLevel().isClientSide()) {
                consumer.accept(event.getEntity());
            }
        });
    }

    @Override
    public void willBlockPlace(BlockSnapshot consumer) {
        NotificationCenterImpl.observer(BlockEvent.EntityPlaceEvent.class, event -> {
            Block block = event.getState().getBlock();
            if (event.getEntity() instanceof ServerPlayer && block instanceof SkinCubeBlock) {
                Player player = (Player) event.getEntity();
                LevelAccessor level = event.getLevel();
                BlockState oldBlockState = event.getBlockSnapshot().getReplacedBlock();
                CompoundTag oldTag = event.getBlockSnapshot().getTag();
                Component reason = TranslateUtils.title("chat.armourers_workshop.undo.placeBlock");
                consumer.snapshot(level, event.getPos(), oldBlockState, oldTag, player, reason);
            }
        });
    }

    @Override
    public void willBlockBreak(BlockSnapshot consumer) {
        NotificationCenterImpl.observer(BlockEvent.BreakEvent.class, event -> {
            Block block = event.getState().getBlock();
            if (event.getPlayer() instanceof ServerPlayer && block instanceof SkinCubeBlock) {
                LevelAccessor level = event.getLevel();
                BlockEntity blockEntity = level.getBlockEntity(event.getPos());
                CompoundTag oldNBT = null;
                if (blockEntity != null) {
                    oldNBT = blockEntity.saveWithFullMetadata();
                }
                Component reason = TranslateUtils.title("chat.armourers_workshop.undo.breakBlock");
                consumer.snapshot(level, event.getPos(), event.getState(), oldNBT, event.getPlayer(), reason);
            }
        });
    }

    @Override
    public void willConfigReload(Consumer<ForgeConfigSpec> consumer) {
        NotificationCenterImpl.observer(ModConfigEvent.class, event -> {
            ForgeConfigSpec spec = ObjectUtils.safeCast(event.getConfig().getSpec(), ForgeConfigSpec.class);
            if (spec != null) {
                consumer.accept(spec);
            }
        });
    }

    @Override
    public void shouldAttackEntity(BiFunction<Entity, Player, InteractionResult> consumer) {
        NotificationCenterImpl.observer(AttackEntityEvent.class, event -> {
            if (consumer.apply(event.getTarget(), event.getEntity()) == InteractionResult.FAIL) {
                event.setCanceled(true);
            }
        });
    }
}
