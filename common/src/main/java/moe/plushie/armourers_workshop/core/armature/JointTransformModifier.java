package moe.plushie.armourers_workshop.core.armature;

//public class JointTransformModifier {
//
//    public static final DataStorageKey<JointTransformModifier> DEFAULT = DataStorageKey.of("default", JointTransformModifier.class, () -> new JointTransformModifier(SkinModifierManager.DEFAULT));
//    public static final DataStorageKey<JointTransformModifier> EPICFIGHT = DataStorageKey.of("epicfight", JointTransformModifier.class, () -> new JointTransformModifier(SkinModifierManager.EPICFIGHT));
//
//    private final ArmatureManager armatureManager;
//    private final HashMap<EntityType<?>, Optional<IJointTransform[]>> transforms = new HashMap<>();
//
//    private int version;
//
//    public JointTransformModifier(ArmatureManager armatureManager) {
//        this.armatureManager = armatureManager;
//    }
//
//    public IJointTransform[] getTransforms(EntityType<?> entityType, IModel model) {
//        // if the entity reenter the world, we need to clear the old data.
//        if (version != armatureManager.getVersion()) {
//            version = armatureManager.getVersion();
//            transforms.clear();
//        }
//        return transforms.computeIfAbsent(entityType, k -> Optional.ofNullable(armatureManager.getTransforms(entityType, model))).orElse(null);
//    }
//
//}
