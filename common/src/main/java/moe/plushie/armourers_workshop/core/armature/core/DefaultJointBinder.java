package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.Joint;
import moe.plushie.armourers_workshop.core.armature.JointContext;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public class DefaultJointBinder extends JointModifier {

    private final String name;

    public DefaultJointBinder(String name, IODataObject parameters) {
        this.name = name;
    }

    @Override
    public IJointTransform apply(IJointTransform transform, Joint joint, JointContext context) {
        var pose = context.poses().byPartName(name);
        if (pose == null) {
            return transform;
        }
        return poseStack -> {
            transform.apply(poseStack);
            pose.transform(poseStack);
        };
    }
}
