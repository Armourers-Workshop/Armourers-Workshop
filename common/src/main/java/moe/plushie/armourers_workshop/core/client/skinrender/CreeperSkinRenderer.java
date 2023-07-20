package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.armature.Joints;
import moe.plushie.armourers_workshop.core.client.other.SkinModelTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class CreeperSkinRenderer<T extends LivingEntity, M extends IModel> extends LivingSkinRenderer<T, M> {

    public CreeperSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinModelTransformer<T, M> transformer) {
        transformer.registerArmor(SkinPartTypes.BIPPED_HEAD, Joints.BIPPED_HEAD);
    }

    @Override
    protected void init(SkinVisibilityTransformer<M> transformer) {
        transformer.modelToPart(SkinPartTypes.BIPPED_HEAD, "head");
        transformer.modelToPart(SkinPartTypes.BIPPED_HEAD, "hair");
    }
}
