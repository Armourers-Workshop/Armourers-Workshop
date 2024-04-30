package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.RegisterCommandsEvent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

@Available("[1.19, )")
public class AbstractFabricRegisterCommandsEvent {

    public static IEventHandler<RegisterCommandsEvent> registryFactory() {
        return subscriber -> CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> subscriber.accept(dispatcher::register)));
    }
}
