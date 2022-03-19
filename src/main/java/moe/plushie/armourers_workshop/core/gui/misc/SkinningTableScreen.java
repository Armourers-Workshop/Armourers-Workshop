package moe.plushie.armourers_workshop.core.gui.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.SkinningTableContainer;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class SkinningTableScreen extends ContainerScreen<SkinningTableContainer> {

    private final SkinningTableContainer container;

    public SkinningTableScreen(SkinningTableContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.container = container;
        this.imageWidth = 176;
        this.imageHeight = 176;
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
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, RenderUtils.TEX_SKINNING_TABLE);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0x282216);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 0x282216);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
