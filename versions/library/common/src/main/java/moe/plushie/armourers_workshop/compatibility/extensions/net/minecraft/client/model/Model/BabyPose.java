package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.model.Model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.Model;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.18, )")
public class BabyPose {

    public static IModelBabyPose getBabyPose(@This Model model) {
        if (!(model instanceof AgeableListModel<?> model1)) {
            return null;
        }
        float scale = model1.babyHeadScale;
        if (model1.scaleHead) {
            scale = 1.5f;
        }
        return IModelBabyPose.of(scale, new Vector3f(0, model1.babyYHeadOffset, model1.babyZHeadOffset));
    }
}
