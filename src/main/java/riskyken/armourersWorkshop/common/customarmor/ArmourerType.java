package riskyken.armourersWorkshop.common.customarmor;

public enum ArmourerType {
    HEAD, CHEST, LEGS, FEET;

    public static ArmourerType getOrdinal(int id) {
        return ArmourerType.values()[id];
    }
}
