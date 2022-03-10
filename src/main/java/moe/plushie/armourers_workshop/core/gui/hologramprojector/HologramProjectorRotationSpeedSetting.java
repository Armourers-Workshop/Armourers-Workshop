package moe.plushie.armourers_workshop.core.gui.hologramprojector;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.block.HologramProjectorContainer;
import moe.plushie.armourers_workshop.core.gui.wardrobe.WardrobeBaseSetting;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
public class HologramProjectorRotationSpeedSetting extends WardrobeBaseSetting {

    protected int contentWidth = 200;
    protected int contentHeight = 82;

    public HologramProjectorRotationSpeedSetting(HologramProjectorContainer container) {
        super("inventory.armourers_workshop.hologram-projector.tab.rotationSpeed");
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int x = (width - contentWidth) / 2;
        RenderUtils.bind(RenderUtils.TEX_HOLOGRAM_PROJECTOR);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, 0, 0, 138, contentWidth, contentHeight, 38, 38, 4, 0);
    }
}