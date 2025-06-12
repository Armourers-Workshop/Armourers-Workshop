package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGSize;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Environment(EnvType.CLIENT)
public class AbstractMenuScreen<T extends AbstractContainerMenu> extends AbstractMenuScreenImpl<T> {

    public AbstractMenuScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public void onClose() {
        super.onClose();
    }

    public CGPoint contentOffset() {
        return new CGPoint(leftPos, topPos);
    }

    public void setContentOffset(CGPoint offset) {
        leftPos = (int) offset.x;
        topPos = (int) offset.y;
    }

    public CGSize contentSize() {
        return new CGSize(imageWidth, imageHeight);
    }

    public void setContentSize(CGSize size) {
        imageWidth = (int) size.width;
        imageHeight = (int) size.height;
    }

    public CGSize screenSize() {
        return new CGSize(width, height);
    }

    public void setScreenSize(CGSize size) {
        width = (int) size.width;
        height = (int) size.height;
    }
}

