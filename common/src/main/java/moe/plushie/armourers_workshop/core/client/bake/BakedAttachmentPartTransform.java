package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractVehicleUpdater;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

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
        for (var skinPart : parts) {
            collect(skinPart, new Stack<>(), results);
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

    public void setup(@Nullable Entity entity, BakedArmature armature, SkinRenderContext context) {
        var renderData = context.renderData();
        if (renderData == null) {
            return;
        }
        var partialTicks = context.partialTicks();
        var poseStack = context.poseStack();
        setup(entity, armature, partialTicks, poseStack, renderData);
    }

    protected void setup(Entity entity, BakedArmature armature, float partialTicks, IPoseStack poseStack, EntityRenderData renderData) {
        poseStack.pushPose();

        apply(entity, armature, partialTicks, poseStack, renderData);

        if (ModDebugger.attachmentOverride && !PlaceholderManager.isPlaceholder(entity)) {
            var tesselator = AbstractBufferSource.tesselator();
            ShapeTesselator.vector(0, 0, 0, 1, 1, 1, poseStack, tesselator);
            tesselator.endBatch();
        }

        poseStack.popPose();
    }

    protected void apply(Entity entity, BakedArmature armature, float partialTicks, IPoseStack poseStack, EntityRenderData renderData) {
        for (var child : children) {
            var jointTransform = armature.transformByPart(child);
            if (jointTransform != null) {
                jointTransform.apply(poseStack);
            }
            child.transform().apply(poseStack);
        }

        poseStack.scale(16, 16, 16);

        renderData.setAttachmentPose(type, index, new SkinAttachmentPose(poseStack.last()));
    }

    private static class Ridding extends BakedAttachmentPartTransform {

        protected Ridding(SkinAttachmentType type, int index, Collection<BakedSkinPart> children) {
            super(type, index, children);
        }

        @Override
        protected void setup(Entity entity, BakedArmature armature, float partialTicks, IPoseStack poseStack, EntityRenderData renderData) {
            // theory we still need to compute in gui, but currently it not display in the gui.
            // and it will affect the update in the next frame start.
            if (SkinRenderMode.inGUI()) {
                return;
            }

            // we need to use a separate pose stack, because the current pose stack is affected by the camera.
            var poseStack1 = new OpenPoseStack();
            apply(entity, armature, partialTicks, poseStack1, renderData);

            // submit vehicle changes into the updater and defer updates.
            AbstractVehicleUpdater.getInstance().submit(entity);

            if (ModDebugger.attachmentOverride && !PlaceholderManager.isPlaceholder(entity)) {
                var tesselator = AbstractBufferSource.tesselator();
                poseStack.pushPose();
                poseStack.multiply(poseStack1.last().pose());
                poseStack.multiply(poseStack1.last().normal());
                ShapeTesselator.vector(0, 0, 0, 1, 1, 1, poseStack, tesselator);
                tesselator.endBatch();
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.setIdentity();
                var cameraPos = Minecraft.getInstance().getCameraPosition();
                var mat = OpenMatrix4f.createScaleMatrix(1, 1, 1);
                mat.rotate(OpenVector3f.YP.rotationDegrees(180 - entity.getViewYRot(partialTicks)));
                mat.scale(-1, -1, 1);
                mat.scale(1.1f, 1.1f, 1.1f);
                mat.translate(0, -1.501f, 0);
                mat.scale(1 / 16f, 1 / 16f, 1 / 16f);
                mat.multiply(poseStack1.last().pose());
                var offset = OpenVector3f.ZERO.transforming(mat);
                double d0 = OpenMath.lerp(partialTicks, entity.xOld, entity.getX()) + offset.x() - cameraPos.x();
                double d1 = OpenMath.lerp(partialTicks, entity.yOld, entity.getY()) + offset.y() - cameraPos.y();
                double d2 = OpenMath.lerp(partialTicks, entity.zOld, entity.getZ()) + offset.z() - cameraPos.z();
                poseStack.translate((float) d0, (float) d1, (float) d2);
                ShapeTesselator.vector(0, 0, 0, 2, 2, 2, poseStack, tesselator);
                tesselator.endBatch();
                poseStack.popPose();
            }
        }
    }
}
