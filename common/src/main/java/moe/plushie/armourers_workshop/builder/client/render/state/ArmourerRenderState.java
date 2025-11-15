package moe.plushie.armourers_workshop.builder.client.render.state;

import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.HashMap;

public class ArmourerRenderState extends BlockEntityRenderState {

    protected OpenDirection facing;

    protected SkinType skinType;
    protected SkinProperties skinProperties;
    protected EntityTextureDescriptor.Model textureModel;
    protected boolean isShowGuides;
    protected boolean isShowModelGuides;
    protected boolean isShowHelper;
    protected boolean isUseHelper;

    protected EntityTextureDescriptor textureDescriptor;
    protected SkinPaintData paintData;

    private Object customTextureProvider;

    protected final HashMap<SkinPartType, Boolean> overrides = new HashMap<>();

    public OpenDirection facing() {
        return facing;
    }

    public SkinType skinType() {
        return skinType;
    }

    public SkinProperties skinProperties() {
        return skinProperties;
    }

    public EntityTextureDescriptor.Model textureModel() {
        return textureModel;
    }

    public boolean isShowGuides() {
        return isShowGuides;
    }

    public boolean isShowModelGuides() {
        return isShowModelGuides;
    }

    public boolean isShowHelper() {
        return isShowHelper;
    }

    public boolean isUseHelper() {
        return isUseHelper;
    }

    public boolean isModelOverridden(SkinPartType partType) {
        return overrides.getOrDefault(partType, false);
    }

    public EntityTextureDescriptor textureDescriptor() {
        return textureDescriptor;
    }

    public SkinPaintData paintData() {
        return paintData;
    }

    public void setCustomTextureProvider(Object customTextureProvider) {
        this.customTextureProvider = customTextureProvider;
    }

    public Object customTextureProvider() {
        return customTextureProvider;
    }

    public static void extract(ArmourerBlockEntity entity, ArmourerRenderState renderState) {
        renderState.facing = entity.facing();

        renderState.skinType = entity.skinType();
        renderState.skinProperties = entity.skinProperties();
        renderState.textureModel = entity.textureModel();

        renderState.isShowGuides = entity.isShowGuides();
        renderState.isShowModelGuides = entity.isShowModelGuides();
        renderState.isShowHelper = entity.isShowHelper();
        renderState.isUseHelper = entity.isUseHelper();

        renderState.textureDescriptor = entity.textureDescriptor();
        renderState.paintData = entity.paintData();

        renderState.overrides.clear();
        for (var partType : renderState.skinType.parts()) {
            renderState.overrides.put(partType, entity.isModelOverridden(partType));
        }
    }
}
