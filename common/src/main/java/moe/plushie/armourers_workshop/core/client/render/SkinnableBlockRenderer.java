package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinnableBlockRenderer<T extends SkinnableBlockEntity> extends AbstractBlockEntityRenderer<T> {

    private final BakedArmature armature = new BakedArmature(Armatures.ANY);
    private final Supplier<MannequinEntity> placeholder = PlaceholderManager.MANNEQUIN;

    public SkinnableBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        auto descriptor = entity.getDescriptor();
        auto skin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (skin == null) {
            return;
        }
        float f = 1 / 16f;

        BlockState blockState = entity.getBlockState();
        OpenQuaternionf rotations = entity.getRenderRotations(blockState);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(rotations);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        SkinRenderContext context = SkinRenderContext.alloc(null, light, TickUtils.ticks(), poseStack, buffers);
        SkinRenderer.render(placeholder.get(), armature, skin, descriptor.getColorScheme(), context);
        context.release();

        poseStack.popPose();

        if (ModDebugger.skinnable) {
            skin.getBlockBounds().forEach((pos, rect) -> {
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.scale(f, f, f);
                poseStack.mulPose(rotations);
                poseStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
                ShapeTesselator.stroke(rect, UIColor.RED, poseStack, buffers);
                poseStack.popPose();
            });
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getCustomRenderBoundingBox(blockState), UIColor.ORANGE, poseStack, buffers);
            poseStack.popPose();
        }
    }
}
