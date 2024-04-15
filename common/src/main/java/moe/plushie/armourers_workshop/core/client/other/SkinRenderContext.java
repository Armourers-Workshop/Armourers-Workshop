package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.Iterators;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
public class SkinRenderContext {

    public static final SkinRenderContext EMPTY = new SkinRenderContext();
    private static final Iterator<SkinRenderContext> POOL = Iterators.cycle(ObjectUtils.makeItems(100, i -> new SkinRenderContext()));

    protected int lightmap = 0xf000f0;
    protected int overlay = 0;
    protected float partialTicks = 0;
    protected float animationTicks = 0;

    protected IBufferSource bufferSource;

    protected SkinRenderData renderData;
    protected SkinRenderBufferSource bufferProvider;

    protected SkinItemSource itemSource;

    protected ColorScheme colorScheme = ColorScheme.EMPTY;
    protected AbstractItemTransformType transformType = AbstractItemTransformType.NONE;

    protected final IPoseStack defaultPoseStack;
    protected IPoseStack poseStack;

    public SkinRenderContext() {
        this(new AbstractPoseStack());
    }

    public SkinRenderContext(IPoseStack poseStack) {
        this.defaultPoseStack = poseStack;
        this.poseStack = defaultPoseStack;
    }

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, AbstractItemTransformType transformType, IPoseStack poseStack, IBufferSource bufferSource) {
        SkinRenderContext context = POOL.next();
        context.setRenderData(renderData);
        context.setLightmap(light);
        context.setPartialTicks(partialTick);
        context.setAnimationTicks(TickUtils.ticks());
        context.setTransformType(transformType);
        context.setPose(poseStack);
        context.setBuffers(bufferSource);
        return context;
    }

    public static SkinRenderContext alloc(SkinRenderData renderData, int light, float partialTick, IPoseStack poseStack, IBufferSource bufferSource) {
        return alloc(renderData, light, partialTick, AbstractItemTransformType.NONE, poseStack, bufferSource);
    }

    public void release() {
        this.lightmap = 0xf000f0;
        this.partialTicks = 0;

        this.colorScheme = ColorScheme.EMPTY;
        this.transformType = AbstractItemTransformType.NONE;
        this.itemSource = SkinItemSource.EMPTY;

        this.poseStack = defaultPoseStack;

        this.bufferProvider = null;
        this.renderData = null;
        this.bufferSource = null;
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

    public void setAnimationTicks(float animationTicks) {
        this.animationTicks = animationTicks;
    }

    public float getAnimationTicks() {
        return animationTicks;
    }

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setTransformType(AbstractItemTransformType transformType) {
        this.transformType = transformType;
    }

    public AbstractItemTransformType getTransformType() {
        return transformType;
    }

    public void setReferenced(SkinItemSource itemSource) {
        this.itemSource = itemSource;
    }

    public SkinItemSource getReferenced() {
        if (this.itemSource != null) {
            return this.itemSource;
        }
        return SkinItemSource.EMPTY;
    }

    public void setRenderData(SkinRenderData renderData) {
        this.renderData = renderData;
    }

    public SkinRenderData getRenderData() {
        return renderData;
    }

    public void setPose(IPoseStack pose) {
        this.poseStack = pose;
    }

    public void setBuffers(IBufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    public IBufferSource getBuffers() {
        return bufferSource;
    }

    public SkinRenderBufferSource.ObjectBuilder getBuffer(@NotNull BakedSkin skin) {
        if (bufferProvider != null) {
            return bufferProvider.getBuffer(skin);
        }
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(bufferSource);
        return bufferBuilder.getBuffer(skin);
    }

    public void setBufferProvider(SkinRenderBufferSource bufferProvider) {
        this.bufferProvider = bufferProvider;
    }
}
