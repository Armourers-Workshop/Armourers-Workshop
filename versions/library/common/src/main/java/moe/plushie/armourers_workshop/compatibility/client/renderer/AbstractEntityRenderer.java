package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractEntityRenderer<T extends Entity> extends AbstractEntityRendererImpl<T>  {

    public AbstractEntityRenderer(Context context) {
        super(context);
    }

    public void render(T entity, float f, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        super.render(entity, f, partialTicks, poseStack.cast(), buffers, packedLightIn);
    }

    @Override
    public void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        this.render(entity, f, g, MatrixUtils.of(poseStack), multiBufferSource, i);
    }
}
