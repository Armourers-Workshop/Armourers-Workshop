package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractEntityRenderer<T extends Entity> extends AbstractEntityRendererImpl<T>  {

    public AbstractEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }
}
