package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderPoseStack;
import moe.plushie.armourers_workshop.core.client.render.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
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
        CGPoint pos = convertPointToView(new CGPoint(bounds.width / 2, bounds.height - 8), null);
        AbstractRenderPoseStack modelViewStack = RenderSystem.getExtendedModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(0, 0, 300);
        modelViewStack.translate(pos.x, pos.y, 50);
        modelViewStack.mulPose(Vector3f.XP.rotationDegrees(-20));
        modelViewStack.mulPose(Vector3f.YP.rotationDegrees(playerRotation));
        modelViewStack.translate(0, 0, -50);
        modelViewStack.apply();

        MannequinEntityRenderer.enableLimitScale = true;
        InventoryScreen.renderEntityInInventory(0, 0, 45, 0, 0, entity);
        MannequinEntityRenderer.enableLimitScale = false;

        modelViewStack.popPose();
        modelViewStack.apply();
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
