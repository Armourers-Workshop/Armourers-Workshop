package moe.plushie.armourers_workshop.compatibility.fabric;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentSerializer;
import moe.plushie.armourers_workshop.init.platform.fabric.provider.CommonNativeProviderImpl;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class AbstractFabricCommonNativeImpl implements CommonNativeProviderImpl {

    private static <T extends IArgumentType<?>> void registerArgument(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
        ArgumentTypes.register(registryName.toString(), argumentType, new AbstractArgumentSerializer<>(argumentSerializer));
    }

    @Override
    public void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> consumer.accept(dispatcher));
    }

    @Override
    public void willRegisterArgument(Consumer<ArgumentRegistry> consumer) {
        consumer.accept(AbstractFabricCommonNativeImpl::registerArgument);
    }
}
