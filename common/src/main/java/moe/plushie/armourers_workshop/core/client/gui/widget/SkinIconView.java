package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.gui.element.SkinGuiElement;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.data.ticket.TicketHolder;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;

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
        var tx = rect.x;
        var ty = rect.y;
        var tw = rect.width;
        var th = rect.height;
        var si = Math.min(tw, th);
        var colorScheme = descriptor.paintScheme();
        context.saveGraphicsState();
        context.translateCTM(tx + tw / 2f, ty + th / 2f, 200);
        context.scaleCTM(1, -1, 1);
        context.rotateCTM(30, 135, 0);
        context.scaleCTM(0.625f, 0.625f, 0.625f);
        context.scaleCTM(si, si, si);
        context.draw(SkinGuiElement.blit(bakedSkin, colorScheme, SkinItemSource.EMPTY));
        context.restoreGraphicsState();
    }

    public SkinDescriptor skin() {
        return descriptor;
    }

    public void setSkin(SkinDescriptor descriptor) {
        this.tickets.invalidate();
        this.descriptor = descriptor;
    }
}
