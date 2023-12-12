package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class SkinnableBlockRenderer<T extends SkinnableBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public SkinnableBlockRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        SkinDescriptor descriptor = entity.getDescriptor();
        SkinRenderTesselator tesselator = SkinRenderTesselator.create(descriptor, Tickets.RENDERER);
        if (tesselator == null) {
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

        tesselator.setLightmap(light);
        tesselator.setPartialTicks(TickUtils.ticks());
        tesselator.setColorScheme(descriptor.getColorScheme());

        tesselator.draw(poseStack, buffers);

        poseStack.popPose();

        if (ModDebugger.skinnable) {
            tesselator.getBakedSkin().getBlockBounds().forEach((pos, rect) -> {
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
