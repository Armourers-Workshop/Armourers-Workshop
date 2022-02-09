//package moe.plushie.armourers_workshop.core.render.other;
//
//import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
//import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
//import net.minecraft.util.ResourceLocation;
//
//public class SkinPartRenderData extends SkinRenderData {
//
//	private final SkinPart skinPart;
//
//	public SkinPartRenderData(SkinPart skinPart, ISkinDye skinDye, IExtraColours extraColours, double distance, boolean doLodLoading, boolean showSkinPaint, boolean itemRender, ResourceLocation entityTexture) {
//	    super(skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, entityTexture);
//	    this.skinPart = skinPart;
//	}
//
//	public SkinPartRenderData(SkinPart skinPart, SkinRenderData renderData) {
//		super(renderData.getSkinDye(), renderData.getExtraColours(), renderData.getLod(), renderData.isDoLodLoading(), renderData.isShowSkinPaint(), renderData.isItemRender(), renderData.getEntityTexture());
//	    this.skinPart = skinPart;
//
//	    this.light = renderData.light;
//	    this.overlayLight = renderData.overlayLight;
//	    this.partialTicks = renderData.partialTicks;
//	}
//
//	public SkinPart getSkinPart() {
//		return skinPart;
//	}
//}
