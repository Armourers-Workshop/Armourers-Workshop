package moe.plushie.armourers_workshop.client.render;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.util.ResourceLocation;

public class SkinPartRenderData extends SkinRenderData {
	
	private final SkinPart skinPart;
	
	public SkinPartRenderData(SkinPart skinPart, float scale, ISkinDye skinDye, IExtraColours extraColours, double distance, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
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
