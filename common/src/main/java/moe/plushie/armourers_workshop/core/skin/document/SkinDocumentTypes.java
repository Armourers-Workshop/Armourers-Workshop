package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SkinDocumentTypes {

    private static final LinkedHashMap<String, SkinDocumentType> ALL_TYPES = new LinkedHashMap<>();

    public static final SkinDocumentType GENERAL_ARMOR_HEAD = register("general", SkinTypes.ARMOR_HEAD);
    public static final SkinDocumentType GENERAL_ARMOR_CHEST = register("general", SkinTypes.ARMOR_CHEST);
    public static final SkinDocumentType GENERAL_ARMOR_FEET = register("general", SkinTypes.ARMOR_FEET);
    public static final SkinDocumentType GENERAL_ARMOR_LEGS = register("general", SkinTypes.ARMOR_LEGS);
    public static final SkinDocumentType GENERAL_ARMOR_WINGS = register("general", SkinTypes.ARMOR_WINGS);
    public static final SkinDocumentType GENERAL_ARMOR_OUTFIT = register("general", SkinTypes.OUTFIT);

//    public static final AdvancedCategory IRONMAN_ARMOR_HEAD = register("ironman", SkinTypes.ARMOR_HEAD);
//    public static final AdvancedCategory IRONMAN_ARMOR_CHEST = register("ironman", SkinTypes.ARMOR_CHEST);
//    public static final AdvancedCategory IRONMAN_ARMOR_FEET = register("ironman", SkinTypes.ARMOR_FEET);
//    public static final AdvancedCategory IRONMAN_ARMOR_LEGS = register("ironman", SkinTypes.ARMOR_LEGS);
//    public static final AdvancedCategory IRONMAN_ARMOR_WINGS = register("ironman", SkinTypes.ARMOR_WINGS);
//    public static final AdvancedCategory IRONMAN_ARMOR_OUTFIT = register("ironman", SkinTypes.OUTFIT);

    public static final SkinDocumentType ITEM = register("item", SkinTypes.ITEM);

    public static final SkinDocumentType ITEM_SWORD = register("item", SkinTypes.ITEM_SWORD);
    public static final SkinDocumentType ITEM_SHIELD = register("item", SkinTypes.ITEM_SHIELD);
    public static final SkinDocumentType ITEM_BOW = register("item", SkinTypes.ITEM_BOW);
    public static final SkinDocumentType ITEM_TRIDENT = register("item", SkinTypes.ITEM_TRIDENT);

    public static final SkinDocumentType ITEM_PICKAXE = register("item", SkinTypes.ITEM_PICKAXE);
    public static final SkinDocumentType ITEM_AXE = register("item", SkinTypes.ITEM_AXE);
    public static final SkinDocumentType ITEM_SHOVEL = register("item", SkinTypes.ITEM_SHOVEL);
    public static final SkinDocumentType ITEM_HOE = register("item", SkinTypes.ITEM_HOE);

    public static final SkinDocumentType ITEM_BOAT = register("item", SkinTypes.ITEM_BOAT);
    public static final SkinDocumentType ITEM_FISHING = register("item", SkinTypes.ITEM_FISHING);

    public static final SkinDocumentType ENTITY_HORSE_OUTFIT = register("entity", SkinTypes.HORSE);

    public static final SkinDocumentType BLOCK = register("block", SkinTypes.BLOCK);

    public static SkinDocumentType byName(String name) {
        return ALL_TYPES.getOrDefault(name, GENERAL_ARMOR_HEAD);
    }

    public static void forEach(BiConsumer<String, List<SkinDocumentType>> consumer) {
        ArrayList<String> names = new ArrayList<>();
        HashMap<String, List<SkinDocumentType>> sections = new HashMap<>();

        Function<String, List<SkinDocumentType>> builder = (name) -> {
            names.add(name);
            return new ArrayList<>();
        };

        ALL_TYPES.forEach((key, it) -> sections.computeIfAbsent(it.getName(), builder).add(it));

        for (String name : names) {
            consumer.accept(name, sections.get(name));
        }
    }


    private static SkinDocumentType register(String category, ISkinType skinType) {
        SkinDocumentType advancedSkinType = new SkinDocumentType(category, skinType);
        advancedSkinType.setRegistryName(skinType.getRegistryName());
        ALL_TYPES.put(advancedSkinType.getRegistryName().toString(), advancedSkinType);
        return advancedSkinType;
    }
}
