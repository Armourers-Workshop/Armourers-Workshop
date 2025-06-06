package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModConfig;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BakedLuminanceCalculator {

    private static final OpenRectangle3f DEFAULT = new OpenRectangle3f(0, 0, 0, 8, 8, 8);
    private static final Map<SkinPartType, OpenRectangle3f> BOXES = Collections.immutableMap(builder -> {
        builder.put(SkinPartTypes.BIPPED_HAT, new OpenRectangle3f(0, 0, 0, 8, 8, 8));
        builder.put(SkinPartTypes.BIPPED_HEAD, new OpenRectangle3f(0, 0, 0, 8, 8, 8));
        builder.put(SkinPartTypes.BIPPED_CHEST, new OpenRectangle3f(0, 0, 0, 4, 12, 4));

        builder.put(SkinPartTypes.BIPPED_LEFT_ARM, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_ARM, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_LEFT_THIGH, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_LEFT_FOOT, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_FOOT, new OpenRectangle3f(0, 0, 0, 4, 12, 4));

        builder.put(SkinPartTypes.BIPPED_LEFT_WING, new OpenRectangle3f(0, 0, 0, 8, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_WING, new OpenRectangle3f(0, 0, 0, 8, 12, 4));
        builder.put(SkinPartTypes.BIPPED_LEFT_PHALANX, new OpenRectangle3f(0, 0, 0, 8, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_PHALANX, new OpenRectangle3f(0, 0, 0, 8, 12, 4));

        builder.put(SkinPartTypes.BIPPED_TORSO, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_LEFT_HAND, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_HAND, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_LEFT_LEG, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
        builder.put(SkinPartTypes.BIPPED_RIGHT_LEG, new OpenRectangle3f(0, 0, 0, 4, 12, 4));
    });

    public static int apply(List<BakedSkinPart> skinParts) {
        // only calculate when user requires accurate luminance.
        if (ModConfig.Client.skinDynamicLight >= 0) {
            return ModConfig.Client.skinDynamicLight;
        }
        var strength = new OpenVector3f();
        var poseStack = new OpenPoseStack();
        for (var skinPart : skinParts) {
            var fully = BOXES.getOrDefault(skinPart.getType(), DEFAULT);
            var used = new OpenVector3f();
            extract(skinPart, poseStack, rect -> {
                var tx = rect.depth() * rect.height();
                var ty = rect.width() * rect.depth();
                var tz = rect.width() * rect.height();
                used.add(tx, ty, tz);
            });
            var tx = used.x() / (fully.depth() * fully.height());
            var ty = used.y() / (fully.width() * fully.depth());
            var tz = used.z() / (fully.width() * fully.height());
            strength.add(tx, ty, tz);
        }
        var luminance = OpenMath.max(strength.x(), strength.y(), strength.z()) * 15;
        return OpenMath.clamp(OpenMath.ceili(luminance), 0, 15);
    }

    private static void extract(BakedSkinPart part, OpenPoseStack poseStack, Consumer<OpenRectangle3f> applier) {
        // add light info when have growing channel.
        part.getQuads().forEach((renderType, quad) -> {
            if (renderType.isEmissive()) {
                var shape = OpenVoxelShape.empty();
                quad.forEach((transform, faces) -> merge(faces, transform, poseStack, shape));
                applier.accept(shape.bounds());
            }
        });
        // check the children.
        for (var child : part.getChildren()) {
            poseStack.pushPose();
            child.getTransform().apply(poseStack);
            extract(child, poseStack, applier);
            poseStack.popPose();
        }
    }

    private static void merge(List<BakedGeometryFace> faces, ITransform quadTransform, OpenPoseStack poseStack, OpenVoxelShape shape) {
        poseStack.pushPose();
        quadTransform.apply(poseStack);
        faces.forEach(face -> {
            var faceTransform = face.getTransform();
            if (faceTransform != OpenTransform3f.IDENTITY) {
                poseStack.pushPose();
                faceTransform.apply(poseStack);
            }
            for (var vertex : face.getVertices()) {
                shape.add(vertex.getPosition().transforming(poseStack.last().pose()));
            }
            if (faceTransform != OpenTransform3f.IDENTITY) {
                poseStack.popPose();
            }
        });
        poseStack.popPose();
    }
}
