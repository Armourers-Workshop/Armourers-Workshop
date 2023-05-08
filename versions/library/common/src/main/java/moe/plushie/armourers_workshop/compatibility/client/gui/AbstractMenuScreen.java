package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
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
public  class AbstractMenuScreen<T extends AbstractContainerMenu> extends AbstractMenuScreenImpl<T> {

    public AbstractMenuScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        // ignored
    }

    @Override
    public void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);
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

    public Font getFont() {
        return font;
    }
}

