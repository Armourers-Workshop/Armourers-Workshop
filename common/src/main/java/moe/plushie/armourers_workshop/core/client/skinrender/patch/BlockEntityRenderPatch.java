package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmaturePluginContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityRenderPatch<T extends BlockEntity> {

    protected final SkinRenderContext renderContext = new SkinRenderContext();
    protected final DefaultArmaturePluginContext pluginContext = new DefaultArmaturePluginContext();

    public BlockEntityRenderPatch(BlockEntity blockEntity) {
    }

    public void activate(T entity, float partialTicks, int lightmap, int overlay, IPoseStack poseStack) {
        pluginContext.setAnimationTicks(TickUtils.animationTicks());
        pluginContext.setPartialTicks(partialTicks);
        pluginContext.setLightmap(lightmap);
        pluginContext.setOverlay(overlay);
        pluginContext.setPoseStack(poseStack);
    }

    public void deactivate(T entity) {
    }

    public DefaultArmaturePluginContext getPluginContext() {
        return pluginContext;
    }

    public SkinRenderContext getRenderingContext() {
        return renderContext;
    }
}
