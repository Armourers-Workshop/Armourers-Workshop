package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UILabel;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PlayerInventoryView;
import moe.plushie.armourers_workshop.core.menu.SkinnableMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

@Environment(value = EnvType.CLIENT)
public class SkinnableWindow extends MenuWindow<SkinnableMenu> {

    public SkinnableWindow(SkinnableMenu container, Inventory inventory, NSString title) {
        super(container, inventory, new NSString(container.getInventoryName()));
        this.setFrame(new CGRect(0, 0, 176, container.getRow() * 18 + 124));
        this.inventoryView.setStyle(PlayerInventoryView.Style.COMPACT);
        this.setContents(ModTextures.defaultWindowImage());
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        RenderSystem.bind(ModTextures.COMMON);
        List<Slot> slots = menu.slots;
        for (int i = 36; i < slots.size(); ++i) {
            Slot slot = slots.get(i);
            RenderSystem.blit(context.poseStack, slot.x - 1, slot.y - 1, 238, 0, 18, 18);
        }
    }

    @Override
    public boolean shouldRenderExtendScreen() {
        return true;
    }
}
