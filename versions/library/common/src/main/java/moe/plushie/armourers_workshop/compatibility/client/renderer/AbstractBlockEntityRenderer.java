package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
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
    public abstract void render(T entity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j);
}
