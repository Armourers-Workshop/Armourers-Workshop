package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.world.entity.monster.Slime;

@Environment(value = EnvType.CLIENT)
public class SlimeSkinRenderer<T extends Slime, M extends SlimeModel<T>> extends LivingSkinRenderer<T, M> {

    public SlimeSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, this::offset);
        mappers.put(SlimeOuterLayer.class, ForwardingLayer.when(this::visibleHead));
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

    private boolean visibleHead(T entity, M model) {
        for (ModelPart modelRenderer : model.parts()) {
            return modelRenderer.visible;
        }
        return false;
    }

    private void offset(PoseStack matrixStack, M model) {
        matrixStack.translate(0.0f, 24.0f, 0.0f);
    }
}