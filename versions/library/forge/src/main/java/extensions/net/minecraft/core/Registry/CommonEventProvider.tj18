package extensions.net.minecraft.core.Registry;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, 1.19)")
@Extension
public class CommonEventProvider {

    public static void willServerTickFO(@ThisClass Class<?> clazz, Consumer<ServerLevel> consumer) {
        NotificationCenterImpl.observer(TickEvent.WorldTickEvent.class, event -> {
            if (event.side == LogicalSide.SERVER) {
                consumer.accept((ServerLevel) event.world);
            }
        });
    }

    public static void willServerStartFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerAboutToStartEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    public static void didServerStartFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerStartedEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    public static void willServerStopFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerStoppingEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    public static void didServerStopFO(@ThisClass Class<?> clazz, Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(ServerStoppedEvent.class, consumer, ServerLifecycleEvent::getServer);
    }


    public static void willPlayerLoginFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.PlayerLoggedInEvent.class, consumer, PlayerEvent::getPlayer);
    }

    public static void willPlayerLogoutFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.PlayerLoggedOutEvent.class, consumer, PlayerEvent::getPlayer);
    }

    public static void willPlayerCloneFO(@ThisClass Class<?> clazz, BiConsumer<Player, Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.Clone.class, event -> consumer.accept(event.getOriginal(), event.getPlayer()));
    }

    public static void didEntityTackingFO(@ThisClass Class<?> clazz, BiConsumer<Entity, Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.StartTracking.class, event -> {
            consumer.accept(event.getTarget(), event.getPlayer());
        });
    }

    public static void shouldEntityAttackFO(@ThisClass Class<?> clazz, BiFunction<Entity, Player, InteractionResult> consumer) {
        NotificationCenterImpl.observer(AttackEntityEvent.class, event -> {
            if (consumer.apply(event.getTarget(), event.getPlayer()) == InteractionResult.FAIL) {
                event.setCanceled(true);
            }
        });
    }

    public static void willEntityDeathFO(@ThisClass Class<?> clazz, Consumer<Entity> consumer) {
        NotificationCenterImpl.observer(LivingDropsEvent.class, consumer, LivingEvent::getEntity);
    }

    public static void didEntityJoinFO(@ThisClass Class<?> clazz, Consumer<Entity> consumer) {
        NotificationCenterImpl.observer(EntityJoinWorldEvent.class, event -> {
            if (!event.getWorld().isClientSide()) {
                consumer.accept(event.getEntity());
            }
        });
    }

    public static void willBlockPlaceFO(@ThisClass Class<?> clazz, CommonNativeProvider.BlockSnapshot consumer) {
        NotificationCenterImpl.observer(BlockEvent.EntityPlaceEvent.class, event -> {
            Block block = event.getState().getBlock();
            if (event.getEntity() instanceof ServerPlayer && block instanceof SkinCubeBlock) {
                Player player = (Player) event.getEntity();
                LevelAccessor level = event.getWorld();
                BlockState oldState = event.getBlockSnapshot().getReplacedBlock();
                CompoundTag oldTag = event.getBlockSnapshot().getTag();
                Component reason = TranslateUtils.title("chat.armourers_workshop.undo.placeBlock");
                consumer.snapshot(level, event.getPos(), oldState, oldTag, player, reason);
            }
        });
    }

    public static void willBlockBreakFO(@ThisClass Class<?> clazz, CommonNativeProvider.BlockSnapshot consumer) {
        NotificationCenterImpl.observer(BlockEvent.BreakEvent.class, event -> {
            Block block = event.getState().getBlock();
            if (event.getPlayer() instanceof ServerPlayer && block instanceof SkinCubeBlock) {
                LevelAccessor level = event.getWorld();
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

    public static void willConfigReloadFO(@ThisClass Class<?> clazz, Consumer<ForgeConfigSpec> consumer) {
        NotificationCenterImpl.observer(ModConfigEvent.class, event -> {
            ForgeConfigSpec spec = ObjectUtils.safeCast(event.getConfig().getSpec(), ForgeConfigSpec.class);
            if (spec != null) {
                consumer.accept(spec);
            }
        });
    }

    public static void willRegisterEntityAttributesFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.EntityAttributesRegistry> consumer) {
        NotificationCenterImpl.observer(EntityAttributeCreationEvent.class, consumer, event -> (entity, builder) -> event.put(entity, builder.build()));
    }

    public static void willRegisterCustomDataPackFO(@ThisClass Class<?> clazz, Supplier<PreparableReloadListener> consumer) {
        NotificationCenterImpl.observer(AddReloadListenerEvent.class, event -> event.addListener(consumer.get()));
    }


    public static void willRegisterCommandFO(@ThisClass Class<?> clazz, Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        NotificationCenterImpl.observer(RegisterCommandsEvent.class, consumer, RegisterCommandsEvent::getDispatcher);
    }

    public static void willRegisterArgumentFO(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.ArgumentRegistry> consumer) {
        consumer.accept(AbstractArgumentTypeInfo::register);
    }
}
