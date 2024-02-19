package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractBlockEntityRenderer<T extends BlockEntity> extends AbstractBlockEntityRendererImpl<T> {

    public AbstractBlockEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public final void render(T entity, float f, PoseStack poseStack, MultiBufferSource bufferSource, int i, int j) {
        render(entity, f, AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(bufferSource), i, j);
    }

    public abstract void render(T entity, float f, IPoseStack poseStack, IBufferSource bufferSource, int i, int j);
}
