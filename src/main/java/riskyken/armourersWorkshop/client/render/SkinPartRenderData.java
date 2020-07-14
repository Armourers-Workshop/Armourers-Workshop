package riskyken.armourersWorkshop.client.render;

import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;

public class SkinPartRenderData extends SkinRenderData {

    private final SkinPart skinPart;

    public SkinPartRenderData(SkinPart skinPart, float scale, ISkinDye skinDye, byte[] extraColours, double distance, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
        super(scale, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, entityTexture);
        this.skinPart = skinPart;
    }

    public SkinPartRenderData(SkinPart skinPart, SkinRenderData renderData) {
        super(renderData.getScale(), renderData.getSkinDye(), renderData.getExtraColours(), renderData.getLod(), renderData.isDoLodLoading(), renderData.isShowSkinPaint(), renderData.isItemRender(), renderData.getEntityTexture());
        this.skinPart = skinPart;
    }

    public SkinPart getSkinPart() {
        return skinPart;
    }
}