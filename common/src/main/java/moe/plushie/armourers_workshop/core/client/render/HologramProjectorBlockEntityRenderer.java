package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import me.sagesse.minecraft.client.renderer.BlockEntityRenderer;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.Quaternionf;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorBlockEntityRenderer<T extends HologramProjectorBlockEntity> extends BlockEntityRenderer<T> {

    public HologramProjectorBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }
        ItemStack itemStack = entity.getItem(0);
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin == null) {
            return;
        }
        BlockState blockState = entity.getBlockState();
        Entity mannequin = SkinItemRenderer.getInstance().getMannequinEntity();
        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(mannequin, model, null);
        if (renderer == null || mannequin == null || mannequin.level == null) {
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
        poseStack.rotate(entity.getRenderRotations(blockState));
        poseStack.translate(0.0f, 0.5f, 0.0f);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(mannequin, model, null, itemStack);
        apply(entity, rect, partialTicks1, poseStack, buffers);

        IModelHolder<Model> modelHolder = ModelHolder.of(model);
        SkinRenderContext context = SkinRenderContext.alloc(null, overLight, partialTicks1, poseStack, buffers);
        context.setItem(itemStack, 0);
        context.setTransforms(mannequin, renderer.getOverrideModel(modelHolder));
        renderer.render(mannequin, modelHolder, bakedSkin, ColorScheme.EMPTY, context);
        context.release();

        poseStack.popPose();

        if (ModDebugger.hologramProjectorBlock) {
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderSystem.drawBoundingBox(poseStack, entity.getCustomRenderBoundingBox(blockState), UIColor.ORANGE, buffers);
            poseStack.popPose();
        }
    }

    private void apply(T entity, Rectangle3f rect, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers) {
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
            RenderSystem.drawBoundingBox(poseStack, -1, -1, -1, 1, 1, 1, UIColor.MAGENTA, buffers);
        }

        if (ModDebugger.hologramProjectorBlock) {
            RenderSystem.drawPoint(poseStack, null, 128, buffers);
        }

        poseStack.rotate(new Quaternionf(rotX, -rotY, rotZ, true));
        poseStack.translate(rotationOffset.getX(), -rotationOffset.getY(), rotationOffset.getZ());

        if (ModDebugger.hologramProjectorBlock) {
            RenderSystem.drawPoint(poseStack, null, 128, buffers);
        }
    }

    @Override
    public int getViewDistance() {
        return 272;
    }
}
