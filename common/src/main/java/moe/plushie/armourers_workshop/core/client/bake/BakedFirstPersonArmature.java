package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;

import java.util.HashSet;
import java.util.Map;

public class BakedFirstPersonArmature extends BakedArmature {

    private static final BakedFirstPersonArmature DEFAULT = new BakedFirstPersonArmature();
    private static final Map<OpenEquipmentSlot, BakedFirstPersonArmature> VARIANTS = Collections.immutableMap(it -> {
        it.put(OpenEquipmentSlot.OFFHAND, new BakedFirstPersonArmature("Arm_L", "Hand_L"));
        it.put(OpenEquipmentSlot.MAINHAND, new BakedFirstPersonArmature("Arm_R", "Hand_R"));
    });

    private final HashSet<String> jointNames;

    public BakedFirstPersonArmature(String... names) {
        super(Armatures.HAND);
        this.jointNames = Collections.newSet(names);
    }

    public static BakedFirstPersonArmature defaultBy(OpenEquipmentSlot equipmentSlot) {
        return VARIANTS.getOrDefault(equipmentSlot, DEFAULT);
    }

    @Override
    public IJoint jointByType(SkinPartType partType) {
        var joint = super.jointByType(partType);
        if (joint != null && !jointNames.isEmpty() && !jointNames.contains(joint.name())) {
            return null;
        }
        return joint;
    }
}
