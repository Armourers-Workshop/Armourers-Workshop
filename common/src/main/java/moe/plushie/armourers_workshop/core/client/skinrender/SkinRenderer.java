package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentBufferBuilder;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class SkinRenderer {

    public static void render(Entity entity, BakedArmature armature, BakedSkin bakedSkin, SkinPaintScheme scheme, ConcurrentRenderingContext context) {
        var poseStack = context.poseStack();
        var bufferBuilder = context.getBuffer(bakedSkin);
        for (var bakedPart : bakedSkin.parts()) {
            var jointTransform = armature.transformByPart(bakedPart);
            if (jointTransform == null) {
                continue;
            }
            poseStack.pushPose();
            jointTransform.apply(poseStack);
            bakedPart.transform().apply(poseStack);
            bufferBuilder.addPart(bakedPart, bakedSkin, scheme, context);
            renderChild(entity, bakedPart, bakedSkin, scheme, bakedPart.isVisible(), bufferBuilder, context);
            renderDebugger(entity, bakedPart, bakedSkin, scheme, bakedPart.isVisible(), bufferBuilder, context);
            poseStack.popPose();
        }
        if (ModDebugger.skinBounds) {
            bufferBuilder.addShape(getShape(entity, armature, bakedSkin, poseStack), 0xffff0000, context);
        }
        if (ModDebugger.skinOrigin) {
            bufferBuilder.addShape(OpenVector3f.ZERO, context);
        }
        if (ModDebugger.armature) {
            bufferBuilder.addShape(armature, context);
        }
    }

    private static void renderChild(Entity entity, BakedSkinPart parentPart, BakedSkin skin, SkinPaintScheme scheme, boolean isVisible, ConcurrentBufferBuilder bufferBuilder, ConcurrentRenderingContext context) {
        var poseStack = context.poseStack();
        for (var part : parentPart.children()) {
            poseStack.pushPose();
            part.transform().apply(poseStack);
            bufferBuilder.addPart(part, skin, scheme, context);
            renderChild(entity, part, skin, scheme, isVisible, bufferBuilder, context);
            renderDebugger(entity, part, skin, scheme, isVisible, bufferBuilder, context);
            poseStack.popPose();
        }
    }

    private static void renderDebugger(Entity entity, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinPaintScheme scheme, boolean isVisible, ConcurrentBufferBuilder builder, ConcurrentRenderingContext context) {
        if (!isVisible) {
            return;
        }
        if (ModDebugger.skinPartBounds) {
            builder.addShape(bakedPart.renderShape(), ColorUtils.getPaletteColor(bakedPart.id()), context);
        }
        if (ModDebugger.skinPartOrigin && bakedPart.type() != SkinPartTypes.ADVANCED_LOCATOR) {
            builder.addShape(OpenVector3f.ZERO, context);
        }
        if (ModDebugger.skinLocatorOrigin && bakedPart.type() == SkinPartTypes.ADVANCED_LOCATOR) {
            builder.addShape(OpenVector3f.ZERO, context);
        }
    }

    public static OpenVoxelShape getShape(Entity entity, BakedArmature armature, BakedSkin bakedSkin, IPoseStack poseStack) {
        var voxelShape = new OpenVoxelShape();
        for (var part : bakedSkin.parts()) {
            if (!part.isVisible()) {
                continue; // ignore invisible part.
            }
            getShape(entity, voxelShape, part, bakedSkin, armature, poseStack);
        }
        return voxelShape;
    }

    private static void getShape(Entity entity, OpenVoxelShape shape, BakedSkinPart bakedPart, BakedSkin bakedSkin, BakedArmature armature, IPoseStack poseStack) {
        var jointTransform = armature.transformByPart(bakedPart);
        if (jointTransform == null) {
            return;
        }
        var shape1 = bakedPart.renderShape().copy();
        poseStack.pushPose();
        jointTransform.apply(poseStack);
        bakedPart.transform().apply(poseStack);
        shape1.mul(poseStack.last().pose());
        shape.add(shape1);
        for (var childPart : bakedPart.children()) {
            getChildShape(shape, childPart, poseStack);
        }
        poseStack.popPose();
    }

    private static void getChildShape(OpenVoxelShape shape, BakedSkinPart bakedPart, IPoseStack poseStack) {
        var shape1 = bakedPart.renderShape().copy();
        poseStack.pushPose();
        bakedPart.transform().apply(poseStack);
        shape1.mul(poseStack.last().pose());
        shape.add(shape1);
        for (var childPart : bakedPart.children()) {
            getChildShape(shape, childPart, poseStack);
        }
        poseStack.popPose();
    }
}
