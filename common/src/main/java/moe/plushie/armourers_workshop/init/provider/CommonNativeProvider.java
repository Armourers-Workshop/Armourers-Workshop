package moe.plushie.armourers_workshop.init.provider;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface CommonNativeProvider {

    void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer);

    void willRegisterArgument(Consumer<ArgumentRegistry> consumer);

    void willRegisterCustomDataPack(Supplier<PreparableReloadListener> provider);

    void willRegisterEntityAttributes(Consumer<EntityAttributesRegistry> consumer);

    void willServerTick(Consumer<ServerLevel> consumer);

    void willServerStart(Consumer<MinecraftServer> consumer);

    void didServerStart(Consumer<MinecraftServer> consumer);

    void willServerStop(Consumer<MinecraftServer> consumer);

    void didServerStop(Consumer<MinecraftServer> consumer);

    void willPlayerLogin(Consumer<Player> consumer);

    void willPlayerLogout(Consumer<Player> consumer);

    void willPlayerClone(BiConsumer<Player, Player> consumer);

    void willPlayerDeath(Consumer<Player> consumer);

    void didEntityTacking(BiConsumer<Entity, Player> consumer);

    void didEntityJoin(Consumer<Entity> consumer);

    void willBlockPlace(BlockSnapshot consumer);

    void willBlockBreak(BlockSnapshot consumer);

    interface ArgumentRegistry {
        <T extends IArgumentType<?>> void register(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer);
    }

    interface EntityAttributesRegistry {
        void register(EntityType<? extends LivingEntity> entity, AttributeSupplier.Builder builder);
    }

    interface EntitySerializersRegistry {
        void register(EntityDataSerializer<?> arg);
    }

    interface Dispatcher {
        CompletableFuture<Void> enqueueWork(Runnable work);
    }

    interface BlockSnapshot {
        void snapshot(Player player, LevelAccessor level, BlockPos blockPos, @Nullable BlockState blockState, IBlockSnapshot snapshot);
    }
}
