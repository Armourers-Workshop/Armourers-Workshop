//package moe.plushie.armourers_workshop.core.render.other;
//
//import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
//import moe.plushie.armourers_workshop.core.misc.SkinConfig;
//import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
//import moe.plushie.armourers_workshop.core.utils.ExtraColours;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.MathHelper;
//
//public class SkinRenderData {
//
//    public static final SkinDye BLANK_DYE = new SkinDye();
//
//    private final ISkinDye skinDye;
//    private final IExtraColours extraColours;
//    private final int lod;
//    private final boolean doLodLoading;
//    private final boolean showSkinPaint;
//    private final boolean itemRender;
//    private final ResourceLocation entityTexture;
//
//    public int light = 0;
//    public int overlayLight = 0;
//    public float partialTicks = 0;
//
//    public SkinRenderData(ISkinDye skinDye, IExtraColours extraColours, double distance, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
//        this(skinDye, extraColours, getLOD(distance), doLodLoading, showSkinPaint, itemRender, entityTexture);
//    }
//
//    public SkinRenderData(ISkinDye skinDye, IExtraColours extraColours, int lod, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
//        if (skinDye == null) {
//            this.skinDye = BLANK_DYE;
//        } else {
//            this.skinDye = skinDye;
//        }
//        if (extraColours == null) {
//            this.extraColours = ExtraColours.EMPTY_COLOUR;
//        } else {
//            this.extraColours = extraColours;
//        }
//        this.lod = lod;
//        this.doLodLoading = doLodLoading;
//        this.showSkinPaint = showSkinPaint;
//        this.itemRender = itemRender;
//        this.entityTexture = entityTexture;
//    }
//
//    public ISkinDye getSkinDye() {
//        return skinDye;
//    }
//
//    public IExtraColours getExtraColours() {
//        return extraColours;
//    }
//
//    public int getLod() {
//        return lod;
//    }
//
//    public boolean isDoLodLoading() {
//        return doLodLoading;
//    }
//
//    public boolean isShowSkinPaint() {
//        return showSkinPaint;
//    }
//
//    public boolean isItemRender() {
//        return itemRender;
//    }
//
//    public ResourceLocation getEntityTexture() {
//        return entityTexture;
//    }
//
//    protected static int getLOD(double distance) {
//        int lod = MathHelper.floor(distance / SkinConfig.lodDistance);
//        return MathHelper.clamp(lod, 0, SkinConfig.maxLodLevels);
//    }
//}
