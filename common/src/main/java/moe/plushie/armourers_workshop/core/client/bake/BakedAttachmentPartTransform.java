package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractVehicleUpdater;
import moe.plushie.armourers_workshop.core.client.other.SceneGraphicsContext;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class BakedAttachmentPartTransform {

    protected final int index;

    protected final SkinAttachmentType type;
    protected final Collection<BakedSkinPart> children;

    protected BakedAttachmentPartTransform(SkinAttachmentType type, int index, Collection<BakedSkinPart> children) {
        this.type = type;
        this.index = index;
        this.children = children;
    }

    public static Collection<BakedAttachmentPartTransform> create(Collection<BakedSkinPart> parts) {
        var results = new ArrayList<BakedAttachmentPartTransform>();
        for (var part : parts) {
            collect(part, new Stack<>(), results);
        }
        // we need optimize it?
        return results;
    }

    private static void collect(BakedSkinPart part, Stack<BakedSkinPart> parent, ArrayList<BakedAttachmentPartTransform> results) {
        parent.push(part);
        // this object is a locator?
        if (part.type() == SkinPartTypes.ADVANCED_LOCATOR) {
            results.add(create(part.name(), new ArrayList<>(parent)));
        }
        // check the child tree.
        for (var child : part.children()) {
            collect(child, parent, results);
        }
        parent.pop();
    }

    protected static BakedAttachmentPartTransform create(String name, Collection<BakedSkinPart> children) {
        var pair = SkinAttachmentTypes.parse(name);
        var index = pair.getValue();
        var type = pair.getKey();
        if (type == SkinAttachmentTypes.RIDING) {
            return new Ridding(type, index, children);
        }
        return new BakedAttachmentPartTransform(type, index, children);
    }

    public void setup(EntityRenderState renderState, BakedArmature armature, float partialTicks, IPoseStack poseStack) {
        poseStack.pushPose();

        apply(renderState, armature, partialTicks, poseStack);

        if (ModDebugger.attachmentOverride && renderState != MannequinRenderState.getPlaceholder()) {
            var tesselator = SceneGraphicsContext.tesselator();
            tesselator.ctm().last().set(poseStack.last());
            tesselator.draw(ShapeElement.arrow());
        }

        poseStack.popPose();
    }

    protected void apply(EntityRenderState renderState, BakedArmature armature, float partialTicks, IPoseStack poseStack) {
        for (var child : children) {
            var jointTransform = armature.transformByPart(child);
            if (jointTransform != null) {
                jointTransform.apply(poseStack);
            }
            child.transform().apply(poseStack);
        }

        poseStack.scale(16, 16, 16);

        renderState.setAttachmentPose(type, index, new SkinAttachmentPose(poseStack.last()));
    }

    private static class Ridding extends BakedAttachmentPartTransform {

        protected Ridding(SkinAttachmentType type, int index, Collection<BakedSkinPart> children) {
            super(type, index, children);
        }

        @Override
        public void setup(EntityRenderState renderState, BakedArmature armature, float partialTicks, IPoseStack poseStack) {
            // theory we still need to compute in gui, but currently it not display in the gui.
            // and it will affect the update in the next frame start.
            if (renderState.shouldRenderInGUI()) {
                return;
            }

            // we need to use a separate pose stack, because the current pose stack is affected by the camera.
            var poseStack1 = new OpenPoseStack();
            apply(renderState, armature, partialTicks, poseStack1);

            // submit vehicle changes into the updater and defer updates.
            AbstractVehicleUpdater.getInstance().submit(renderState);

            if (ModDebugger.attachmentOverride && renderState != MannequinRenderState.getPlaceholder()) {
                var tesselator = SceneGraphicsContext.tesselator();
                tesselator.ctm().last().set(poseStack.last());
                tesselator.ctm().multiply(poseStack1.last().pose());
                tesselator.ctm().multiply(poseStack1.last().normal());
                tesselator.draw(ShapeElement.arrow());

//                poseStack.pushPose();
//                poseStack.setIdentity();
//                var cameraPos = Minecraft.getInstance().getCameraPosition();
//                var mat = OpenMatrix4f.createScaleMatrix(1, 1, 1);
//                mat.rotate(OpenVector3f.YP.rotationDegrees(180 - entity.getViewYRot(partialTicks)));
//                mat.scale(-1, -1, 1);
//                mat.scale(1.1f, 1.1f, 1.1f);
//                mat.translate(0, -1.501f, 0);
//                mat.scale(1 / 16f, 1 / 16f, 1 / 16f);
//                mat.multiply(poseStack1.last().pose());
//                var offset = OpenVector3f.ZERO.transforming(mat);
//                double d0 = OpenMath.lerp(partialTicks, renderState.xo, renderState.x) + offset.x() - cameraPos.x();
//                double d1 = OpenMath.lerp(partialTicks, renderState.yo, renderState.y) + offset.y() - cameraPos.y();
//                double d2 = OpenMath.lerp(partialTicks, renderState.zo, renderState.z) + offset.z() - cameraPos.z();
//                poseStack.translate((float) d0, (float) d1, (float) d2);
//                ShapeTesselator.vector(0, 0, 0, 2, 2, 2, poseStack, tesselator);
//                tesselator.endBatch();
//                poseStack.popPose();
            }
        }
    }
}
