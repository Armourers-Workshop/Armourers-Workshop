package moe.plushie.armourers_workshop.compatibility.fabric;

import com.mojang.brigadier.CommandDispatcher;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractCommonNativeExt_V1618;
import moe.plushie.armourers_workshop.compatibility.fabric.ext.AbstractFabricCommonExt_V1618;
import moe.plushie.armourers_workshop.init.platform.fabric.provider.FabricCommonNativeProvider;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Consumer;

public class AbstractFabricCommonNativeImpl implements FabricCommonNativeProvider, AbstractCommonNativeExt_V1618, AbstractFabricCommonExt_V1618 {

    @Override
    public void willRegisterCommand(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> consumer.accept(dispatcher));
    }
}
