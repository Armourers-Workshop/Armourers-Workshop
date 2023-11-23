package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.data;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "SameParameterValue"})
public class AdvancedSkinTypes {

    private static final ArrayList<Entry> ALL_TYPES = new ArrayList<>();

    public static final Entry GENERAL_ARMOR_HEAD = register("general", SkinTypes.ARMOR_HEAD);
    public static final Entry GENERAL_ARMOR_CHEST = register("general", SkinTypes.ARMOR_CHEST);
    public static final Entry GENERAL_ARMOR_FEET = register("general", SkinTypes.ARMOR_FEET);
    public static final Entry GENERAL_ARMOR_LEGS = register("general", SkinTypes.ARMOR_LEGS);
    public static final Entry GENERAL_ARMOR_WINGS = register("general", SkinTypes.ARMOR_WINGS);
    public static final Entry GENERAL_ARMOR_OUTFIT = register("general", SkinTypes.OUTFIT);

//    public static final Entry HORSE_ARMOR_HEAD = register("horse", SkinTypes.ARMOR_HEAD);
//    public static final Entry HORSE_ARMOR_CHEST = register("horse", SkinTypes.ARMOR_CHEST);
//    public static final Entry HORSE_ARMOR_FEET = register("horse", SkinTypes.ARMOR_FEET);
//    public static final Entry HORSE_ARMOR_LEGS = register("horse", SkinTypes.ARMOR_LEGS);
//    public static final Entry HORSE_ARMOR_WINGS = register("horse", SkinTypes.ARMOR_WINGS);
//    public static final Entry HORSE_ARMOR_OUTFIT = register("horse", SkinTypes.OUTFIT);
//
//    public static final Entry IRONMAN_ARMOR_HEAD = register("ironman", SkinTypes.ARMOR_HEAD);
//    public static final Entry IRONMAN_ARMOR_CHEST = register("ironman", SkinTypes.ARMOR_CHEST);
//    public static final Entry IRONMAN_ARMOR_FEET = register("ironman", SkinTypes.ARMOR_FEET);
//    public static final Entry IRONMAN_ARMOR_LEGS = register("ironman", SkinTypes.ARMOR_LEGS);
//    public static final Entry IRONMAN_ARMOR_WINGS = register("ironman", SkinTypes.ARMOR_WINGS);
//    public static final Entry IRONMAN_ARMOR_OUTFIT = register("ironman", SkinTypes.OUTFIT);


    public static final Entry ITEM = register("item", SkinTypes.ITEM);

    public static final Entry ITEM_SWORD = register("item", SkinTypes.ITEM_SWORD);
    public static final Entry ITEM_SHIELD = register("item", SkinTypes.ITEM_SHIELD);
    public static final Entry ITEM_BOW = register("item", SkinTypes.ITEM_BOW);
    public static final Entry ITEM_TRIDENT = register("item", SkinTypes.ITEM_TRIDENT);

    public static final Entry ITEM_PICKAXE = register("item", SkinTypes.TOOL_PICKAXE);
    public static final Entry ITEM_AXE = register("item", SkinTypes.TOOL_AXE);
    public static final Entry ITEM_SHOVEL = register("item", SkinTypes.TOOL_SHOVEL);
    public static final Entry ITEM_HOE = register("item", SkinTypes.TOOL_HOE);

    public static final Entry BLOCK = register("block", SkinTypes.BLOCK);


    public static void forEach(BiConsumer<String, List<Entry>> consumer) {
        ArrayList<String> names = new ArrayList<>();
        HashMap<String, List<Entry>> sections = new HashMap<>();

        Function<String, List<Entry>> builder = (name) -> {
            names.add(name);
            return new ArrayList<>();
        };

        ALL_TYPES.forEach(it -> sections.computeIfAbsent(it.category, builder).add(it));

        for (String name : names) {
            consumer.accept(name, sections.get(name));
        }
    }

    public static ArrayList<Entry> values() {
        return ALL_TYPES;
    }

    private static Entry register(String category, ISkinType skinType) {
        Entry advancedSkinType = new Entry(category, skinType);
        ALL_TYPES.add(advancedSkinType);
        return advancedSkinType;
    }

    public static class Entry {

        private final String category;
        private final ISkinType skinType;

        public Entry(String category, ISkinType skinType) {
            this.category = category;
            this.skinType = skinType;
        }

        public String getCategory() {
            return category;
        }

        public ISkinType getType() {
            return skinType;
        }
    }
}
