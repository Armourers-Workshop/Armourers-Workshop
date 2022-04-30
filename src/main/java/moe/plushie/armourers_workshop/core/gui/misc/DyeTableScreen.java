package moe.plushie.armourers_workshop.core.gui.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.DyeTableContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class DyeTableScreen extends AWAbstractContainerScreen<DyeTableContainer> {

    public DyeTableScreen(DyeTableContainer container, PlayerInventory inventory, ITextComponent title) {
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
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);

        RenderUtils.bind(RenderUtils.TEX_DYE_TABLE);
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, 256, imageHeight);
        RenderUtils.blit(matrixStack, leftPos + 174 + 74, topPos, 174, 0, 82, imageHeight);

        this.renderMannequin(matrixStack, leftPos + 174, topPos + 23, 148, 159);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0x282216);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 0x282216);
    }

    private void renderMannequin(MatrixStack matrixStack, int x, int y, int width, int height) {
        SkinDescriptor descriptor = SkinDescriptor.of(menu.getOutputStack());
        if (!descriptor.isEmpty()) {
            IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            SkinItemRenderer.renderSkin(descriptor, x, y, 500, width, height, 160, 45, 0, matrixStack, buffers);
            buffers.endBatch();
        }
    }
}
