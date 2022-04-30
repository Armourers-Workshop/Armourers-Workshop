package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.init.common.ModTags;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.SkinResourceLocation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "SameParameterValue"})
public final class SkinTypes {

    private static final ArrayList<ISkinType> ALL_SORTED_TYPES = new ArrayList<>();
    private static final HashMap<String, ISkinType> ALL_TYPES = new HashMap<>();

    public static final ISkinType UNKNOWN = register("unknown", 255, SkinPartTypes.UNKNOWN);

    public static final ISkinType ARMOR_HEAD = registerArmor("head", 1, EquipmentSlotType.HEAD, SkinPartTypes.BIPED_HEAD);
    public static final ISkinType ARMOR_CHEST = registerArmor("chest", 2, EquipmentSlotType.CHEST, SkinPartTypes.BIPED_CHEST, SkinPartTypes.BIPED_LEFT_ARM, SkinPartTypes.BIPED_RIGHT_ARM);
    public static final ISkinType ARMOR_LEGS = registerArmor("legs", 3, EquipmentSlotType.LEGS, SkinPartTypes.BIPED_LEFT_LEG, SkinPartTypes.BIPED_RIGHT_LEG, SkinPartTypes.BIPED_SKIRT);
    public static final ISkinType ARMOR_FEET = registerArmor("feet", 4, EquipmentSlotType.FEET, SkinPartTypes.BIPED_LEFT_FOOT, SkinPartTypes.BIPED_RIGHT_FOOT);
    public static final ISkinType ARMOR_WINGS = registerArmor("wings", 5, null, SkinPartTypes.BIPED_LEFT_WING, SkinPartTypes.BIPED_RIGHT_WING);

    //public static final ISkinType HORSE = register("horse", 17, SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType ITEM_SWORD = registerItem("sword", 7, ModTags.SWORDS, SkinPartTypes.ITEM_SWORD);
    public static final ISkinType ITEM_SHIELD = registerItem("shield", 8, ModTags.SHIELDS, SkinPartTypes.ITEM_SHIELD);
    public static final ISkinType ITEM_BOW = registerItem("bow", 9, ModTags.BOWS, SkinPartTypes.ITEM_BOW1, SkinPartTypes.ITEM_BOW2, SkinPartTypes.ITEM_BOW3, SkinPartTypes.ITEM_ARROW);

    public static final ISkinType TOOL_PICKAXE = registerItem("pickaxe", 10, ModTags.PICKAXES, SkinPartTypes.TOOL_PICKAXE);
    public static final ISkinType TOOL_AXE = registerItem("axe", 11, ModTags.AXES, SkinPartTypes.TOOL_AXE);
    public static final ISkinType TOOL_SHOVEL = registerItem("shovel", 12, ModTags.SHOVELS, SkinPartTypes.TOOL_SHOVEL);
    public static final ISkinType TOOL_HOE = registerItem("hoe", 13, ModTags.HOES, SkinPartTypes.TOOL_HOE);

    public static final ISkinType ITEM = register("item", 14, SkinPartTypes.ITEM);
    public static final ISkinType BLOCK = register("block", 15, SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType ADVANCED = register("part", 16, SkinPartTypes.ADVANCED);

    public static final ISkinType OUTFIT = registerArmor("outfit", 6, null, SkinTypes.ARMOR_HEAD, SkinTypes.ARMOR_CHEST, SkinTypes.ARMOR_LEGS, SkinTypes.ARMOR_FEET, SkinTypes.ARMOR_WINGS);


    public static ISkinType byName(String registryName) {
        if (registryName == null) {
            return UNKNOWN;
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
        return register(name, new SkinType(name, id, Arrays.asList(parts)));
    }

    private static ISkinType registerArmor(String name, int id, EquipmentSlotType slotType, ISkinPartType... parts) {
        return register(name, new SkinType.Armor(name, id, slotType, Arrays.asList(parts)));
    }

    private static ISkinType registerArmor(String name, int id, EquipmentSlotType slotType, ISkinType... types) {
        List<ISkinPartType> partTypes = new ArrayList<>();
        for (ISkinType type : types) {
            partTypes.addAll(type.getParts());
        }
        return register(name, new SkinType.Armor(name, id, slotType, partTypes));
    }

    private static ISkinType registerItem(String name, int id, ITag<Item> tag, ISkinPartType... parts) {
        return register(name, new SkinType.Tool(name, id, Arrays.asList(parts), tag));
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
