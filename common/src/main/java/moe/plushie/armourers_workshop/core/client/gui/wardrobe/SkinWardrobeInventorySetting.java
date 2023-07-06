package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinWardrobeInventorySetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobeMenu container;

    public SkinWardrobeInventorySetting(SkinWardrobeMenu container) {
        super("inventory.armourers_workshop.wardrobe.skins");
        this.container = container;
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        container.forEachCustomSlots(slot -> context.drawImage(ModTextures.WARDROBE_2, slot.x - 1, slot.y - 1, 18, 18, 238, 194, 256, 256));
    }
}
