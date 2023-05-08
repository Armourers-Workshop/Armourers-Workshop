package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.armature.ArmatureModifier;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;

public class DefaultJointBinder extends ArmatureModifier {

    private final String name;

    public DefaultJointBinder(String name) {
        this.name = name;
    }

    @Override
    public ITransformf apply(ITransformf transform, IModelHolder<?> model) {
        ModelPart part = model.getPart(name);
        if (part == null) {
            return transform;
        }
        return poseStack -> {
            transform.apply(poseStack);
            if (part.x != 0 || part.y != 0 || part.z != 0) {
                poseStack.translate(part.x, part.y, part.z);
            }
            if (part.zRot != 0) {
                poseStack.mulPose(Vector3f.ZP.rotation(part.zRot));
            }
            if (part.yRot != 0) {
                poseStack.mulPose(Vector3f.YP.rotation(part.yRot));
            }
            if (part.xRot != 0) {
                poseStack.mulPose(Vector3f.XP.rotation(part.xRot));
            }
        };
    }
}
