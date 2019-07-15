package riskyken.armourersWorkshop.common.skin.type;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public final class SkinTypeHelper {
    
    public static ISkinType getSkinTypeForSlot(int slotId) {
        switch (slotId) {
        case 0:
            return SkinTypeRegistry.skinHead;
        case 1:
            return SkinTypeRegistry.skinChest;
        case 2:
            return SkinTypeRegistry.skinLegs;
        case 3:
            return SkinTypeRegistry.skinFeet;
        case 4:
            return SkinTypeRegistry.skinSword;
        case 5:
            return SkinTypeRegistry.skinBow;
        case 6:
            return SkinTypeRegistry.oldSkinArrow;
        default:
            return null;
        }
    }
    
    public static int getSlotForSkinType(ISkinType skinType) {
        if (skinType == SkinTypeRegistry.skinHead) {
            return 0;
        } else if (skinType == SkinTypeRegistry.skinChest) {
            return 1;
        } else if (skinType == SkinTypeRegistry.skinLegs) {
            return 2;
        } else if (skinType == SkinTypeRegistry.skinFeet) {
            return 3;
        } else if (skinType == SkinTypeRegistry.skinSword) {
            return 4;
        } else if (skinType == SkinTypeRegistry.skinBow) {
            return 5;
        } else if (skinType == SkinTypeRegistry.oldSkinArrow) {
            return 6;
        }
        return -1;
    }
}
