package moe.plushie.armourers_workshop.compatibility.client;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public interface AbstractMenuWindowProvider<M extends AbstractContainerMenu, W extends UIWindow> {

    @NotNull
    W create(M var1, Inventory var2, NSString title);

    @NotNull
    default ContainerMenuScreen<M, W> createScreen(M menu, Inventory inventory, Component title) {
        try {
            W window = create(menu, inventory, new NSString(title));
            return new ContainerMenuScreen<>(window, menu, inventory, title);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }
}
