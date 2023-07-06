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

    public void setContentOffset(CGPoint offset) {
        leftPos = (int) offset.getX();
        topPos = (int) offset.getY();
    }

    public CGPoint getContentOffset() {
        return new CGPoint(leftPos, topPos);
    }

    public void setContentSize(CGSize size) {
        imageWidth = (int) size.getWidth();
        imageHeight = (int) size.getHeight();
    }

    public CGSize getContentSize() {
        return new CGSize(imageWidth, imageHeight);
    }

    public CGSize getScreenSize() {
        return new CGSize(width, height);
    }

    public void setScreenSize(CGSize size) {
        width = (int) size.getWidth();
        height = (int) size.getHeight();
    }
}

