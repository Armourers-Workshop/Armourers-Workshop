package riskyken.armourersWorkshop.common.inventory;

public enum MannequinSlotType {
    HEAD,
    CHEST,
    LEGS,
    SKIRT,
    FEET,
    RIGHT_HAND,
    LEFT_HAND;

    public static MannequinSlotType getOrdinal(int i) {
        return MannequinSlotType.values()[i];
    }
}
