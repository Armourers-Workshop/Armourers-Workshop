package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Environment(value = EnvType.CLIENT)
public class SkinnableBlockEntityRenderer<T extends SkinnableBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public SkinnableBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffers, int light, int overlay) {
        BakedSkin bakedSkin = BakedSkin.of(entity.getDescriptor());
        if (bakedSkin == null) {
            return;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        BlockState blockState = entity.getBlockState();
        Entity mannequin = SkinItemRenderer.getInstance().getMannequinEntity();
        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(mannequin, model, null);
        if (renderer == null || mannequin == null || mannequin.level == null) {
            return;
        }
        float f = 1 / 16f;
        float partialTicks1 = TickUtils.ticks();
        OpenQuaternionf rotations = entity.getRenderRotations(blockState);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.rotate(rotations);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        IModelHolder<Model> modelHolder = ModelHolder.of(model);
        SkinRenderContext context = SkinRenderContext.alloc(null, light, partialTicks1, poseStack, buffers);
        context.setTransforms(mannequin, renderer.getOverrideModel(modelHolder));
        renderer.render(mannequin, modelHolder, bakedSkin, ColorScheme.EMPTY, context);
        context.release();

        poseStack.popPose();

        if (ModDebugger.skinnableBlock) {
            bakedSkin.getBlockBounds().forEach((pos, rect) -> {
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.scale(f, f, f);
                poseStack.rotate(rotations);
                poseStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
                RenderSystem.drawBoundingBox(poseStack, rect, UIColor.RED, buffers);
                poseStack.popPose();
            });
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderSystem.drawBoundingBox(poseStack, entity.getCustomRenderBoundingBox(blockState), UIColor.ORANGE, buffers);
            poseStack.popPose();
        }
    }
}
