package extensions.net.minecraft.core.Registry;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class CommonEventProvider {

    public static void willDropEntityFA(@ThisClass Class<?> clazz, Consumer<Entity> consumer) {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damageAmount) -> {
            consumer.accept(entity);
            return true;
        });
    }

    public static void willRegisterCommandFA(@ThisClass Class<?> clazz, Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher));
    }

    public static void willRegisterArgumentFA(@ThisClass Class<?> clazz, Consumer<CommonNativeProvider.ArgumentRegistry> consumer) {
        consumer.accept(new CommonNativeProvider.ArgumentRegistry() {
            @Override
            public <T extends IArgumentType<?>> void register(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
                ArgumentTypeRegistry.registerArgumentType(registryName, argumentType, new AbstractArgumentTypeInfo<>(argumentSerializer));
            }
        });
    }
}
