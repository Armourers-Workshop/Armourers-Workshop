package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastSkinRenderer<T extends GhastEntity, M extends GhastModel<T>> extends LivingSkinRenderer<T, M> {

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
            for (ModelRenderer modelRenderer : model.parts()) {
                addModelOverride(modelRenderer);
            }
        }
    }

    private void offset(MatrixStack matrixStack, M model) {
        matrixStack.translate(0.0f, 24.0f + 1.5f, 0.0f);
        matrixStack.scale(2f, 2f, 2f);
    }
}
