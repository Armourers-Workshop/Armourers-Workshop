package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.armature.Armatures;

import java.util.HashSet;

public class BakedFirstPersonArmature extends BakedArmature {

    private static final BakedFirstPersonArmature DEFAULT = new BakedFirstPersonArmature();
    private static final ImmutableMap<AbstractItemTransformType, BakedFirstPersonArmature> VARIANTS = new ImmutableMap.Builder<AbstractItemTransformType, BakedFirstPersonArmature>()
            .put(AbstractItemTransformType.FIRST_PERSON_LEFT_HAND, new BakedFirstPersonArmature("Arm_L", "Hand_L"))
            .put(AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND, new BakedFirstPersonArmature("Arm_R", "Hand_R"))
            .build();

    private final HashSet<String> jointNames;

    public BakedFirstPersonArmature(String... names) {
        super(Armatures.HAND);
        this.jointNames = Sets.newHashSet(names);
    }

    public static BakedFirstPersonArmature defaultBy(AbstractItemTransformType transformType) {
        return VARIANTS.getOrDefault(transformType, DEFAULT);
    }

    @Override
    public IJoint getJoint(ISkinPartType partType) {
        var joint = super.getJoint(partType);
        if (joint != null && !jointNames.isEmpty() && !jointNames.contains(joint.getName())) {
            return null;
        }
        return joint;
    }
}
