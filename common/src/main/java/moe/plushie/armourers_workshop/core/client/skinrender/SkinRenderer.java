package moe.plushie.armourers_workshop.core.client.skinrender;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderBufferSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.auto;

public class SkinRenderer {

    public static int render(Entity entity, BakedArmature armature, BakedSkin bakedSkin, ColorScheme scheme, SkinRenderContext context) {
        int counter = 0;
        auto scheme1 = bakedSkin.resolve(entity, scheme);
        auto builder = context.getBuffer(bakedSkin);
        for (auto bakedPart : bakedSkin.getParts()) {
            auto bakedTransform = armature.getTransform(bakedPart);
            if (bakedTransform == null) {
                continue;
            }
            boolean shouldRenderPart = shouldRenderPart(entity, bakedPart, bakedSkin, context);
            context.pushPose();
            bakedTransform.apply(context.pose());
            bakedPart.getTransform().apply(context.pose());
            builder.addPart(bakedPart, bakedSkin, scheme1, shouldRenderPart, context);
            renderChild(entity, bakedPart, bakedSkin, scheme1, shouldRenderPart, builder, context);
            renderDebugger(entity, bakedPart, bakedSkin, scheme1, shouldRenderPart, builder, context);
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
            context.popPose();
        }
        if (ModDebugger.skinBounds) {
            builder.addShape(bakedSkin.getRenderShape(entity, armature, context.getReferenced()), UIColor.RED, context);
        }
        if (ModDebugger.skinOrigin) {
            builder.addShape(Vector3f.ZERO, context);
        }
        if (ModDebugger.armature) {
            builder.addShape(armature, context);
        }
        return counter;
    }

    private static void renderChild(Entity entity, BakedSkinPart parentPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRenderPart, SkinRenderBufferSource.ObjectBuilder builder, SkinRenderContext context) {
        for (auto bakedPart : parentPart.getChildren()) {
            context.pushPose();
            bakedPart.getTransform().apply(context.pose());
            builder.addPart(bakedPart, bakedSkin, scheme, shouldRenderPart, context);
            renderChild(entity, bakedPart, bakedSkin, scheme, shouldRenderPart, builder, context);
            renderDebugger(entity, bakedPart, bakedSkin, scheme, shouldRenderPart, builder, context);
            context.popPose();
        }
    }

    private static void renderDebugger(Entity entity, BakedSkinPart bakedPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRenderPart, SkinRenderBufferSource.ObjectBuilder builder, SkinRenderContext context) {
        if (!shouldRenderPart) {
            return;
        }
        if (ModDebugger.skinPartBounds) {
            builder.addShape(bakedPart.getRenderShape(), ColorUtils.getPaletteColor(bakedPart.getId()), context);
        }
        if (ModDebugger.skinPartOrigin) {
            builder.addShape(Vector3f.ZERO, context);
        }
    }

    public static OpenVoxelShape getShape(Entity entity, BakedArmature armature, BakedSkin bakedSkin, SkinRenderContext context) {
        OpenVoxelShape voxelShape = OpenVoxelShape.empty();
        for (BakedSkinPart bakedPart : bakedSkin.getParts()) {
            getShape(entity, voxelShape, bakedPart, bakedSkin, armature, context);
        }
        return voxelShape;
    }

    private static void getShape(Entity entity, OpenVoxelShape shape, BakedSkinPart bakedPart, BakedSkin bakedSkin, BakedArmature armature, SkinRenderContext context) {
        // ignore invisible part.
        if (!shouldRenderPart(entity, bakedPart, bakedSkin, context)) {
            return;
        }
        auto bakedTransform = armature.getTransform(bakedPart);
        if (bakedTransform == null) {
            return;
        }
        OpenVoxelShape shape1 = bakedPart.getRenderShape().copy();
        context.pushPose();
        bakedTransform.apply(context.pose());
        bakedPart.getTransform().apply(context.pose());
        shape1.mul(context.pose().last().pose());
        shape.add(shape1);
        for (BakedSkinPart childPart : bakedPart.getChildren()) {
            getChildShape(shape, childPart, context);
        }
        context.popPose();
    }

    private static void getChildShape(OpenVoxelShape shape, BakedSkinPart bakedPart, SkinRenderContext context) {
        OpenVoxelShape shape1 = bakedPart.getRenderShape().copy();
        context.pushPose();
        bakedPart.getTransform().apply(context.pose());
        shape1.mul(context.pose().last().pose());
        shape.add(shape1);
        for (BakedSkinPart childPart : bakedPart.getChildren()) {
            getChildShape(shape, childPart, context);
        }
        context.popPose();
    }

    private static boolean shouldRenderPart(Entity entity, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        return bakedSkin.shouldRenderPart(entity, bakedPart, context);
    }
}
