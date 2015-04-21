package riskyken.armourersWorkshop.common.equipment.skin;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;

public final class SkinTypeHelper {
    
    public static IEquipmentSkinType getSkinTypeForSlot(int slotId) {
        switch (slotId) {
        case 0:
            return SkinTypeRegistry.skinHead;
        case 1:
            return SkinTypeRegistry.skinChest;
        case 2:
            return SkinTypeRegistry.skinLegs;
        case 3:
            return SkinTypeRegistry.skinSkirt;
        case 4:
            return SkinTypeRegistry.skinFeet;
        case 5:
            return SkinTypeRegistry.skinSword;
        case 6:
            return SkinTypeRegistry.skinBow;
        default:
            return null;
        }
    }
    
    public static int getSlotForSkinType(IEquipmentSkinType skinType) {
        if (skinType == SkinTypeRegistry.skinHead) {
            return 0;
        } else if (skinType == SkinTypeRegistry.skinChest) {
            return 1;
        } else if (skinType == SkinTypeRegistry.skinLegs) {
            return 2;
        } else if (skinType == SkinTypeRegistry.skinSkirt) {
            return 3;
        } else if (skinType == SkinTypeRegistry.skinFeet) {
            return 4;
        } else if (skinType == SkinTypeRegistry.skinSword) {
            return 5;
        } else if (skinType == SkinTypeRegistry.skinBow) {
            return 6;
        }
        return -1;
    }
}
