package moe.plushie.armourers_workshop.core.client.skinrender;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentBufferBuilder;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class SkinRenderer {

    public static void render(Entity entity, BakedArmature armature, BakedSkin bakedSkin, ColorScheme scheme, ConcurrentRenderingContext context) {
        var poseStack = context.getPoseStack();
        var bufferBuilder = context.getBuffer(bakedSkin);
        for (var bakedPart : bakedSkin.getParts()) {
            var bakedTransform = armature.getTransform(bakedPart);
            if (bakedTransform == null) {
                continue;
            }
            poseStack.pushPose();
            bakedTransform.apply(poseStack);
            bakedPart.getTransform().apply(poseStack);
            bufferBuilder.addPart(bakedPart, bakedSkin, scheme, context);
            renderChild(entity, bakedPart, bakedSkin, scheme, bakedPart.isVisible(), bufferBuilder, context);
            renderDebugger(entity, bakedPart, bakedSkin, scheme, bakedPart.isVisible(), bufferBuilder, context);
            poseStack.popPose();
        }
        if (ModDebugger.skinBounds) {
            bufferBuilder.addShape(getShape(entity, armature, bakedSkin, poseStack), UIColor.RED, context);
        }
        if (ModDebugger.skinOrigin) {
            bufferBuilder.addShape(Vector3f.ZERO, context);
        }
        if (ModDebugger.armature) {
            bufferBuilder.addShape(armature, context);
        }
    }

    private static void renderChild(Entity entity, BakedSkinPart parentPart, BakedSkin skin, ColorScheme scheme, boolean isVisible, ConcurrentBufferBuilder bufferBuilder, ConcurrentRenderingContext context) {
        var poseStack = context.getPoseStack();
        for (var part : parentPart.getChildren()) {
            poseStack.pushPose();
            part.getTransform().apply(poseStack);
            bufferBuilder.addPart(part, skin, scheme, context);
            renderChild(entity, part, skin, scheme, isVisible, bufferBuilder, context);
            renderDebugger(entity, part, skin, scheme, isVisible, bufferBuilder, context);
            poseStack.popPose();
        }
    }

    private static void renderDebugger(Entity entity, BakedSkinPart bakedPart, BakedSkin bakedSkin, ColorScheme scheme, boolean isVisible, ConcurrentBufferBuilder builder, ConcurrentRenderingContext context) {
        if (!isVisible) {
            return;
        }
        if (ModDebugger.skinPartBounds) {
            builder.addShape(bakedPart.getRenderShape(), ColorUtils.getPaletteColor(bakedPart.getId()), context);
        }
        if (ModDebugger.skinPartOrigin) {
            builder.addShape(Vector3f.ZERO, context);
        }
    }

    public static OpenVoxelShape getShape(Entity entity, BakedArmature armature, BakedSkin bakedSkin, IPoseStack poseStack) {
        var voxelShape = OpenVoxelShape.empty();
        for (var part : bakedSkin.getParts()) {
            if (!part.isVisible()) {
                continue; // ignore invisible part.
            }
            getShape(entity, voxelShape, part, bakedSkin, armature, poseStack);
        }
        return voxelShape;
    }

    private static void getShape(Entity entity, OpenVoxelShape shape, BakedSkinPart bakedPart, BakedSkin bakedSkin, BakedArmature armature, IPoseStack poseStack) {
        var bakedTransform = armature.getTransform(bakedPart);
        if (bakedTransform == null) {
            return;
        }
        var shape1 = bakedPart.getRenderShape().copy();
        poseStack.pushPose();
        bakedTransform.apply(poseStack);
        bakedPart.getTransform().apply(poseStack);
        shape1.mul(poseStack.last().pose());
        shape.add(shape1);
        for (var childPart : bakedPart.getChildren()) {
            getChildShape(shape, childPart, poseStack);
        }
        poseStack.popPose();
    }

    private static void getChildShape(OpenVoxelShape shape, BakedSkinPart bakedPart, IPoseStack poseStack) {
        var shape1 = bakedPart.getRenderShape().copy();
        poseStack.pushPose();
        bakedPart.getTransform().apply(poseStack);
        shape1.mul(poseStack.last().pose());
        shape.add(shape1);
        for (var childPart : bakedPart.getChildren()) {
            getChildShape(shape, childPart, poseStack);
        }
        poseStack.popPose();
    }
}
