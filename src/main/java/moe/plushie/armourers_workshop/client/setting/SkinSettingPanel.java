package moe.plushie.armourers_workshop.client.setting;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Slot;

public class SkinSettingPanel extends BaseSettingPanel {


    private final SkinWardrobeContainer container;

    public SkinSettingPanel(SkinWardrobeContainer container) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.skins"));
        this.container = container;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(SkinCore.TEX_WARDROBE_2);
        for (Slot slot : container.getCustomSlots()) {
            if (slot.isActive()) {
                blit(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1, 238, 194, 18, 18);
            }
        }
    }
}
