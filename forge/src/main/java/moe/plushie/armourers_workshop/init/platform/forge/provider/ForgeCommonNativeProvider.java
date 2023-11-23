package moe.plushie.armourers_workshop.init.platform.forge.provider;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ForgeCommonNativeProvider extends CommonNativeProvider {

    @Override
    default void willServerTick(Consumer<ServerLevel> consumer) {
        Registry.willServerTickFO(consumer);
    }

    @Override
    default void willServerStart(Consumer<MinecraftServer> consumer) {
        Registry.willServerStartFO(consumer);
    }

    @Override
    default void didServerStart(Consumer<MinecraftServer> consumer) {
        Registry.didServerStartFO(consumer);
    }

    @Override
    default void willServerStop(Consumer<MinecraftServer> consumer) {
        Registry.willServerStopFO(consumer);
    }

    @Override
    default void didServerStop(Consumer<MinecraftServer> consumer) {
        Registry.didServerStopFO(consumer);
    }

    @Override
    default void willPlayerLogin(Consumer<Player> consumer) {
        Registry.willPlayerLoginFO(consumer);
    }

    @Override
    default void willPlayerLogout(Consumer<Player> consumer) {
        Registry.willPlayerLogoutFO(consumer);
    }

    @Override
    default void willPlayerClone(BiConsumer<Player, Player> consumer) {
        Registry.willPlayerCloneFO(consumer);
    }

    @Override
    default void didEntityTacking(BiConsumer<Entity, Player> consumer) {
        Registry.didEntityTackingFO(consumer);
    }

    @Override
    default void didEntityJoin(Consumer<Entity> consumer) {
        Registry.didEntityJoinFO(consumer);
    }

    @Override
    default void willBlockPlace(BlockSnapshot consumer) {
        Registry.willBlockPlaceFO(consumer);
    }

    @Override
    default void willBlockBreak(BlockSnapshot consumer) {
        Registry.willBlockBreakFO(consumer);
    }

    @Override
    default void willRegisterArgument(Consumer<ArgumentRegistry> consumer) {
        Registry.willRegisterArgumentFO(consumer);
    }

    @Override
    default void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        Registry.willRegisterCommandFO(consumer);
    }

    @Override
    default void willRegisterEntityAttributes(Consumer<EntityAttributesRegistry> consumer) {
        Registry.willRegisterEntityAttributesFO(consumer);
    }

    @Override
    default void willRegisterCustomDataPack(Supplier<PreparableReloadListener> consumer) {
        Registry.willRegisterCustomDataPackFO(consumer);
    }

    @Override
    default void willPlayerDeath(Consumer<Player> consumer) {
        Registry.willEntityDeathFO(entity -> {
            if (entity instanceof Player) {
                consumer.accept((Player) entity);
            }
        });
    }
}
