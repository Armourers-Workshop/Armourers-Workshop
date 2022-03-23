package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolemSkinRenderer<T extends IronGolemEntity, M extends IronGolemModel<T>> extends ExtendedSkinRenderer<T, M> {

    public IronGolemSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelRenderer getHat(M model) {
                return model.head;
            }

            public ModelRenderer getHead(M model) {
                return model.head;
            }

            public ModelRenderer getBody(M model) {
                return model.body;
            }

            public ModelRenderer getLeftArm(M model) {
                return model.arm0;
            }

            public ModelRenderer getRightArm(M model) {
                return model.arm1;
            }

            public ModelRenderer getLeftLeg(M model) {
                return model.leg0;
            }

            public ModelRenderer getRightLeg(M model) {
                return model.leg1;
            }
        };
    }
}
