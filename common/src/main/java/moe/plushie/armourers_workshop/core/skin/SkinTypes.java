package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.slot.ItemOverrideType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SkinResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "SameParameterValue"})
public final class SkinTypes {

    private static final ArrayList<ISkinType> ALL_SORTED_TYPES = new ArrayList<>();
    private static final HashMap<String, ISkinType> ALL_TYPES = new HashMap<>();

    public static final ISkinType UNKNOWN = register("unknown", 255, SkinPartTypes.UNKNOWN);

    public static final ISkinType ARMOR_HEAD = registerArmor("head", 1, EquipmentSlot.HEAD, SkinPartTypes.BIPPED_HEAD);
    public static final ISkinType ARMOR_CHEST = registerArmor("chest", 2, EquipmentSlot.CHEST, SkinPartTypes.BIPPED_CHEST, SkinPartTypes.BIPPED_LEFT_ARM, SkinPartTypes.BIPPED_RIGHT_ARM);
    public static final ISkinType ARMOR_LEGS = registerArmor("legs", 3, EquipmentSlot.LEGS, SkinPartTypes.BIPPED_LEFT_THIGH, SkinPartTypes.BIPPED_RIGHT_THIGH, SkinPartTypes.BIPPED_SKIRT);
    public static final ISkinType ARMOR_FEET = registerArmor("feet", 4, EquipmentSlot.FEET, SkinPartTypes.BIPPED_LEFT_FOOT, SkinPartTypes.BIPPED_RIGHT_FOOT);
    public static final ISkinType ARMOR_WINGS = registerArmor("wings", 5, null, SkinPartTypes.BIPPED_LEFT_WING, SkinPartTypes.BIPPED_RIGHT_WING);

    public static final ISkinType OUTFIT = registerArmor("outfit", 6, null, SkinTypes.ARMOR_HEAD, SkinTypes.ARMOR_CHEST, SkinTypes.ARMOR_LEGS, SkinTypes.ARMOR_FEET, SkinTypes.ARMOR_WINGS);

    public static final ISkinType ITEM_SWORD = registerItem("sword", 7, ItemOverrideType.SWORD, SkinPartTypes.ITEM_SWORD);
    public static final ISkinType ITEM_SHIELD = registerItem("shield", 8, ItemOverrideType.SHIELD, SkinPartTypes.ITEM_SHIELD);
    public static final ISkinType ITEM_BOW = registerItem("bow", 9, ItemOverrideType.BOW, SkinPartTypes.ITEM_BOW0, SkinPartTypes.ITEM_BOW1, SkinPartTypes.ITEM_BOW2, SkinPartTypes.ITEM_BOW3, SkinPartTypes.ITEM_ARROW);
    public static final ISkinType ITEM_TRIDENT = registerItem("trident", 17, ItemOverrideType.TRIDENT, SkinPartTypes.ITEM_TRIDENT);

    public static final ISkinType ITEM_PICKAXE = registerItem("pickaxe", 10, ItemOverrideType.PICKAXE, SkinPartTypes.ITEM_PICKAXE);
    public static final ISkinType ITEM_AXE = registerItem("axe", 11, ItemOverrideType.AXE, SkinPartTypes.ITEM_AXE);
    public static final ISkinType ITEM_SHOVEL = registerItem("shovel", 12, ItemOverrideType.SHOVEL, SkinPartTypes.ITEM_SHOVEL);
    public static final ISkinType ITEM_HOE = registerItem("hoe", 13, ItemOverrideType.HOE, SkinPartTypes.ITEM_HOE);

    public static final ISkinType ITEM_BOAT = registerItem("boat", 19, ItemOverrideType.FISHING_ROD, SkinPartTypes.ITEM_BOAT_BODY, SkinPartTypes.ITEM_BOAT_LEFT_PADDLE, SkinPartTypes.ITEM_BOAT_RIGHT_PADDLE);
    public static final ISkinType ITEM_FISHING = registerItem("fishing", 20, ItemOverrideType.FISHING_ROD, SkinPartTypes.ITEM_FISHING_ROD, SkinPartTypes.ITEM_FISHING_HOOK);

    public static final ISkinType ITEM = register("item", 14, SkinPartTypes.ITEM);
    public static final ISkinType BLOCK = register("block", 15, SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType HORSE = register("horse", 18, SkinPartTypes.HORSE_HEAD, SkinPartTypes.HORSE_NECK, SkinPartTypes.HORSE_CHEST, SkinPartTypes.HORSE_RIGHT_FRONT_THIGH, SkinPartTypes.HORSE_LEFT_FRONT_THIGH, SkinPartTypes.HORSE_RIGHT_HIND_THIGH, SkinPartTypes.HORSE_LEFT_HIND_THIGH, SkinPartTypes.HORSE_TAIL);

    public static final ISkinType ADVANCED = register("part", 16, SkinPartTypes.ADVANCED);

    public static ISkinType byName(String registryName) {
        if (registryName == null) {
            return UNKNOWN;
        }
        if (!registryName.startsWith("armourers:")) {
            registryName = "armourers:" + registryName;
        }
        if (registryName.equals("armourers:skirt")) {
            return ARMOR_LEGS;
        }
        if (registryName.equals("armourers:arrow")) {
            return ITEM_BOW;
        }
        return ALL_TYPES.getOrDefault(registryName, UNKNOWN);
    }

    public static ArrayList<ISkinType> values() {
        return ALL_SORTED_TYPES;
    }

    private static ISkinType register(String name, int id, ISkinPartType... parts) {
        return register(name, new SkinType(name, id, ObjectUtils.map(parts)));
    }

    private static ISkinType registerArmor(String name, int id, EquipmentSlot slotType, ISkinPartType... parts) {
        return register(name, new SkinType.Armor(name, id, slotType, ObjectUtils.map(parts)));
    }

    private static ISkinType registerArmor(String name, int id, EquipmentSlot slotType, ISkinType... types) {
        List<ISkinPartType> partTypes = new ArrayList<>();
        for (ISkinType type : types) {
            partTypes.addAll(type.getParts());
        }
        return register(name, new SkinType.Armor(name, id, slotType, partTypes));
    }

    private static ISkinType registerItem(String name, int id, ItemOverrideType overrideType, ISkinPartType... parts) {
        return register(name, new SkinType.Tool(name, id, ObjectUtils.map(parts), overrideType::isOverrideItem));
    }

    private static ISkinType register(String name, SkinType type) {
        type.setRegistryName(new SkinResourceLocation("armourers", name));
        if (type.getParts().size() == 0) {
            ModLog.warn("A mod tried to register a skin type no skin type parts.");
            return type;
        }
        if (ALL_TYPES.containsKey(type.getRegistryName().toString())) {
            ModLog.warn("A mod tried to register a skin type with a registry name that is in use.");
            return type;
        }
        ALL_SORTED_TYPES.add(type);
        ALL_TYPES.put(type.getRegistryName().toString(), type);
        ModLog.debug("Registering Skin '{}'", type.getRegistryName());
        return type;
    }
}
