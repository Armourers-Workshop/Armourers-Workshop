package moe.plushie.armourers_workshop.core.gui.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.SkinnableContainer;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.List;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class SkinnableScreen extends ContainerScreen<SkinnableContainer> {

    public SkinnableScreen(SkinnableContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, container.getTitle());

        this.imageWidth = 176;
        this.imageHeight = container.getRow() * 18 + 125;
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
        RenderUtils.bind(RenderUtils.TEX_COMMON);
        GuiUtils.drawContinuousTexturedBox(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 128, 128, 4, 4, 4, 4, 0);
        RenderUtils.blit(matrixStack, leftPos + 7, topPos + imageHeight - 85, 0, 180, 162, 76);
        List<Slot> slots = menu.slots;
        for (int i = 36; i < slots.size(); ++i) {
            Slot slot = slots.get(i);
            RenderUtils.blit(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1, 238, 0, 18, 18);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
