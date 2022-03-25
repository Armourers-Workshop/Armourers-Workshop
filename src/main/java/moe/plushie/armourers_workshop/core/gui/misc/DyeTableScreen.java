package moe.plushie.armourers_workshop.core.gui.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.DyeTableContainer;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class DyeTableScreen extends ContainerScreen<DyeTableContainer> {

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

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    private void renderMannequin(MatrixStack matrixStack, int x, int y, int width, int height) {
        SkinDescriptor descriptor = SkinDescriptor.of(menu.getOutputStack());
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            return;
        }
        int t = (int) System.currentTimeMillis();
        int size = Math.min(width, height);
        IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        matrixStack.pushPose();
        matrixStack.translate(x + width / 2f, y + height / 2f, 100f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(150));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(45 - (float) (t / 10 % 360)));
        matrixStack.scale(0.625f, 0.625f, 0.625f);
        matrixStack.scale(size, size, size);
        matrixStack.scale(-1, 1, 1);
        SkinItemRenderer.renderSkin(bakedSkin, descriptor.getColorScheme(), 0, 0xf000f0, matrixStack, buffers);
        matrixStack.popPose();
        buffers.endBatch();
    }
}
