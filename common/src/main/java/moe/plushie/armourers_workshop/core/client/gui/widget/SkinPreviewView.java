package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinPreviewView extends UIControl {

    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private final Ticket loadTicket = Ticket.wardrobe();

    public SkinPreviewView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        auto bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
        if (bakedSkin == null) {
            return;
        }
        CGRect rect = bounds();
        float tx = rect.x;
        float ty = rect.y;
        float tw = rect.width;
        float th = rect.height;
        auto poseStack = context.state().ctm();
        auto colorScheme = descriptor.getColorScheme();
        auto itemStack = ItemStack.EMPTY;
        auto buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        ExtendedItemRenderer.renderSkinInGUI(bakedSkin, colorScheme, itemStack, tx, ty, 200, tw, th, 20, 45, 0, 0, 0xf000f0, poseStack, buffers);
        buffers.endBatch();
    }

    public SkinDescriptor skin() {
        return descriptor;
    }

    public void setSkin(SkinDescriptor descriptor) {
        this.loadTicket.invalidate();
        this.descriptor = descriptor;
    }
}
