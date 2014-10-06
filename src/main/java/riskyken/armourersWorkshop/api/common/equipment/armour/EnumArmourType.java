package riskyken.armourersWorkshop.api.common.equipment.armour;


public enum EnumArmourType {
    NONE(-1, null),
    HEAD(0, new EnumArmourPart[] { EnumArmourPart.HEAD }),
    CHEST(1, new EnumArmourPart[] { EnumArmourPart.CHEST, EnumArmourPart.LEFT_ARM, EnumArmourPart.RIGHT_ARM }),
    LEGS(2, new EnumArmourPart[] { EnumArmourPart.LEFT_LEG, EnumArmourPart.RIGHT_LEG }),
    SKIRT(2, new EnumArmourPart[] { EnumArmourPart.SKIRT }),
    FEET(3, new EnumArmourPart[] { EnumArmourPart.LEFT_FOOT, EnumArmourPart.RIGHT_FOOT });
    
    private final EnumArmourPart[] parts;
    private final int slotId;
    
    private EnumArmourType(int slotId, EnumArmourPart[] parts) {
        this.slotId = slotId;
        this.parts = parts;
    }
    
    public static EnumArmourType getOrdinal(int id) {
        return EnumArmourType.values()[id];
    }
    
    public int getSlotId() {
        return slotId;
    }
    
    public EnumArmourPart[] getParts() {
        return parts;
    }
}
