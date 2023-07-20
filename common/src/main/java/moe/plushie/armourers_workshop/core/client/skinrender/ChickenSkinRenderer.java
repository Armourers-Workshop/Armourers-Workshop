package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinModelTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ChickenSkinRenderer<T extends LivingEntity, M extends IModel> extends LivingSkinRenderer<T, M> {

    public ChickenSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinModelTransformer<T, M> transformer) {
        transformer.registerArmor(SkinPartTypes.BIPPED_HEAD, this::offset);
    }

    @Override
    protected void init(SkinVisibilityTransformer<M> transformer) {
        transformer.modelToPart(SkinPartTypes.BIPPED_HEAD, "head");
        transformer.modelToPart(SkinPartTypes.BIPPED_HEAD, "beak");
        transformer.modelToPart(SkinPartTypes.BIPPED_HEAD, "red_thing");
    }

    private void offset(IPoseStack poseStack, M model) {
        transformer.apply(poseStack, model.getPart("head"));
        poseStack.translate(0.0f, -2.0f, -1.0f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
    }
}
