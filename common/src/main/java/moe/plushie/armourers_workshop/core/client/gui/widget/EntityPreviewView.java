package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class EntityPreviewView extends UIControl {

    private CGPoint lastMousePos;
    private float playerRotation = 45.0f;

    public EntityPreviewView(CGRect frame) {
        super(frame);
        this.setClipBounds(true);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        LivingEntity entity = ObjectUtils.safeCast(contents(), LivingEntity.class);
        if (entity == null) {
            return;
        }
        CGRect bounds = bounds();
        MannequinEntityRenderer.enableLimitScale = true;
        MannequinEntityRenderer.enableLimitYRot = true;
        context.saveGraphicsState();
        context.translateCTM(0, 0, 300);
        context.translateCTM(bounds.width / 2f, bounds.height - 8, 50);
        context.rotateCTM(-20, 0, 0);
        context.rotateCTM(0, playerRotation, 0);
        context.translateCTM(0, 0, -50);
        context.drawEntity(entity, 0, 0, 45, 0, 0);

        context.restoreGraphicsState();
        MannequinEntityRenderer.enableLimitYRot = false;
        MannequinEntityRenderer.enableLimitScale = false;
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
        CGPoint oldMousePos = this.lastMousePos;
        if (oldMousePos == null) {
            return;
        }
        this.lastMousePos = event.locationInWindow();
        this.playerRotation = (playerRotation + (lastMousePos.x - oldMousePos.x) + 360) % 360;
    }
}
