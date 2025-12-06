package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.world.entity.Entity;

public class EntityPreviewView extends UIControl {

    private CGPoint lastMousePos;
    private OpenVector3f lastPlayerRotation = new OpenVector3f(-20, 45, 0);

    public EntityPreviewView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        if (!(contents() instanceof Entity entity)) {
            return;
        }
        var bounds = bounds();
        RenderSystem.setExtendedScissorFlags(1);
        context.saveGraphicsState();

        context.translateCTM(0, 0, 300);
        context.translateCTM(bounds.midX(), bounds.maxY() - 8, 50);
        context.rotateCTM(lastPlayerRotation.x(), 0, 0);
        context.rotateCTM(0, lastPlayerRotation.y(), 0);
        context.translateCTM(0, 0, -50);

        context.drawEntity(entity, CGPoint.ZERO, 45, CGPoint.ZERO);

        context.restoreGraphicsState();
        RenderSystem.setExtendedScissorFlags(0);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        super.setHighlighted(highlighted);
        this.setClipBounds(!highlighted);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        this.lastMousePos = null;
        if (event.type() == UIEvent.Type.MOUSE_RIGHT_DOWN) {
            this.lastMousePos = event.locationInWindow();
        }
    }

    @Override
    public void mouseDragged(UIEvent event) {
        super.mouseDragged(event);
        var oldMousePos = this.lastMousePos;
        if (oldMousePos == null) {
            return;
        }
        var yRot = lastPlayerRotation.y();
        this.lastMousePos = event.locationInWindow();
        this.lastPlayerRotation.setY((yRot + (lastMousePos.x - oldMousePos.x) + 360) % 360);
    }
}
