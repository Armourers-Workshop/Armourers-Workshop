package moe.plushie.armourers_workshop.compatibility.fabric;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface AbstractFabricMenuType {

    static <T extends AbstractContainerMenu> MenuType<T> create(ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(factory);
    }
}
