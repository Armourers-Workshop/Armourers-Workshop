package moe.plushie.armourers_workshop.library.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.library.container.GlobalSkinLibraryContainer;
import moe.plushie.armourers_workshop.library.container.SkinLibraryContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlobalSkinLibraryScreen extends ContainerScreen<GlobalSkinLibraryContainer> {

    public GlobalSkinLibraryScreen(GlobalSkinLibraryContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = width;
        this.imageHeight = height;
        super.init();

        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = imageHeight - 96;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
//        RenderUtils.bind(RenderUtils.TEX_DYE_TABLE);
//        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, 256, imageHeight);
//        RenderUtils.blit(matrixStack, leftPos + 174 + 74, topPos, 174, 0, 82, imageHeight);
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
}
