package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointFilter;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

import java.util.HashMap;

public class BakedArmature {

    private static final HashMap<Armature, BakedArmature> DEFAULT_IMMUTABLE_ARMATURES = new HashMap<>();
    private static final HashMap<Armature, BakedArmature> DEFAULT_MUTABLE_ARMATURES = new HashMap<>();

    private final Armature armature;
    private final IJointTransform[] defaultTransforms;

    private IJointFilter filter;
    private IJointTransform[] finalTransforms;

    public BakedArmature(Armature armature) {
        this.armature = armature;
        this.defaultTransforms = new IJointTransform[armature.size()];
        this.finalTransforms = defaultTransforms;
        // initialized to default joint transform.
        for (int i = 0; i < defaultTransforms.length; ++i) {
            this.defaultTransforms[i] = armature.getGlobalTransform(i);
        }
    }

    public static BakedArmature defaultBy(Armature armature) {
        return DEFAULT_IMMUTABLE_ARMATURES.computeIfAbsent(armature, BakedArmature::new);
    }

    public static BakedArmature defaultBy(ISkinType skinType) {
        return defaultBy(Armatures.byType(skinType));
    }

    public static BakedArmature mutableBy(Armature armature) {
        return DEFAULT_MUTABLE_ARMATURES.computeIfAbsent(armature, BakedArmature::new);
    }

    public static BakedArmature mutableBy(ISkinType skinType) {
        return mutableBy(Armatures.byType(skinType));
    }

    public void setFilter(IJointFilter filter) {
        this.filter = filter;
    }

    public IJointFilter getFilter() {
        return filter;
    }

    public void seTransforms(IJointTransform[] transforms) {
        if (transforms != null) {
            this.finalTransforms = transforms;
        } else {
            this.finalTransforms = defaultTransforms;
        }
    }

    public IJointTransform[] getTransforms() {
        return finalTransforms;
    }

    public IJointTransform getTransform(ISkinPartType partType) {
        IJoint joint = getJoint(partType);
        if (joint != null) {
            return finalTransforms[joint.getId()];
        }
        return null;
    }

    public IJoint getJoint(ISkinPartType partType) {
        IJoint joint = armature.getJoint(partType);
        if (joint != null && filter != null && !filter.test(joint)) {
            return null;
        }
        return joint;
    }

    public Armature getArmature() {
        return armature;
    }

    public IJointTransform getTransform(BakedSkinPart bakedPart) {
        ISkinPartType partType = bakedPart.getType();
        if (partType == SkinPartTypes.BIPPED_LEFT_WING) {
            if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
                return getTransform(SkinPartTypes.BIPPED_LEFT_PHALANX);
            }
        }
        if (partType == SkinPartTypes.BIPPED_RIGHT_WING) {
            if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
                return getTransform(SkinPartTypes.BIPPED_RIGHT_PHALANX);
            }
        }
        return getTransform(partType);
    }
}
