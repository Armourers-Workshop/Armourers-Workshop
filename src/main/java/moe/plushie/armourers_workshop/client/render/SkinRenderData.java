package moe.plushie.armourers_workshop.client.render;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SkinRenderData {
	
	public static final SkinDye BLANK_DYE = new SkinDye();
	
	private final SkinPart skinPart;
	private final float scale;
	private final ISkinDye skinDye;
	private final ExtraColours extraColours;
	private final int lod;
	private final boolean doLodLoading;
	private final ResourceLocation entityTexture;
	
	public SkinRenderData(SkinPart skinPart, float scale, ISkinDye skinDye, ExtraColours extraColours, double distance, boolean doLodLoading, ResourceLocation entityTexture) {
        this(skinPart, scale, skinDye, extraColours, getLOD(distance), doLodLoading, entityTexture);
	}
	
	public SkinRenderData(SkinPart skinPart, float scale, ISkinDye skinDye, ExtraColours extraColours, int lod, boolean doLodLoading, ResourceLocation entityTexture) {
		this.skinPart = skinPart;
		this.scale = scale;
		if (skinDye == null) {
			this.skinDye = BLANK_DYE;
		} else {
			this.skinDye = skinDye;
		}
		this.extraColours = extraColours;
		this.lod = lod;
		this.doLodLoading = doLodLoading;
		this.entityTexture = entityTexture;
	}
	
	public SkinPart getSkinPart() {
		return skinPart;
	}

	public float getScale() {
		return scale;
	}

	public ISkinDye getSkinDye() {
		return skinDye;
	}

	public ExtraColours getExtraColours() {
		return extraColours;
	}

	public int getLod() {
		return lod;
	}

	public boolean isDoLodLoading() {
		return doLodLoading;
	}

	public ResourceLocation getEntityTexture() {
		return entityTexture;
	}

	private static int getLOD(double distance) {
		int lod = MathHelper.floor(distance / ConfigHandlerClient.lodDistance);
        return MathHelper.clamp(lod, 0, ConfigHandlerClient.maxLodLevels);
	}
}
