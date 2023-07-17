package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.init.platform.SkinModifierManager;
import moe.plushie.armourers_workshop.utils.DataStorageKey;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Optional;

public class JointTransformModifier {

    public static final DataStorageKey<JointTransformModifier> DEFAULT = DataStorageKey.of("default", JointTransformModifier.class, () -> new JointTransformModifier(SkinModifierManager.DEFAULT));
    public static final DataStorageKey<JointTransformModifier> EPICFIGHT = DataStorageKey.of("epicfight", JointTransformModifier.class, () -> new JointTransformModifier(SkinModifierManager.EPICFIGHT));

    private final ArmatureManager armatureManager;
    private final HashMap<EntityType<?>, Optional<ITransformf[]>> transforms = new HashMap<>();

    private int version;

    public JointTransformModifier(ArmatureManager armatureManager) {
        this.armatureManager = armatureManager;
    }

    public ITransformf[] getTransforms(EntityType<?> entityType, IModel model) {
        // if the entity reenter the world, we need to clear the old data.
        if (version != armatureManager.getVersion()) {
            version = armatureManager.getVersion();
            transforms.clear();
        }
        return transforms.computeIfAbsent(entityType, k -> Optional.ofNullable(armatureManager.getTransforms(entityType, model))).orElse(null);
    }

}
