package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;

public class SkinRenderContext implements ConcurrentRenderingContext {

    private static final ObjectPool<SkinRenderContext> POOL = ObjectPool.create(SkinRenderContext::new);

    private int overlay;
    private int lightmap;

    private float partialTick = 1.0f;
    private double animationTick = 0.0;
    private AnimationManager animationManager;
    private SkinItemSource itemSource;

    private int outlineColor = 0;

    private IGraphicsContext context;

    public static SkinRenderContext newInstance(IGraphicsContext context) {
        var that = POOL.alloc();
        that.context = context;
        that.lightmap = LightmapTexture.DEFAULT;
        that.overlay = OverlayTexture.NO_OVERLAY;
        that.animationManager = AnimationManager.NONE;
        that.itemSource = SkinItemSource.EMPTY;
        that.outlineColor = 0;
        return that;
    }

    @Override
    public void draw(IGraphicsElement element) {
        context.draw(element);
    }

    @Override
    public IPoseStack ctm() {
        return context.ctm();
    }

    public void setOutlineColor(int outlineColor) {
        this.outlineColor = outlineColor;
    }

    @Override
    public int outlineColor() {
        return outlineColor;
    }

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    @Override
    public int overlay() {
        return overlay;
    }

    public void setLightmap(int lightmap) {
        this.lightmap = lightmap;
    }

    @Override
    public int lightmap() {
        return lightmap;
    }

    public void setPartialTick(float partialTick) {
        this.partialTick = partialTick;
    }

    @Override
    public float partialTick() {
        return partialTick;
    }

    public void setAnimationTick(double animationTick) {
        this.animationTick = animationTick;
    }

    @Override
    public double animationTick() {
        return animationTick;
    }

    public void setAnimationManager(AnimationManager animationManager) {
        this.animationManager = animationManager;
    }

    @Override
    public AnimationManager animationManager() {
        return animationManager;
    }

    public void setItemSource(SkinItemSource itemSource) {
        this.itemSource = itemSource;
    }

    @Override
    public SkinItemSource itemSource() {
        return itemSource;
    }
}
