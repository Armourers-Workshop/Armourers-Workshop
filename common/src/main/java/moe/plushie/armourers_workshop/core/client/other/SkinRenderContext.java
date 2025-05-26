package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class SkinRenderContext implements ConcurrentRenderingContext {

    public static final SkinRenderContext EMPTY = new SkinRenderContext();
    private static final Iterator<SkinRenderContext> POOL = Collections.cycle(Collections.newList(100, i -> new SkinRenderContext()));

    protected int lightmap = 0xf000f0;
    protected int overlay = OverlayTexture.NO_OVERLAY;
    protected int outlineColor = 0;
    protected float partialTicks = 0;
    protected double animationTicks = 0;

    protected IBufferSource bufferSource;

    protected EntityRenderData renderData;
    protected Function<BakedSkin, ConcurrentBufferBuilder> bufferProvider;

    protected SkinItemSource itemSource;
    protected boolean useItemTransforms = false;
    protected OpenVector3f displayBox;


    protected SkinPaintScheme colorScheme = SkinPaintScheme.EMPTY;
    protected AnimationManager animationManager;
    protected OpenItemDisplayContext displayContext = OpenItemDisplayContext.NONE;

    protected final IPoseStack defaultPoseStack;
    protected IPoseStack poseStack;
    protected IPoseStack modelViewStack;

    public SkinRenderContext() {
        this(new AbstractPoseStack());
    }

    public SkinRenderContext(IPoseStack poseStack) {
        this.defaultPoseStack = poseStack;
        this.poseStack = defaultPoseStack;
    }

    public static SkinRenderContext alloc(EntityRenderData renderData, int light, float partialTick, OpenItemDisplayContext itemDisplayContext) {
        var context = POOL.next();
        context.setRenderData(renderData);
        context.setLightmap(light);
        context.setPartialTicks(partialTick);
        context.setAnimationTicks(TickUtils.animationTicks());
        context.setDisplayBox(null);
        context.setDisplayContext(itemDisplayContext);
        context.setUseItemTransforms(false);
        return context;
    }

    public static SkinRenderContext alloc(EntityRenderData renderData, int light, float partialTick) {
        return alloc(renderData, light, partialTick, OpenItemDisplayContext.NONE);
    }

    public void release() {
        this.overlay = OverlayTexture.NO_OVERLAY;
        this.lightmap = 0xf000f0;
        this.outlineColor = 0;
        this.partialTicks = 0;

        this.colorScheme = SkinPaintScheme.EMPTY;
        this.displayContext = OpenItemDisplayContext.NONE;
        this.itemSource = SkinItemSource.EMPTY;
        this.displayBox = null;
        this.useItemTransforms = false;

        this.poseStack = defaultPoseStack;

        this.bufferProvider = null;
        this.renderData = null;
        this.bufferSource = null;
        this.animationManager = null;
    }

    public void pushPose() {
        poseStack.pushPose();
    }

    public void popPose() {
        poseStack.popPose();
    }

    public IPoseStack pose() {
        return poseStack;
    }

    public void setLightmap(int lightmap) {
        this.lightmap = lightmap;
    }

    public int getLightmap() {
        return lightmap;
    }

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    public int getOverlay() {
        return overlay;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setAnimationTicks(double animationTicks) {
        this.animationTicks = animationTicks;
    }

    public double getAnimationTicks() {
        return animationTicks;
    }

    public void setColorScheme(SkinPaintScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public SkinPaintScheme getColorScheme() {
        return colorScheme;
    }

    public void setDisplayContext(OpenItemDisplayContext displayContext) {
        this.displayContext = displayContext;
    }

    public OpenItemDisplayContext getDisplayContext() {
        return displayContext;
    }

    public void setDisplayBox(OpenVector3f displayBox) {
        this.displayBox = displayBox;
    }

    public OpenVector3f getDisplayBox() {
        return displayBox;
    }

    public void setRenderData(EntityRenderData renderData) {
        this.renderData = renderData;
    }

    public EntityRenderData getRenderData() {
        return renderData;
    }

    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    public AnimationManager getAnimationManager() {
        if (renderData != null) {
            return renderData.getAnimationManager();
        }
        if (animationManager != null) {
            return animationManager;
        }
        return AnimationManager.NONE;
    }

    @Override
    public ConcurrentBufferBuilder getBuffer(@NotNull BakedSkin skin) {
        if (bufferProvider != null) {
            return bufferProvider.apply(skin);
        }
        var usedBufferSource = bufferSource;
        if (outlineColor != 0) {
            usedBufferSource = AbstractBufferSource.outline();
        }
        var bufferBuilder = SkinVertexBufferBuilder.of(usedBufferSource, skin.getRenderInfo());
        return bufferBuilder.getBuffer(skin);
    }

    public void setBufferProvider(Function<BakedSkin, ConcurrentBufferBuilder> bufferProvider) {
        this.bufferProvider = bufferProvider;
    }

    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }

    public int getOutlineColor() {
        return outlineColor;
    }

    @Override
    public boolean shouldRenderOutline() {
        return outlineColor != 0;
    }

    @Override
    public float getRenderPriority() {
        if (itemSource != null) {
            return itemSource.getRenderPriority();
        }
        return 0;
    }

    public void setItemSource(SkinItemSource itemSource) {
        this.itemSource = itemSource;
    }

    @Override
    public SkinItemSource getItemSource() {
        if (this.itemSource != null) {
            return this.itemSource;
        }
        return SkinItemSource.EMPTY;
    }

    public void setUseItemTransforms(boolean useItemTransforms) {
        this.useItemTransforms = useItemTransforms;
    }

    public boolean isUseItemTransforms() {
        return useItemTransforms;
    }

    public void setPoseStack(IPoseStack pose) {
        this.poseStack = pose;
    }

    @Override
    public IPoseStack getPoseStack() {
        return poseStack;
    }

    public void setBufferSource(IBufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    @Override
    public IBufferSource getBufferSource() {
        return bufferSource;
    }

    public void setModelViewStack(IPoseStack modelViewStack) {
        this.modelViewStack = modelViewStack;
    }

    @Override
    public IPoseStack getModelViewStack() {
        return modelViewStack;
    }
}
