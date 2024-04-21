package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

public abstract class EntityRenderPatch<T extends Entity> extends SkinRenderContext {

    private final int libraryVersion;

    public EntityRenderPatch(SkinRenderData renderData) {
        super(null);
        this.libraryVersion = SkinRendererManager.getVersion();
        this.setRenderData(renderData);
    }

    protected abstract void onActivate(T entity);

    protected abstract void onDeactivate(T entity);

    protected abstract void onRender(T entity);

    protected void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn) {
        setPartialTicks(partialTicks);
        setAnimationTicks(TickUtils.ticks());
        setLightmap(packedLight);
        setPose(AbstractPoseStack.wrap(poseStackIn));
        setBuffers(AbstractBufferSource.wrap(buffersIn));
    }

    protected boolean isValid() {
        return libraryVersion == SkinRendererManager.getVersion();
    }
}
