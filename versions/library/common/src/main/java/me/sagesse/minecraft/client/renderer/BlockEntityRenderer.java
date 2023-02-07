package me.sagesse.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(value= EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> extends AbstractBlockEntityRenderer<T> {

    public BlockEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context);
    }

    public abstract void render(T entity, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int lightmap, int overlay);

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int lightmap, int overlay) {
        render(entity, partialTicks, MatrixUtils.of(poseStack), buffers, lightmap, overlay);
    }
}
