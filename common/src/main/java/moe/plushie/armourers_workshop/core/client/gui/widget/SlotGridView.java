package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.inventory.Slot;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SlotGridView extends UIView {

    private int startIndex = 0;
    private int endIndex = 0;
    private List<Slot> slots;

    public SlotGridView(CGRect frame) {
        super(frame);
    }

    public void reloadData(List<Slot> slots, int startIndex, int endIndex) {
        this.slots = slots;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        if (slots == null) {
            return;
        }
        for (int i = startIndex; i < endIndex; ++i) {
            Slot slot = slots.get(i);
            context.drawImage(ModTextures.COMMON, slot.x - 1, slot.y - 1, 238, 0, 18, 18, 256, 256);
        }
    }
}
