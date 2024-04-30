package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterScreensEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Available("[1.21, )")
public class AbstractForgeRegisterScreensEvent {

    public static IEventHandler<RegisterScreensEvent> registryFactory() {
        return AbstractForgeClientEventsImpl.MENU_SCREEN_REGISTRY.map(event -> new RegisterScreensEvent() {

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> arg, Factory<M, U> arg2) {
                event.register(arg, arg2::create);
            }
        });
    }
}
