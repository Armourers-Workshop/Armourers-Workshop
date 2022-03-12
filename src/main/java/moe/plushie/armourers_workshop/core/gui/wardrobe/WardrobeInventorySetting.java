package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.wardrobe.WardrobeContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class WardrobeInventorySetting extends AWTabPanel {

    private final WardrobeContainer container;

    public WardrobeInventorySetting(WardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.skins");
        this.container = container;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bind(RenderUtils.TEX_WARDROBE_2);
        for (Slot slot : container.getCustomSlots()) {
            if (slot.isActive()) {
                RenderUtils.blit(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1, 238, 194, 18, 18);
            }
        }
    }
}
