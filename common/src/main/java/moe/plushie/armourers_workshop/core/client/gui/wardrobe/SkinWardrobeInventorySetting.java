package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.apple.library.coregraphics.CGGraphicsContext;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import com.apple.library.coregraphics.CGPoint;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeInventorySetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobeMenu container;

    public SkinWardrobeInventorySetting(SkinWardrobeMenu container) {
        super("inventory.armourers_workshop.wardrobe.skins");
        this.container = container;
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        PoseStack poseStack = context.poseStack;
        RenderSystem.setShaderTexture(0, ModTextures.WARDROBE_2);
        container.forEachCustomSlots(slot -> {
            RenderSystem.blit(poseStack, slot.x - 1, slot.y - 1, 238, 194, 18, 18);
        });
    }
}
