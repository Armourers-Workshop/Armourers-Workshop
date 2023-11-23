package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class HologramProjectorBlockEntityRenderer<T extends HologramProjectorBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public HologramProjectorBlockEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }
        BlockState blockState = entity.getBlockState();
        ItemStack itemStack = entity.getItem(0);
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        SkinRenderTesselator context = SkinRenderTesselator.create(descriptor, Tickets.RENDERER);
        if (context == null) {
            return;
        }
        float f = 1 / 16f;
        float partialTicks1 = TickUtils.ticks();
        int overLight = light;
        if (entity.isOverrideLight()) {
            overLight = 0xf000f0;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(entity.getRenderRotations(blockState));
        poseStack.translate(0.0f, 0.5f, 0.0f);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        context.setLightmap(overLight);
        context.setPartialTicks(partialTicks1);
        context.setColorScheme(descriptor.getColorScheme());
        context.setReferenced(SkinItemSource.create(itemStack));

        Rectangle3f rect = context.getBakedRenderBounds();
        apply(entity, rect, partialTicks1, poseStack, buffers);

        context.draw(poseStack, buffers);

        poseStack.popPose();

        if (ModDebugger.hologramProjector) {
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            ShapeTesselator.stroke(entity.getCustomRenderBoundingBox(blockState), UIColor.ORANGE, poseStack, buffers);
            poseStack.popPose();
        }
    }

    private void apply(T entity, Rectangle3f rect, float partialTicks, PoseStack poseStack, MultiBufferSource buffers) {
        Vector3f angle = entity.getModelAngle();
        Vector3f offset = entity.getModelOffset();
        Vector3f rotationOffset = entity.getRotationOffset();
        Vector3f rotationSpeed = entity.getRotationSpeed();

        float rotX = angle.getX();
        float speedX = rotationSpeed.getX();
        if (speedX != 0) {
            rotX += ((partialTicks % speedX) / speedX) * 360.0f;
        }

        float rotY = angle.getY();
        float speedY = rotationSpeed.getY();
        if (speedY != 0) {
            rotY += ((partialTicks % speedY) / speedY) * 360.0f;
        }

        float rotZ = angle.getZ();
        float speedZ = rotationSpeed.getZ();
        if (speedZ != 0) {
            rotZ += ((partialTicks % speedZ) / speedZ) * 360.0f;
        }

        float scale = entity.getModelScale();
        poseStack.scale(scale, scale, scale);
        if (entity.isOverrideOrigin()) {
            poseStack.translate(0, -rect.getMaxY(), 0); // to model center
        }
        poseStack.translate(-offset.getX(), -offset.getY(), offset.getZ());

        if (entity.shouldShowRotationPoint()) {
            ShapeTesselator.stroke(-1, -1, -1, 1, 1, 1, UIColor.MAGENTA, poseStack, buffers);
        }

        if (ModDebugger.hologramProjector) {
            ShapeTesselator.vector(Vector3f.ZERO, 128, poseStack, buffers);
        }

        poseStack.mulPose(new OpenQuaternionf(rotX, -rotY, rotZ, true));
        poseStack.translate(rotationOffset.getX(), -rotationOffset.getY(), rotationOffset.getZ());

        if (ModDebugger.hologramProjector) {
            ShapeTesselator.vector(Vector3f.ZERO, 128, poseStack, buffers);
        }
    }

    @Override
    public int getViewDistance() {
        return 272;
    }
}
