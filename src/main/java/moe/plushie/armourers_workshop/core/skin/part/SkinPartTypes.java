package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.advanced.SkinAdvancedPartBase;
import moe.plushie.armourers_workshop.core.skin.part.block.SkinBlockPartBase;
import moe.plushie.armourers_workshop.core.skin.part.block.SkinBlockPartMultiBlock;
import moe.plushie.armourers_workshop.core.skin.part.bow.SkinBowPartArrow;
import moe.plushie.armourers_workshop.core.skin.part.bow.SkinBowPartBase;
import moe.plushie.armourers_workshop.core.skin.part.bow.SkinBowPartFrame1;
import moe.plushie.armourers_workshop.core.skin.part.bow.SkinBowPartFrame2;
import moe.plushie.armourers_workshop.core.skin.part.chest.SkinChestPartBase;
import moe.plushie.armourers_workshop.core.skin.part.chest.SkinChestPartLeftArm;
import moe.plushie.armourers_workshop.core.skin.part.chest.SkinChestPartRightArm;
import moe.plushie.armourers_workshop.core.skin.part.feet.SkinFeetPartLeftFoot;
import moe.plushie.armourers_workshop.core.skin.part.feet.SkinFeetPartRightFoot;
import moe.plushie.armourers_workshop.core.skin.part.head.SkinHeadPartBase;
import moe.plushie.armourers_workshop.core.skin.part.item.SkinItemPartBase;
import moe.plushie.armourers_workshop.core.skin.part.legs.SkinLegsPartLeftLeg;
import moe.plushie.armourers_workshop.core.skin.part.legs.SkinLegsPartRightLeg;
import moe.plushie.armourers_workshop.core.skin.part.legs.SkinLegsPartSkirt;
import moe.plushie.armourers_workshop.core.skin.part.unknown.SkinUnknownPartUnknown;
import moe.plushie.armourers_workshop.core.skin.part.wings.SkinWingsPartLeftWing;
import moe.plushie.armourers_workshop.core.skin.part.wings.SkinWingsPartRightWing;
import moe.plushie.armourers_workshop.core.utils.SkinLog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


// offset:
//  1 left/right legs
//  2 left/right feet
//  3 skirt
//  4 left/right arm
//  5 chest
//  8 head
//  7 left/right wing
//  9 item

@SuppressWarnings("unused")
public final class SkinPartTypes {

    private static final Map<String, ISkinPartType> ALL_PART_TYPES = new HashMap<>();

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
        return ALL_PART_TYPES.getOrDefault(registryName, UNKNOWN);
    }

    public static Set<String> registeredNames() {
        return ALL_PART_TYPES.keySet();
    }

    public static Collection<ISkinPartType> registeredTypes() {
        return ALL_PART_TYPES.values();
    }

    private static ISkinPartType register(String name, SkinPartType partType) {
        partType.setRegistryName("armourers:" + name);
        if (partType instanceof SkinUnknownPartUnknown) {
            return partType;
        }
        if (ALL_PART_TYPES.containsKey(partType.getRegistryName())) {
            SkinLog.warn("A mod tried to register a skin type with a registry name that is in use.");
            return partType;
        }
        ALL_PART_TYPES.put(partType.getRegistryName(), partType);
        SkinLog.info("Registering Skin Part '{}'", partType.getRegistryName());
        return partType;
    }

}

