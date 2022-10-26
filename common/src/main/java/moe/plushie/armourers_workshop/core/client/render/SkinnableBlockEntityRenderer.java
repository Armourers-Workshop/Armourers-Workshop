package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
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
    public void render(T entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, int overlay) {
        BakedSkin bakedSkin = BakedSkin.of(entity.getDescriptor());
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
        Quaternion rotations = entity.getRenderRotations(blockState);

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(rotations);

        matrixStack.scale(f, f, f);
        matrixStack.scale(-1, -1, 1);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(light, partialTicks1, matrixStack, buffers);
        renderer.render(mannequin, SkinRendererManager.wrap(model), bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, 0, context);
        context.clean();

        matrixStack.popPose();

        if (ModDebugger.skinnableBlock) {
            bakedSkin.getBlockBounds().forEach((pos, rect) -> {
                matrixStack.pushPose();
                matrixStack.translate(0.5f, 0.5f, 0.5f);
                matrixStack.scale(f, f, f);
                matrixStack.mulPose(rotations);
                matrixStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
                RenderSystem.drawBoundingBox(matrixStack, rect, UIColor.RED, buffers);
                matrixStack.popPose();
            });
            BlockPos pos = entity.getBlockPos();
            matrixStack.pushPose();
            matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderSystem.drawBoundingBox(matrixStack, entity.getCustomRenderBoundingBox(blockState), UIColor.ORANGE, buffers);
            matrixStack.popPose();
        }
    }
}
