package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllagerSkinRenderer<T extends AbstractIllagerEntity, M extends IllagerModel<T>> extends ExtendedSkinRenderer<T, M> {

    public IllagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.arms);
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.arms);
        }
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelRenderer getHat(M model) {
                return model.hat;
            }

            public ModelRenderer getHead(M model) {
                return model.head;
            }

            public ModelRenderer getBody(M model) {
                return model.body;
            }

            public ModelRenderer getLeftArm(M model) {
                return model.leftArm;
            }

            public ModelRenderer getRightArm(M model) {
                return model.rightArm;
            }

            public ModelRenderer getLeftLeg(M model) {
                return model.leftLeg;
            }

            public ModelRenderer getRightLeg(M model) {
                return model.rightLeg;
            }
        };
    }
}

