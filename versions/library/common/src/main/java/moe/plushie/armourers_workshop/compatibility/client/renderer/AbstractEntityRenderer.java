package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class AbstractEntityRenderer<T extends Entity> extends AbstractEntityRendererImpl<T> {

    public AbstractEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public final void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        render(entity, f, g, AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(multiBufferSource), i);
    }

    public abstract void render(T entity, float f, float g, IPoseStack poseStack, IBufferSource multiBufferSource, int i);
}
