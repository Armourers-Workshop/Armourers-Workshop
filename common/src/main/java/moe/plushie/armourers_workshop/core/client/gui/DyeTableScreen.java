package moe.plushie.armourers_workshop.core.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.menu.DyeTableMenu;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings({"unused"})
@Environment(value = EnvType.CLIENT)
public class DyeTableScreen extends AWAbstractContainerScreen<DyeTableMenu> {

    public DyeTableScreen(DyeTableMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 330;
        this.imageHeight = 190;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = imageHeight - 96;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);

        RenderUtils.bind(RenderUtils.TEX_DYE_TABLE);
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, 256, imageHeight);
        RenderUtils.blit(matrixStack, leftPos + 174 + 74, topPos, 174, 0, 82, imageHeight);

        this.renderMannequin(matrixStack, leftPos + 174, topPos + 23, 148, 159);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0x282216);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 0x282216);
    }

    private void renderMannequin(PoseStack matrixStack, int x, int y, int width, int height) {
        SkinDescriptor descriptor = SkinDescriptor.of(menu.getOutputStack());
        if (!descriptor.isEmpty()) {
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkin(descriptor, ItemStack.EMPTY, x, y, 500, width, height, 160, 45, 0, matrixStack, buffers);
            buffers.endBatch();
        }
    }
}
