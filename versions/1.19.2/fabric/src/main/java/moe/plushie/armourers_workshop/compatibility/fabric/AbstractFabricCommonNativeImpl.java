package moe.plushie.armourers_workshop.compatibility.fabric;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.compatibility.v19.CommonNativeProviderExt_V1920;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;

public class AbstractFabricCommonNativeImpl implements AbstractFabricCommonNativeProvider, CommonNativeProviderExt_V1920 {

    @Override
    public void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> consumer.accept(dispatcher));
    }

    @Override
    public void willRegisterArgumentInfo(Consumer<ArgumentInfoRegistry> consumer) {
        consumer.accept(ArgumentTypeRegistry::registerArgumentType);
    }
}
