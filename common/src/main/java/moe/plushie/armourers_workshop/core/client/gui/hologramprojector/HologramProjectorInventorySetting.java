package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorInventorySetting extends AWTabPanel {

    protected int contentWidth = 176;
    protected int contentHeight = 40;

    public HologramProjectorInventorySetting(HologramProjectorBlockEntity entity) {
        super("inventory.armourers_workshop.hologram-projector.inventory");
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = (width - contentWidth) / 2;
        RenderUtils.blit(matrixStack, x, 0, 0, 98, contentWidth, contentHeight, RenderUtils.TEX_HOLOGRAM_PROJECTOR);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int k) {
        // click in the inventory?
        int invLeft = left + (width - contentWidth) / 2;
        return !(mouseX >= invLeft && mouseY >= top && mouseX <= (invLeft + contentWidth) && mouseY <= (top + contentHeight));
    }
}