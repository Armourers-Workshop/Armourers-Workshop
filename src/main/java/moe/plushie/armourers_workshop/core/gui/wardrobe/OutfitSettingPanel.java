package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.inventory.container.Slot;

public class OutfitSettingPanel extends BaseSettingPanel {

    private final SkinWardrobeContainer container;

    public OutfitSettingPanel(SkinWardrobeContainer container) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.outfits"));
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
