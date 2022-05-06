package moe.plushie.armourers_workshop.core.skin.property;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;

public class SkinProperty<T> implements ISkinProperty<T> {

    // Properties for all skins.
    public static final SkinProperty<String> ALL_CUSTOM_NAME = new SkinProperty<>("customName", "");
    public static final SkinProperty<String> ALL_FLAVOUR_TEXT = new SkinProperty<>("flavour", "");
    public static final SkinProperty<String> ALL_AUTHOR_NAME = new SkinProperty<>("authorName", "");
    public static final SkinProperty<String> ALL_AUTHOR_UUID = new SkinProperty<>("authorUUID", "");

    public static final SkinProperty<String> ALL_KEY_TAGS = new SkinProperty<>("tags", "");

    // Properties.
    @Deprecated
    public static final SkinProperty<Boolean> MODEL_OVERRIDE = new SkinProperty<>("armourOverride", false);
    public static final SkinProperty<Boolean> MODEL_OVERRIDE_HEAD = new SkinProperty<>("overrideModelHead", false);
    public static final SkinProperty<Boolean> MODEL_OVERRIDE_CHEST = new SkinProperty<>("overrideModelChest", false);
    public static final SkinProperty<Boolean> MODEL_OVERRIDE_ARM_LEFT = new SkinProperty<>("overrideModelArmLeft", false);
    public static final SkinProperty<Boolean> MODEL_OVERRIDE_ARM_RIGHT = new SkinProperty<>("overrideModelArmRight", false);
    public static final SkinProperty<Boolean> MODEL_OVERRIDE_LEG_LEFT = new SkinProperty<>("overrideModelLegLeft", false);
    public static final SkinProperty<Boolean> MODEL_OVERRIDE_LEG_RIGHT = new SkinProperty<>("overrideModelLegRight", false);

    @Deprecated
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY = new SkinProperty<>("armourHideOverlay", false);
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_HEAD = new SkinProperty<>("hideOverlayHead", false);
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_CHEST = new SkinProperty<>("hideOverlayChest", false);
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_ARM_LEFT = new SkinProperty<>("hideOverlayArmLeft", false);
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_ARM_RIGHT = new SkinProperty<>("hideOverlayArmRight", false);
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_LEG_LEFT = new SkinProperty<>("hideOverlayLegLeft", false);
    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_LEG_RIGHT = new SkinProperty<>("hideOverlayLegRight", false);

    public static final SkinProperty<Boolean> MODEL_LEGS_LIMIT_LIMBS = new SkinProperty<>("limitLimbs", false);

    public static final SkinProperty<String> OUTFIT_PART_INDEXS = new SkinProperty<>("partIndexs", "");

    public static final SkinProperty<Boolean> BLOCK_GLOWING = new SkinProperty<>("blockGlowing", false);
    public static final SkinProperty<Boolean> BLOCK_LADDER = new SkinProperty<>("blockLadder", false);
    public static final SkinProperty<Boolean> BLOCK_NO_COLLISION = new SkinProperty<>("blockNoCollision", false);
    public static final SkinProperty<Boolean> BLOCK_SEAT = new SkinProperty<>("blockSeat", false);
    public static final SkinProperty<Boolean> BLOCK_MULTIBLOCK = new SkinProperty<>("blockMultiblock", false);
    public static final SkinProperty<Boolean> BLOCK_BED = new SkinProperty<>("blockBed", false);
    public static final SkinProperty<Boolean> BLOCK_INVENTORY = new SkinProperty<>("blockInventory", false);
    public static final SkinProperty<Boolean> BLOCK_ENDER_INVENTORY = new SkinProperty<>("blockEnderInventory", false);
    public static final SkinProperty<Integer> BLOCK_INVENTORY_WIDTH = new SkinProperty<>("blockInventoryWidth", 9);
    public static final SkinProperty<Integer> BLOCK_INVENTORY_HEIGHT = new SkinProperty<>("blockInventoryHeight", 4);

    public static final SkinProperty<Double> WINGS_MAX_ANGLE = new SkinProperty<>("wingsMaxAngle", 75D, true);
    public static final SkinProperty<Double> WINGS_MIN_ANGLE = new SkinProperty<>("wingsMinAngle", 0D, true);
    public static final SkinProperty<Double> WINGS_IDLE_SPEED = new SkinProperty<>("wingsIdleSpeed", 6000D, true);
    public static final SkinProperty<Double> WINGS_FLYING_SPEED = new SkinProperty<>("wingsFlyingSpeed", 350D, true);
    public static final SkinProperty<String> WINGS_MOVMENT_TYPE = new SkinProperty<>("wingsMovmentType", MovementType.EASE.name(), true);

    private final String key;
    private final T defaultValue;
    private final boolean multipleKey;

    public SkinProperty(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.multipleKey = false;
    }
    public SkinProperty(String key, T defaultValue, boolean multipleKey) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.multipleKey = multipleKey;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    public boolean isMultipleKey() {
        return multipleKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinProperty<?> that = (SkinProperty<?>) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    //    public T getValue(ISkinProperties properties) {
//        return (T) properties.getProperty(key, defaultValue);
//    }
//
//    public void setValue(ISkinProperties properties, T value) {
//        properties.setProperty(key, value);
//    }
//
//    public void clearValue(SkinProperties properties) {
//        properties.removeProperty(key);
//    }
//
//    public T getValue(ISkinProperties properties, int index) {
//        if (properties.haveProperty(key + String.valueOf(index))) {
//            return (T) properties.getProperty(key + String.valueOf(index), defaultValue);
//        } else if (properties.haveProperty(key)) {
//            return (T) properties.getProperty(key, defaultValue);
//        } else {
//            return defaultValue;
//        }
//    }
//
//    public void setValue(ISkinProperties properties, T value, int index) {
//        properties.setProperty(key + String.valueOf(index), value);
//    }
//
//    public void clearValue(ISkinProperties properties, int index) {
//        properties.removeProperty(key + String.valueOf(index));
//    }

    public enum MovementType {
        EASE,
        LINEAR
    }
}
