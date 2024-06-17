package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class EntityPreviewView extends UIControl {

    private CGPoint lastMousePos;
    private Vector3f lastPlayerRotation = new Vector3f(-20, 45, 0);

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
        MannequinEntityRenderer.enableLimitScale = true;
        MannequinEntityRenderer.enableLimitYRot = true;
        context.saveGraphicsState();

        context.translateCTM(0, 0, 300);
        context.translateCTM(bounds.getMidX(), bounds.getMaxY() - 8, 50);
        context.rotateCTM(lastPlayerRotation.getX(), 0, 0);
        context.rotateCTM(0, lastPlayerRotation.getY(), 0);
        context.translateCTM(0, 0, -50);
        context.drawEntity(entity, CGPoint.ZERO, 45, CGPoint.ZERO);

        context.restoreGraphicsState();
        MannequinEntityRenderer.enableLimitYRot = false;
        MannequinEntityRenderer.enableLimitScale = false;
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
        var yRot = lastPlayerRotation.getY();
        this.lastMousePos = event.locationInWindow();
        this.lastPlayerRotation.setY((yRot + (lastMousePos.x - oldMousePos.x) + 360) % 360);
    }
}
