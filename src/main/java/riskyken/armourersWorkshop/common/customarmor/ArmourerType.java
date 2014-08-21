package riskyken.armourersWorkshop.common.customarmor;

public enum ArmourerType {
    HEAD, CHEST, LEGS;

    public static ArmourerType getOrdinal(int id) {
        return ArmourerType.values()[id];
    }
}
