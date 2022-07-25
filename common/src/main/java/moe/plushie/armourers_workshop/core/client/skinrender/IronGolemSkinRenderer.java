package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.world.entity.animal.IronGolem;

@Environment(value = EnvType.CLIENT)
public class IronGolemSkinRenderer<T extends IronGolem, M extends IronGolemModel<T>> extends ExtendedSkinRenderer<T, M> {

    public IronGolemSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return null;
        // TODO: IMP
//        return new IPartAccessor<M>() {
//
//            public ModelPart getHat(M model) {
//                return model.head;
//            }
//
//            public ModelPart getHead(M model) {
//                return model.head;
//            }
//
//            public ModelPart getBody(M model) {
//                return model.body;
//            }
//
//            public ModelPart getLeftArm(M model) {
//                return model.arm0;
//            }
//
//            public ModelPart getRightArm(M model) {
//                return model.arm1;
//            }
//
//            public ModelPart getLeftLeg(M model) {
//                return model.leg0;
//            }
//
//            public ModelPart getRightLeg(M model) {
//                return model.leg1;
//            }
//        };
    }
}
