package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.gui.element.SkinGuiElement;
import moe.plushie.armourers_workshop.core.data.ticket.TicketHolder;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;

public class SkinPreviewView extends UIControl {

    private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

    private final TicketHolder tickets = new TicketHolder("SkinPreviewView");

    public SkinPreviewView(CGRect frame) {
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
        var colorScheme = descriptor.paintScheme();
        context.draw(SkinGuiElement.blit(bakedSkin, colorScheme, rect.x, rect.y, 200, rect.width, rect.height, 20, 45, 0));
    }

    public SkinDescriptor skin() {
        return descriptor;
    }

    public void setSkin(SkinDescriptor descriptor) {
        this.tickets.invalidate();
        this.descriptor = descriptor;
    }
}
