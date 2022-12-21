package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class AbstractPoseStack implements IPoseStack {

    @Environment(value = EnvType.CLIENT)
    @Override
    public PoseStack cast() {
        IPoseStack poseStack = MatrixUtils.stack();
        poseStack.lastPose().multiply(lastPose());
        poseStack.lastNormal().multiply(lastNormal());
        return poseStack.cast();
    }
}
