package riskyken.armourersWorkshop.common.customarmor;

public enum ArmourerType {
    NONE(-1), HEAD(0), CHEST(1), LEGS(2), SKIRT(2), FEET(3);

    private final int slotId;
    
    private ArmourerType(int slotId) {
        this.slotId = slotId;
    }
    
    public static ArmourerType getOrdinal(int id) {
        return ArmourerType.values()[id];
    }
    
    public int getSlotId() {
        return slotId;
    }
}
