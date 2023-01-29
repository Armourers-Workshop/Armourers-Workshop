package moe.plushie.armourers_workshop.init.platform.forge.provider;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ForgeCommonNativeProvider extends CommonNativeProvider {

    void willEntityDrop(Consumer<Entity> consumer);

    void willConfigReload(Consumer<ForgeConfigSpec> consumer);

    void shouldAttackEntity(BiFunction<Entity, Player, InteractionResult> consumer);

    @Override
    default void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        NotificationCenterImpl.observer(RegisterCommandsEvent.class, consumer, RegisterCommandsEvent::getDispatcher);
    }

    @Override
    default void willRegisterEntityAttributes(Consumer<EntityAttributesRegistry> consumer) {
        NotificationCenterImpl.observer(EntityAttributeCreationEvent.class, consumer, event -> (entity, builder) -> event.put(entity, builder.build()));
    }

    @Override
    default void willRegisterCustomDataPack(Supplier<PreparableReloadListener> consumer) {
        NotificationCenterImpl.observer(AddReloadListenerEvent.class, event -> event.addListener(consumer.get()));
    }

    @Override
    default void willPlayerDrop(Consumer<Player> consumer) {
        willEntityDrop(entity -> {
            if (entity instanceof Player) {
                consumer.accept((Player) entity);
            }
        });
    }
}
