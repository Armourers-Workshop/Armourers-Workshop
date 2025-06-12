package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.client.animation.AnimatedOutputMode;
import moe.plushie.armourers_workshop.core.client.animation.AnimatedOutputPoint;
import moe.plushie.armourers_workshop.core.client.animation.AnimatedTransform;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EpicFightEntityRendererPatch;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.skin.part.other.PartitionPartType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class BakedAdapterJointTransform implements ITransform, IJointTransform {

    private final BakedSkinPart part;
    private final AnimatedOutputPoint output;
    private final boolean isPartitionPart;

    private final OpenPoseStack tester = new OpenPoseStack();

    public BakedAdapterJointTransform(BakedSkinPart part) {
        this.part = part;
        this.isPartitionPart = part.type() instanceof PartitionPartType;
        this.output = new AnimatedOutputPoint(null, AnimatedOutputMode.MAIN);
        // the part is controlled by the adapter.
        var transform = AnimatedTransform.of(part.transform());
        if (transform != null) {
            transform.setController(output);
        }
    }

    public void setup(@Nullable Entity entity, BakedArmature armature, SkinRenderContext context) {
        // find the joint transform without joint modifier.
        var transform = armature.transformByJoint(armature.jointByPart(part));
        if (transform == null) {
            output.clear();
            return;
        }
        var renderData = context.renderData();
        if (renderData != null && renderData.renderPatch() instanceof EpicFightEntityRendererPatch) {
            setupEpicFight(transform, context);
        } else {
            setupVanilla(transform, context);
        }
    }

    private void setupVanilla(IJointTransform transform, SkinRenderContext context) {
        if (isPartitionPart) {
            output.clear();
            return;
        }
        tester.setIdentity();
        transform.apply(tester);
        // get rotation from pose.
        var quaternion = OpenQuaternionf.fromUnnormalizedMatrix(tester.last().pose());
        var rotation = quaternion.eulerAnglesZYX();
        var xRot = OpenMath.toDegrees(rotation.x());
        var yRot = OpenMath.toDegrees(rotation.y());
        var zRot = OpenMath.toDegrees(rotation.z());
        output.setRotation(xRot, yRot, zRot);
    }

    private void setupEpicFight(IJointTransform transform, SkinRenderContext context) {
        tester.setIdentity();
        transform.apply(tester);
        tester.scale(-1, -1, 1);

        // get rotation from pose.
        var quaternion = OpenQuaternionf.fromUnnormalizedMatrix(tester.last().pose());
        var rotation = quaternion.eulerAnglesZYX();
        var xRot = OpenMath.toDegrees(rotation.x());
        var yRot = OpenMath.toDegrees(rotation.y());
        var zRot = OpenMath.toDegrees(rotation.z());
        output.setRotation(xRot * -1, yRot * -1, zRot * 1);
    }

    @Override
    public void apply(IPoseStack poseStack) {
        // we don't need apply the transform in the adapt mode.
    }

    private boolean inEpicFight(SkinRenderContext context) {
        var renderData = context.renderData();
        if (renderData != null) {
            return renderData.renderPatch() instanceof EpicFightEntityRendererPatch;
        }
        return false;
    }
}
