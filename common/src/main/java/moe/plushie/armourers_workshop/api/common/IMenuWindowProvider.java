package moe.plushie.armourers_workshop.api.common;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIWindow;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IMenuWindowProvider<T extends AbstractContainerMenu, S extends UIWindow> {

    @NotNull
    S createMenuWindow(T var1, Inventory var2, NSString title);
}
