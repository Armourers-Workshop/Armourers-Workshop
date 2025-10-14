package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.TicketHolder;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinIconView extends UIControl {

    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private final TicketHolder tickets = new TicketHolder("SkinIconView");

    public SkinIconView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        var bakedSkin = SkinBakery.getInstance().loadSkin(tickets.get(descriptor));
        if (bakedSkin == null) {
            return;
        }
        var rect = bounds();
        float tx = rect.x;
        float ty = rect.y;
        float tw = rect.width;
        float th = rect.height;
        float si = Math.min(tw, th);
        var poseStack = context.state().ctm();
        var colorScheme = descriptor.paintScheme();
        var itemSource = SkinItemSource.EMPTY;
        var buffers = AbstractBufferSource.buffer();
        poseStack.pushPose();
        poseStack.translate(tx + tw / 2f, ty + th / 2f, 200);
        poseStack.scale(1, -1, 1);
        poseStack.rotate(OpenVector3f.XP.rotationDegrees(30));
        poseStack.rotate(OpenVector3f.YP.rotationDegrees(135));
        poseStack.scale(0.625f, 0.625f, 0.625f);
        poseStack.scale(si, si, si);
        ExtendedItemRenderer.renderSkinInBox(bakedSkin, colorScheme, 0, 0xf000f0, itemSource, poseStack, buffers);
        poseStack.popPose();
        buffers.endBatch();
    }

    public SkinDescriptor skin() {
        return descriptor;
    }

    public void setSkin(SkinDescriptor descriptor) {
        this.tickets.invalidate();
        this.descriptor = descriptor;
    }
}
