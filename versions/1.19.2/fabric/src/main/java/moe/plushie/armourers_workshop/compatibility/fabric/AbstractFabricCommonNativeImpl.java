package moe.plushie.armourers_workshop.compatibility.fabric;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentTypeInfo;
import moe.plushie.armourers_workshop.init.platform.fabric.provider.CommonNativeProviderImpl;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AbstractFabricCommonNativeImpl implements CommonNativeProviderImpl {

    private static <T extends IArgumentType<?>> void registerArgument(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
        AbstractArgumentTypeInfo<T> info = new AbstractArgumentTypeInfo<>(argumentSerializer);
        ArgumentTypeRegistry.registerArgumentType(registryName, argumentType, info);
    }

    @Override
    public void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher));
    }

    @Override
    public void willRegisterArgument(Consumer<ArgumentRegistry> consumer) {
        consumer.accept(AbstractFabricCommonNativeImpl::registerArgument);
    }
}
