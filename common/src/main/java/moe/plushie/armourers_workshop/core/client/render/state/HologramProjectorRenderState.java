package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.init.ModDebugger;

public class HologramProjectorRenderState extends BlockEntityRenderState {

    protected boolean isPowered = false;
    protected boolean isOverrideLight = false;
    protected boolean isOverrideOrigin = false;
    protected boolean shouldShowRotationPoint = false;

    protected OpenQuaternionf renderRotations = OpenQuaternionf.ONE;
    protected OpenRectangle3f renderShape = null;

    protected float modelScale = 1.0f;

    protected OpenVector3f modelAngle = OpenVector3f.ZERO;
    protected OpenVector3f modelOffset = OpenVector3f.ZERO;

    protected OpenVector3f rotationSpeed = OpenVector3f.ZERO;
    protected OpenVector3f rotationOffset = OpenVector3f.ZERO;

    protected final SkinRenderState slots = new SkinRenderState();

    public boolean isPowered() {
        return isPowered;
    }

    public boolean isOverrideLight() {
        return isOverrideLight;
    }

    public boolean isOverrideOrigin() {
        return isOverrideOrigin;
    }

    public boolean shouldShowRotationPoint() {
        return shouldShowRotationPoint;
    }

    public SkinRenderState slots() {
        return slots;
    }

    public OpenRectangle3f renderShape() {
        return renderShape;
    }

    public OpenQuaternionf renderRotations() {
        return renderRotations;
    }

    public float modelScale() {
        return modelScale;
    }

    public OpenVector3f modelAngle() {
        return modelAngle;
    }

    public OpenVector3f modelOffset() {
        return modelOffset;
    }

    public OpenVector3f rotationSpeed() {
        return rotationSpeed;
    }

    public OpenVector3f rotationOffset() {
        return rotationOffset;
    }

    public static void extract(HologramProjectorBlockEntity entity, HologramProjectorRenderState renderState) {
        renderState.isPowered = entity.isPowered();
        renderState.isOverrideLight = entity.isOverrideLight();
        renderState.isOverrideOrigin = entity.isOverrideOrigin();
        renderState.shouldShowRotationPoint = entity.shouldShowRotationPoint();
        renderState.modelAngle = entity.getModelAngle();
        renderState.modelOffset = entity.getModelOffset();
        renderState.rotationSpeed = entity.getRotationSpeed();
        renderState.rotationOffset = entity.getRotationOffset();
        var blockState = entity.getBlockState();
        var renderData = BlockEntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        renderData.tick(entity);
        renderState.animationManager = renderData.animationManager();
        renderState.slots.prepare(renderData.allSkins());
        renderState.renderRotations = entity.getRenderRotations(blockState).orElse(OpenQuaternionf.ONE);
        renderState.renderShape = null;
        if (ModDebugger.hologramProjector) {
            renderState.renderShape = entity.getRenderShape(blockState).orElse(OpenRectangle3f.ZERO);
        }
    }
}
