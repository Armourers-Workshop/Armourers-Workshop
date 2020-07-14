package riskyken.armourersWorkshop.client.render;

import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;

public class SkinRenderData {

    public static final SkinDye BLANK_DYE = new SkinDye();

    private final float scale;
    private final ISkinDye skinDye;
    private final byte[] extraColours;
    private final int lod;
    private final boolean doLodLoading;
    private final boolean showSkinPaint;
    private final boolean itemRender;
    private final ResourceLocation entityTexture;

    public SkinRenderData(float scale, ISkinDye skinDye, byte[] extraColours, double distance, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
        this(scale, skinDye, extraColours, getLOD(distance), doLodLoading, showSkinPaint, itemRender, entityTexture);
    }

    public SkinRenderData(float scale, ISkinDye skinDye, byte[] extraColours, int lod, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
        this.scale = scale;
        if (skinDye == null) {
            this.skinDye = BLANK_DYE;
        } else {
            this.skinDye = skinDye;
        }
        if (extraColours == null) {
            this.extraColours = null;
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

    public byte[] getExtraColours() {
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
        int lod = MathHelper.floor_double(distance / ConfigHandlerClient.lodDistance);
        return MathHelper.clamp_int(lod, 0, ConfigHandlerClient.maxLodLevels);
    }
}