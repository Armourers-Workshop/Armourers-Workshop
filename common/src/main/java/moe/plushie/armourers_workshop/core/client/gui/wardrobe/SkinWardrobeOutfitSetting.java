package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeOutfitSetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobeMenu container;

    public SkinWardrobeOutfitSetting(SkinWardrobeMenu container) {
        super("inventory.armourers_workshop.wardrobe.outfits");
        this.container = container;
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        RenderSystem.setShaderTexture(0, ModTextures.WARDROBE_2);
        container.forEachCustomSlots(slot -> {
            RenderSystem.blit(context.poseStack, slot.x - 1, slot.y - 1, 238, 194, 18, 18);
        });
    }
}
