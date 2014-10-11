package riskyken.armourersWorkshop.api.common.equipment;



public enum EnumEquipmentType {
    NONE(-1, null),
    HEAD(0, new EnumEquipmentPart[] { EnumEquipmentPart.HEAD }),
    CHEST(1, new EnumEquipmentPart[] { EnumEquipmentPart.CHEST, EnumEquipmentPart.LEFT_ARM, EnumEquipmentPart.RIGHT_ARM }),
    LEGS(2, new EnumEquipmentPart[] { EnumEquipmentPart.LEFT_LEG, EnumEquipmentPart.RIGHT_LEG }),
    SKIRT(2, new EnumEquipmentPart[] { EnumEquipmentPart.SKIRT }),
    FEET(3, new EnumEquipmentPart[] { EnumEquipmentPart.LEFT_FOOT, EnumEquipmentPart.RIGHT_FOOT }),
    WEAPON(-1, new EnumEquipmentPart[] { EnumEquipmentPart.WEAPON });
    
    private final EnumEquipmentPart[] parts;
    private final int slotId;
    
    private EnumEquipmentType(int slotId, EnumEquipmentPart[] parts) {
        this.slotId = slotId;
        this.parts = parts;
    }
    
    public static EnumEquipmentType getOrdinal(int id) {
        return EnumEquipmentType.values()[id];
    }
    
    public int getSlotId() {
        return slotId;
    }
    
    public EnumEquipmentPart[] getParts() {
        return parts;
    }
}
