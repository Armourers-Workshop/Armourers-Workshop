package moe.plushie.armourers_workshop.client.render;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SkinRenderData {

    public static final SkinDye BLANK_DYE = new SkinDye();

    private final float scale;
    private final ISkinDye skinDye;
    private final IExtraColours extraColours;
    private final int lod;
    private final boolean doLodLoading;
    private final boolean showSkinPaint;
    private final boolean itemRender;
    private final ResourceLocation entityTexture;

    public SkinRenderData(float scale, ISkinDye skinDye, IExtraColours extraColours, double distance, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
        this(scale, skinDye, extraColours, getLOD(distance), doLodLoading, showSkinPaint, itemRender, entityTexture);
    }

    public SkinRenderData(float scale, ISkinDye skinDye, IExtraColours extraColours, int lod, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
        this.scale = scale;
        if (skinDye == null) {
            this.skinDye = BLANK_DYE;
        } else {
            this.skinDye = skinDye;
        }
        if (extraColours == null) {
            this.extraColours = ExtraColours.EMPTY_COLOUR;
        } else {
            this.extraColours = extraColours;
        }
        this.lod = lod;
        this.doLodLoading = doLodLoading;
        this.showSkinPaint = showSkinPaint;
        this.itemRender = itemRender;
        this.entityTexture = entityTexture;
    }

    public float getScale() {
        return scale;
    }

    public ISkinDye getSkinDye() {
        return skinDye;
    }

    public IExtraColours getExtraColours() {
        return extraColours;
    }

    public int getLod() {
        return lod;
    }

    public boolean isDoLodLoading() {
        return doLodLoading;
    }
    
    public boolean isShowSkinPaint() {
        return showSkinPaint;
    }
    
    public boolean isItemRender() {
        return itemRender;
    }

    public ResourceLocation getEntityTexture() {
        return entityTexture;
    }

    protected static int getLOD(double distance) {
        int lod = MathHelper.floor(distance / ConfigHandlerClient.lodDistance);
        return MathHelper.clamp(lod, 0, ConfigHandlerClient.maxLodLevels);
    }
}
