package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinWardrobeDyeSetting extends AWTabPanel {

    private final SkinWardrobeContainer container;

    public SkinWardrobeDyeSetting(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.dyes");
        this.container = container;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bind(RenderUtils.TEX_WARDROBE_2);
        int slotCount = 0;
        for (Slot slot : container.getCustomSlots()) {
            if (slot.isActive()) {
                RenderUtils.blit(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1, 238, 194, 18, 18);
                RenderUtils.blit(matrixStack, leftPos + slot.x, topPos + slot.y + 18, 112 + slotCount++ * 16, 240, 16, 16);
            }
        }
    }
}