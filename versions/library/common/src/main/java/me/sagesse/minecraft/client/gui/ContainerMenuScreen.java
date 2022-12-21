package me.sagesse.minecraft.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractMenuScreen;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Environment(value = EnvType.CLIENT)
public abstract class ContainerMenuScreen<T extends AbstractContainerMenu> extends AbstractMenuScreen<T> {

    public ContainerMenuScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public void render(IPoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack.cast(), mouseX, mouseY, partialTicks);
    }

    protected void renderBg(IPoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    }

    protected void renderLabels(IPoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack.cast(), mouseX, mouseY);
    }

    protected void renderTooltip(IPoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack.cast(), mouseX, mouseY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        render(MatrixUtils.of(poseStack), mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        renderBg(MatrixUtils.of(poseStack), partialTicks, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        renderLabels(MatrixUtils.of(poseStack), mouseX, mouseY);
    }

    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        renderTooltip(MatrixUtils.of(poseStack), mouseX, mouseY);
    }
}
