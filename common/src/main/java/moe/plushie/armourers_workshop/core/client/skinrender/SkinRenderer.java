package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.element.SkinPartElement;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.init.ModDebugger;

public class SkinRenderer {

    public static int render(BakedSkin skin, SkinPaintScheme scheme, BakedArmature armature, ConcurrentRenderingContext context) {
        var poseStack = context.ctm();
        for (var part : skin.parts()) {
            var jointTransform = armature.transformByPart(part);
            if (jointTransform == null) {
                continue;
            }
            poseStack.pushPose();
            jointTransform.apply(poseStack);
            part.transform().apply(poseStack);
            context.draw(SkinPartElement.newInstance(part, skin, scheme, context));
            renderChild(part, skin, scheme, part.isVisible(), context);
            renderDebugger(part, skin, part.isVisible(), context);
            poseStack.popPose();
        }
        if (ModDebugger.skinBounds) {
            var shape = getShape(skin, armature, new OpenPoseStack());
            context.draw(ShapeElement.stroke(shape, Colors.RED));
        }
        if (ModDebugger.skinOrigin) {
            context.draw(ShapeElement.arrow());
        }
        if (ModDebugger.armature) {
            context.draw(ShapeElement.stroke(armature, true));
        }
        return getRenderCount(skin);
    }

    private static void renderChild(BakedSkinPart parentPart, BakedSkin skin, SkinPaintScheme scheme, boolean isVisible, ConcurrentRenderingContext context) {
        var poseStack = context.ctm();
        for (var part : parentPart.children()) {
            poseStack.pushPose();
            part.transform().apply(poseStack);
            context.draw(SkinPartElement.newInstance(part, skin, scheme, context));
            renderChild(part, skin, scheme, isVisible, context);
            renderDebugger(part, skin, isVisible, context);
            poseStack.popPose();
        }
    }

    private static void renderDebugger(BakedSkinPart part, BakedSkin skin, boolean isVisible, ConcurrentRenderingContext context) {
        if (!isVisible) {
            return;
        }
        if (ModDebugger.skinPartBounds) {
            context.draw(ShapeElement.stroke(part.renderShape(), Colors.getPaletteColor(part.id())));
        }
        if (ModDebugger.skinPartOrigin && part.type() != SkinPartTypes.ADVANCED_LOCATOR) {
            context.draw(ShapeElement.arrow());
        }
        if (ModDebugger.skinLocatorOrigin && part.type() == SkinPartTypes.ADVANCED_LOCATOR) {
            context.draw(ShapeElement.arrow());
        }
    }

    public static OpenVoxelShape getShape(BakedSkin skin, BakedArmature armature, IPoseStack poseStack) {
        var voxelShape = new OpenVoxelShape();
        for (var part : skin.parts()) {
            if (!part.isVisible()) {
                continue; // ignore invisible part.
            }
            getShape(part, skin, armature, poseStack, voxelShape);
        }
        return voxelShape;
    }

    private static void getShape(BakedSkinPart part, BakedSkin skin, BakedArmature armature, IPoseStack poseStack, OpenVoxelShape shape) {
        var jointTransform = armature.transformByPart(part);
        if (jointTransform == null) {
            return;
        }
        var shape1 = part.renderShape().copy();
        poseStack.pushPose();
        jointTransform.apply(poseStack);
        part.transform().apply(poseStack);
        shape1.mul(poseStack.last().pose());
        shape.add(shape1);
        for (var childPart : part.children()) {
            getChildShape(childPart, skin, poseStack, shape);
        }
        poseStack.popPose();
    }

    private static void getChildShape(BakedSkinPart part, BakedSkin skin, IPoseStack poseStack, OpenVoxelShape shape) {
        var shape1 = part.renderShape().copy();
        poseStack.pushPose();
        part.transform().apply(poseStack);
        shape1.mul(poseStack.last().pose());
        shape.add(shape1);
        for (var childPart : part.children()) {
            getChildShape(childPart, skin, poseStack, shape);
        }
        poseStack.popPose();
    }

    private static int getRenderCount(BakedSkin bakedSkin) {
        int count = 0;
        for (var part : bakedSkin.parts()) {
            if (part.isVisible()) {
                count += 1;
            }
        }
        return count;
    }
}
