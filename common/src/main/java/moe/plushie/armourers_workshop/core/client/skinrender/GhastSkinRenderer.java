package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Ghast;

@Environment(value = EnvType.CLIENT)
public class GhastSkinRenderer<T extends Ghast, M extends GhastModel<T>> extends LivingSkinRenderer<T, M> {

    public GhastSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, this::offset);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_HEAD)) {
            for (ModelPart modelRenderer : model.parts()) {
                addModelOverride(modelRenderer);
            }
        }
    }

    private void offset(PoseStack matrixStack, M model) {
        matrixStack.translate(0.0f, 24.0f + 1.5f, 0.0f);
        matrixStack.scale(2f, 2f, 2f);
    }
}
