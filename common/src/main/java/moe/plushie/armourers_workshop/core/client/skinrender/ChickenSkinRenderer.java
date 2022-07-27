package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class ChickenSkinRenderer<T extends LivingEntity, M extends ChickenModel<T>> extends LivingSkinRenderer<T, M> {

    public ChickenSkinRenderer(EntityProfile profile) {
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
            addModelOverride(model.head);
            addModelOverride(model.beak);
            addModelOverride(model.redThing);
        }
    }

    private void offset(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, model.head);
        matrixStack.translate(0.0f, -2.0f, -1.0f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
    }
}
