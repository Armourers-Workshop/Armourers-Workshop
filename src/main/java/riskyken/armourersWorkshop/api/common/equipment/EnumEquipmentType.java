package riskyken.armourersWorkshop.api.common.equipment;



public enum EnumEquipmentType {
    NONE(-1, null),
    HEAD(0, new EnumEquipmentPart[] { EnumEquipmentPart.HEAD }),
    CHEST(1, new EnumEquipmentPart[] { EnumEquipmentPart.CHEST, EnumEquipmentPart.LEFT_ARM, EnumEquipmentPart.RIGHT_ARM }),
    LEGS(2, new EnumEquipmentPart[] { EnumEquipmentPart.LEFT_LEG, EnumEquipmentPart.RIGHT_LEG }),
    SKIRT(2, new EnumEquipmentPart[] { EnumEquipmentPart.SKIRT }),
    FEET(3, new EnumEquipmentPart[] { EnumEquipmentPart.LEFT_FOOT, EnumEquipmentPart.RIGHT_FOOT }),
    SWORD(-1, new EnumEquipmentPart[] { EnumEquipmentPart.WEAPON }),
    BOW(-1, new EnumEquipmentPart[] { EnumEquipmentPart.BOW });
    
    private final EnumEquipmentPart[] parts;
    private final int vanillaSlotId;
    
    private EnumEquipmentType(int vanillaSlotId, EnumEquipmentPart[] parts) {
        this.vanillaSlotId = vanillaSlotId;
        this.parts = parts;
    }
    
    public static EnumEquipmentType getOrdinal(int id) {
        if (id >= 0 & id < values().length) {
            return EnumEquipmentType.values()[id];
        }
        return NONE;
    }
    
    public int getVanillaSlotId() {
        return vanillaSlotId;
    }
    
    public int getArmourersSlotId() {
        return this.ordinal() + 1;
    }
    
    public EnumEquipmentPart[] getParts() {
        return parts;
    }
}
