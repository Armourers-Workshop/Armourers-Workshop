package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityRenderPatch<T extends BlockEntity> extends SkinRenderContext {

    public BlockEntityRenderPatch(BlockEntity blockEntity) {
    }

    public void activate(T entity, float partialTicks, int lightmap, int overlay, IPoseStack poseStack, IBufferSource bufferSource) {
        setAnimationTicks(TickUtils.animationTicks());
        setPartialTicks(partialTicks);
        setTransformType(AbstractItemTransformType.NONE);
        setLightmap(lightmap);
        setOverlay(overlay);
        setPose(poseStack);
        setBuffers(bufferSource);
    }

    public void deactivate(T entity, float partialTicks, int lightmap, int overlay, IPoseStack poseStack, IBufferSource bufferSource) {
    }

}
