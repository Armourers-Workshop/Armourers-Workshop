package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public class DefaultJointBinder extends JointModifier {

    private final String name;

    public DefaultJointBinder(String name, IODataObject parameters) {
        this.name = name;
    }

    @Override
    public IJointTransform apply(IJoint joint, IModel model, IJointTransform transform) {
        var modelPart = model.partByName(name);
        if (modelPart == null) {
            return transform;
        }
        var pose = modelPart.pose();
        return poseStack -> {
            transform.apply(poseStack);
            pose.transform(poseStack);
        };
    }
}
