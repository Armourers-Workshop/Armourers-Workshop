package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.inventory.Slot;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeInventorySetting extends AWTabPanel {

    private final SkinWardrobeMenu container;

    public SkinWardrobeInventorySetting(SkinWardrobeMenu container) {
        super("inventory.armourers_workshop.wardrobe.skins");
        this.container = container;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bind(RenderUtils.TEX_WARDROBE_2);
        for (Slot slot : container.getCustomSlots()) {
            if (slot.isActive()) {
                RenderUtils.blit(matrixStack, leftPos + slot.x - 1, topPos + slot.y - 1, 238, 194, 18, 18);
            }
        }
    }
}
