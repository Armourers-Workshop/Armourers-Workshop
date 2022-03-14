package moe.plushie.armourers_workshop.core.render.renderer;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.capability.Wardrobe;
import moe.plushie.armourers_workshop.core.capability.WardrobeState;
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
    protected void applyOverriders(T entity, M model, Wardrobe wardrobe, WardrobeState snapshot) {
        super.applyOverriders(entity, model, wardrobe, snapshot);
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            addOverrider(model.arms);
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addOverrider(model.arms);
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

