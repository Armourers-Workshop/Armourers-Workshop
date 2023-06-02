package moe.plushie.armourers_workshop.compatibility.client.gui;

import moe.plushie.armourers_workshop.api.math.ISize2i;
import moe.plushie.armourers_workshop.api.math.IVector2i;
import moe.plushie.armourers_workshop.utils.math.Size2i;
import moe.plushie.armourers_workshop.utils.math.Vector2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Environment(value = EnvType.CLIENT)
public class AbstractMenuScreen<T extends AbstractContainerMenu> extends AbstractMenuScreenImpl<T> {

    public AbstractMenuScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public void onClose() {
        super.onClose();
    }

    public void setContentOffset(IVector2i offset) {
        leftPos = offset.getX();
        topPos = offset.getY();
    }

    public IVector2i getContentOffset() {
        return new Vector2i(leftPos, topPos);
    }

    public void setContentSize(ISize2i size) {
        imageWidth = size.getWidth();
        imageHeight = size.getHeight();
    }

    public ISize2i getContentSize() {
        return new Size2i(imageWidth, imageHeight);
    }

    public ISize2i getScreenSize() {
        return new Size2i(width, height);
    }
}

