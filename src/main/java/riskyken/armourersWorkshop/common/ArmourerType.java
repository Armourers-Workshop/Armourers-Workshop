package riskyken.armourersWorkshop.common;

public enum ArmourerType {
    NONE, HEAD, CHEST, LEGS, FEET;

    public static ArmourerType getOrdinal(int id) {
        if (id >= 0 && id < 5) {
            return ArmourerType.values()[id];
        }
        return NONE;
    }
}
