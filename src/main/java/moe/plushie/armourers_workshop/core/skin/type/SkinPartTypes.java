package moe.plushie.armourers_workshop.core.skin.type;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.advanced.SkinAdvancedPartBase;
import moe.plushie.armourers_workshop.core.skin.type.block.SkinBlockPartBase;
import moe.plushie.armourers_workshop.core.skin.type.block.SkinBlockPartMultiBlock;
import moe.plushie.armourers_workshop.core.skin.type.bow.SkinBowPartArrow;
import moe.plushie.armourers_workshop.core.skin.type.bow.SkinBowPartBase;
import moe.plushie.armourers_workshop.core.skin.type.bow.SkinBowPartFrame1;
import moe.plushie.armourers_workshop.core.skin.type.bow.SkinBowPartFrame2;
import moe.plushie.armourers_workshop.core.skin.type.chest.SkinChestPartBase;
import moe.plushie.armourers_workshop.core.skin.type.chest.SkinChestPartLeftArm;
import moe.plushie.armourers_workshop.core.skin.type.chest.SkinChestPartRightArm;
import moe.plushie.armourers_workshop.core.skin.type.feet.SkinFeetPartLeftFoot;
import moe.plushie.armourers_workshop.core.skin.type.feet.SkinFeetPartRightFoot;
import moe.plushie.armourers_workshop.core.skin.type.head.SkinHeadPartBase;
import moe.plushie.armourers_workshop.core.skin.type.item.SkinItemPartBase;
import moe.plushie.armourers_workshop.core.skin.type.legs.SkinLegsPartLeftLeg;
import moe.plushie.armourers_workshop.core.skin.type.legs.SkinLegsPartRightLeg;
import moe.plushie.armourers_workshop.core.skin.type.legs.SkinLegsPartSkirt;
import moe.plushie.armourers_workshop.core.skin.type.unknown.SkinUnknownPartUnknown;
import moe.plushie.armourers_workshop.core.skin.type.wings.SkinWingsPartLeftWing;
import moe.plushie.armourers_workshop.core.skin.type.wings.SkinWingsPartRightWing;
import moe.plushie.armourers_workshop.core.utils.SkinLog;

import java.util.HashMap;
import java.util.Map;

public final class SkinPartTypes {

    public static final Map<String, ISkinPartType> skinPartTypes = new HashMap<>();

    public static final ISkinPartType UNKNOWN = register("unknown", new SkinUnknownPartUnknown());

    public static final ISkinPartType BIPED_HAT = register("hat.base", new SkinItemPartBase());
    public static final ISkinPartType BIPED_HEAD = register("head.base", new SkinHeadPartBase());
    public static final ISkinPartType BIPED_CHEST = register("chest.base", new SkinChestPartBase());
    public static final ISkinPartType BIPED_LEFT_ARM = register("chest.leftArm", new SkinChestPartLeftArm());
    public static final ISkinPartType BIPED_RIGHT_ARM = register("chest.rightArm", new SkinChestPartRightArm());
    public static final ISkinPartType BIPED_SKIRT = register("legs.skirt", new SkinLegsPartSkirt());
    public static final ISkinPartType BIPED_LEFT_LEG = register("legs.leftLeg", new SkinLegsPartLeftLeg());
    public static final ISkinPartType BIPED_RIGHT_LEG = register("legs.rightLeg", new SkinLegsPartRightLeg());
    public static final ISkinPartType BIPED_LEFT_FOOT = register("feet.leftFoot", new SkinFeetPartLeftFoot());
    public static final ISkinPartType BIPED_RIGHT_FOOT = register("feet.rightFoot", new SkinFeetPartRightFoot());
    public static final ISkinPartType BIPED_LEFT_WING = register("wings.leftWing", new SkinWingsPartLeftWing());
    public static final ISkinPartType BIPED_RIGHT_WING = register("wings.rightWing", new SkinWingsPartRightWing());

    public static final ISkinPartType TOOL_PICKAXE = register("pickaxe.base", new SkinItemPartBase());
    public static final ISkinPartType TOOL_AXE = register("axe.base", new SkinItemPartBase());
    public static final ISkinPartType TOOL_SHOVEL = register("shovel.base", new SkinItemPartBase());
    public static final ISkinPartType TOOL_HOPE = register("hope.base", new SkinItemPartBase());

    public static final ISkinPartType ITEM_BOW1 = register("bow.frame1", new SkinBowPartBase());
    public static final ISkinPartType ITEM_BOW2 = register("bow.frame2", new SkinBowPartFrame1());
    public static final ISkinPartType ITEM_BOW3 = register("bow.frame3", new SkinBowPartFrame2());
    public static final ISkinPartType ITEM_ARROW = register("bow.arrow", new SkinBowPartArrow());
    public static final ISkinPartType ITEM_SWORD = register("sword.base", new SkinItemPartBase());
    public static final ISkinPartType ITEM_SHIELD = register("shield.base", new SkinItemPartBase());

    public static final ISkinPartType ITEM = register("item.base", new SkinItemPartBase());
    public static final ISkinPartType BLOCK = register("block.base", new SkinBlockPartBase());
    public static final ISkinPartType BLOCK_MULTI = register("block.multiblock", new SkinBlockPartMultiBlock());

    public static final ISkinPartType ADVANCED = register("part.advanced_part", new SkinAdvancedPartBase());


    public static ISkinPartType byName(String registryName) {
        if (registryName == null) {
            return UNKNOWN;
        }
        if (registryName.equals("armourers:skirt.base")) {
            return BIPED_SKIRT;
        }
        if (registryName.equals("armourers:bow.base")) {
            return ITEM_BOW1;
        }
        if (registryName.equals("armourers:arrow.base")) {
            return ITEM_ARROW;
        }
        return skinPartTypes.getOrDefault(registryName, UNKNOWN);
    }

    private static ISkinPartType register(String name, AbstractSkinPartType skinPartType) {
        skinPartType.setRegistryName("armourers:" + name);
        if (skinPartTypes.containsKey(skinPartType.getRegistryName())) {
            SkinLog.warn("A mod tried to register a skin type with a registry name that is in use.");
            return skinPartType;
        }
        skinPartTypes.put(skinPartType.getRegistryName(), skinPartType);
        SkinLog.info(String.format("Registering Skin Part '%s'", skinPartType.getRegistryName()));
        return skinPartType;
    }

}

