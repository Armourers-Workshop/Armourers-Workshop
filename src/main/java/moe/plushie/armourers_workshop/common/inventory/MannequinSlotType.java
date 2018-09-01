package moe.plushie.armourers_workshop.common.inventory;

public enum MannequinSlotType {
    HEAD,
    CHEST,
    LEGS,
    FEET,
    RIGHT_HAND,
    LEFT_HAND,
    WINGS;

    public static MannequinSlotType getOrdinal(int i) {
        return MannequinSlotType.values()[i];
    }
}
