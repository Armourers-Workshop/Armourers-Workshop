package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimeSkinRenderer<T extends SlimeEntity, M extends SlimeModel<T>> extends LivingSkinRenderer<T, M> {

    public SlimeSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, this::offset);
        mappers.put(SlimeGelLayer.class, ForwardingLayer.when(this::visibleHead));
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_HEAD)) {
            for (ModelRenderer modelRenderer : model.parts()) {
                addModelOverride(modelRenderer);
            }
        }
    }

    private boolean visibleHead(T entity, M model) {
        for (ModelRenderer modelRenderer : model.parts()) {
            return modelRenderer.visible;
        }
        return false;
    }

    private void offset(MatrixStack matrixStack, M model) {
        matrixStack.translate(0.0f, 24.0f, 0.0f);
    }
}
