package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ISize2i;
import moe.plushie.armourers_workshop.api.math.IVector2i;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
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

    public void render(IPoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack.cast(), mouseX, mouseY, partialTicks);
    }

    public void renderLabels(IPoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack.cast(), mouseX, mouseY);
    }

    public void renderTooltip(IPoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack.cast(), mouseX, mouseY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        render(MatrixUtils.of(poseStack), mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        renderLabels(MatrixUtils.of(poseStack), mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        // ignored
    }

    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        renderTooltip(MatrixUtils.of(poseStack), mouseX, mouseY);
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

