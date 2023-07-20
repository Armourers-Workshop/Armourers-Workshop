package extensions.net.minecraft.client.model.Model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.Model;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import manifold.ext.rt.api.auto;

@Extension
@Available("[1.18, )")
public class BabyPose {

    public static IModelBabyPose getBabyPose(@This Model model) {
        auto model1 = ObjectUtils.safeCast(model, AgeableListModel.class);
        if (model1 == null) {
            return null;
        }
        float scale = model1.babyHeadScale;
        if (model1.scaleHead) {
            scale = 1.5f;
        }
        return IModelBabyPose.of(scale, new Vector3f(0, model1.babyYHeadOffset, model1.babyZHeadOffset));
    }
}
