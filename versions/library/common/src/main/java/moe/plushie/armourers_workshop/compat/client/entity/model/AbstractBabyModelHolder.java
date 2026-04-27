package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

@Available("[16, )")
public class AbstractBabyModelHolder extends AbstractBabyModelHolderImpl implements IModelPart, IModelPartPose, IModelBabyPose {

    private final float headScale;
    private final OpenVector3f headOffset;

    private final float bodyScale;
    private final OpenVector3f bodyOffset;

    public AbstractBabyModelHolder(IEntityModel<?> entityModel) {
        super(entityModel);
        this.headScale = super.headScale();
        this.headOffset = super.headOffset();
        this.bodyScale = super.bodyScale();
        this.bodyOffset = super.bodyOffset();
    }

    @Override
    public boolean isVisible() {
        return isBaby();
    }

    @Override
    public void setVisible(boolean visible) {
        // nop
    }

    @Override
    public IModelPartPose pose() {
        return this;
    }

    @Override
    public float x() {
        return headOffset.x;
    }

    @Override
    public float y() {
        return headOffset.y;
    }

    @Override
    public float z() {
        return headOffset.z;
    }

    @Override
    public float xRot() {
        return 0;
    }

    @Override
    public float yRot() {
        return 0;
    }

    @Override
    public float zRot() {
        return 0;
    }

    @Override
    public void setPos(float x, float y, float z) {
        // nop
    }

    @Override
    public void setRotation(float xRot, float yRot, float zRot) {
        // nop
    }

    @Override
    public void transform(IPoseStack poseStack) {
        if (!isVisible()) {
            return;
        }
        // the current version no offset required, but maybe it will change in a future version?
        //var offset = headOffset.scaling(headScale).subtracting(bodyOffset.scaling(bodyScale));
        //poseStack.translate(offset.x(), offset.y(), offset.z());

        // the current context is applying body scale, so we only need to multiply diff of head scale and body scale.
        var scale = headScale / bodyScale;
        poseStack.scale(scale, scale, scale);
    }

    @Override
    public float headScale() {
        return headScale;
    }

    @Override
    public OpenVector3f headOffset() {
        return headOffset;
    }

    @Override
    public float bodyScale() {
        return bodyScale;
    }

    @Override
    public OpenVector3f bodyOffset() {
        return bodyOffset;
    }
}
