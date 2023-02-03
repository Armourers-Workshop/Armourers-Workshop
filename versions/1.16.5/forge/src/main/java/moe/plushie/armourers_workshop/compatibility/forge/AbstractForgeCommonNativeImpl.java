package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractCommonNativeExt_V1618;
import moe.plushie.armourers_workshop.compatibility.forge.ext.AbstractForgeCommonExt_V1618;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AbstractForgeCommonNativeImpl implements AbstractForgeCommonNativeProvider, AbstractCommonNativeExt_V1618, AbstractForgeCommonExt_V1618 {

    @Override
    public void willServerTick(Consumer<ServerLevel> consumer) {
        NotificationCenterImpl.observer(TickEvent.WorldTickEvent.class, event -> {
            if (event.side == LogicalSide.SERVER) {
                consumer.accept((ServerLevel) event.world);
            }
        });
    }

    @Override
    public void willServerStart(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(FMLServerAboutToStartEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void didServerStart(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(FMLServerStartedEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void willServerStop(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(FMLServerStoppingEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void didServerStop(Consumer<MinecraftServer> consumer) {
        NotificationCenterImpl.observer(FMLServerStoppedEvent.class, consumer, ServerLifecycleEvent::getServer);
    }

    @Override
    public void willPlayerLogin(Consumer<Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.PlayerLoggedInEvent.class, consumer, PlayerEvent::getPlayer);
    }

    @Override
    public void willPlayerLogout(Consumer<Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.PlayerLoggedOutEvent.class, consumer, PlayerEvent::getPlayer);
    }

    @Override
    public void willPlayerClone(BiConsumer<Player, Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.Clone.class, event -> consumer.accept(event.getOriginal(), event.getPlayer()));
    }

    @Override
    public void didPlayerTacking(BiConsumer<Entity, Player> consumer) {
        NotificationCenterImpl.observer(PlayerEvent.StartTracking.class, event -> {
            consumer.accept(event.getTarget(), event.getPlayer());
        });
    }

    @Override
    public void didEntityJoin(Consumer<Entity> consumer) {
        NotificationCenterImpl.observer(EntityJoinWorldEvent.class, event -> {
            if (!event.getWorld().isClientSide()) {
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
                LevelAccessor level = event.getWorld();
                BlockState oldState = event.getBlockSnapshot().getReplacedBlock();
                CompoundTag oldTag = event.getBlockSnapshot().getNbt();
                Component reason = TranslateUtils.title("chat.armourers_workshop.undo.placeBlock");
                consumer.snapshot(level, event.getPos(), oldState, oldTag, player, reason);
            }
        });
    }

    @Override
    public void willBlockBreak(BlockSnapshot consumer) {
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

    @Override
    public void willEntityDrop(Consumer<Entity> consumer) {
        NotificationCenterImpl.observer(LivingDropsEvent.class, consumer, LivingEvent::getEntityLiving);
    }

    @Override
    public void willConfigReload(Consumer<ForgeConfigSpec> consumer) {
        NotificationCenterImpl.observer(ModConfig.ModConfigEvent.class, event -> {
            ForgeConfigSpec spec = ObjectUtils.safeCast(event.getConfig().getSpec(), ForgeConfigSpec.class);
            if (spec != null) {
                consumer.accept(spec);
            }
        });
    }

    @Override
    public void shouldAttackEntity(BiFunction<Entity, Player, InteractionResult> consumer) {
        NotificationCenterImpl.observer(AttackEntityEvent.class, event -> {
            if (consumer.apply(event.getTarget(), event.getPlayer()) == InteractionResult.FAIL) {
                event.setCanceled(true);
            }
        });
    }
}
