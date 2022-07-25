package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.AbstractIllager;

@Environment(value = EnvType.CLIENT)
public class IllagerSkinRenderer<T extends AbstractIllager, M extends IllagerModel<T>> extends ExtendedSkinRenderer<T, M> {

    public IllagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.arms);
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.arms);
        }
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelPart getHat(M model) {
                return model.hat;
            }

            public ModelPart getHead(M model) {
                return model.head;
            }

            public ModelPart getBody(M model) {
                return model.body;
            }

            public ModelPart getLeftArm(M model) {
                return model.leftArm;
            }

            public ModelPart getRightArm(M model) {
                return model.rightArm;
            }

            public ModelPart getLeftLeg(M model) {
                return model.leftLeg;
            }

            public ModelPart getRightLeg(M model) {
                return model.rightLeg;
            }
        };
    }
}

