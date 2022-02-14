package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.ISkinArmorType;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.tags.SkinTags;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinLog;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@SuppressWarnings("unused")
public final class SkinTypes {

    private static final Map<String, ISkinType> ALL_TYPES = new HashMap<>();

    public static final ISkinType UNKNOWN = register("unknown", SkinPartTypes.UNKNOWN);

    public static final ISkinType ARMOR_HEAD = registerArmor("head", SkinPartTypes.BIPED_HEAD);
    public static final ISkinType ARMOR_CHEST = registerArmor("chest", SkinPartTypes.BIPED_CHEST, SkinPartTypes.BIPED_LEFT_ARM, SkinPartTypes.BIPED_RIGHT_ARM);
    public static final ISkinType ARMOR_LEGS = registerArmor("legs", SkinPartTypes.BIPED_LEFT_LEG, SkinPartTypes.BIPED_RIGHT_LEG, SkinPartTypes.BIPED_SKIRT);
    public static final ISkinType ARMOR_FEET = registerArmor("feet", SkinPartTypes.BIPED_LEFT_FOOT, SkinPartTypes.BIPED_RIGHT_FOOT);
    public static final ISkinType ARMOR_WINGS = registerArmor("wings", SkinPartTypes.BIPED_LEFT_WING, SkinPartTypes.BIPED_RIGHT_WING);
    public static final ISkinType ARMOR_OUTFIT = registerArmor("outfit", SkinTypes.ARMOR_HEAD, SkinTypes.ARMOR_CHEST, SkinTypes.ARMOR_LEGS, SkinTypes.ARMOR_FEET, SkinTypes.ARMOR_WINGS);

    public static final ISkinType HORSE = register("horse", SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType ITEM_SWORD = registerItem("sword", SkinTags.SWORDS, SkinPartTypes.ITEM_SWORD);
    public static final ISkinType ITEM_SHIELD = registerItem("shield", SkinTags.SHIELDS, SkinPartTypes.ITEM_SHIELD);
    public static final ISkinType ITEM_BOW = registerItem("bow", SkinTags.BOWS, SkinPartTypes.ITEM_BOW1, SkinPartTypes.ITEM_BOW2, SkinPartTypes.ITEM_BOW3, SkinPartTypes.ITEM_ARROW);

    public static final ISkinType TOOL_PICKAXE = registerItem("pickaxe", SkinTags.PICKAXES, SkinPartTypes.TOOL_PICKAXE);
    public static final ISkinType TOOL_AXE = registerItem("axe", SkinTags.AXES, SkinPartTypes.TOOL_AXE);
    public static final ISkinType TOOL_SHOVEL = registerItem("shovel", SkinTags.SHOVELS, SkinPartTypes.TOOL_SHOVEL);
    public static final ISkinType TOOL_HOE = registerItem("hoe", SkinTags.HOES, SkinPartTypes.TOOL_HOE);

    public static final ISkinType ITEM = register("item", SkinPartTypes.ITEM);
    public static final ISkinType BLOCK = register("block", SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType ADVANCED = register("part", SkinPartTypes.ADVANCED);

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

    private static ISkinType register(String name, ISkinPartType... parts) {
        return register(name, new ParameterizedSkinType(name, Arrays.asList(parts)));
    }

    private static ISkinType registerArmor(String name, ISkinPartType... parts) {
        return register(name, new ArmorType(name, Arrays.asList(parts)));
    }

    private static ISkinType registerArmor(String name, ISkinType... types) {
        List<ISkinPartType> partTypes = new ArrayList<>();
        for (ISkinType type : types) {
            partTypes.addAll(type.getParts());
        }
        return register(name, new ArmorType(name, partTypes));
    }

    private static ISkinType registerItem(String name, ITag<Item> tag, ISkinPartType... parts) {
        return register(name, new ItemType(name, Arrays.asList(parts), tag));
    }

    private static ISkinType register(String name, ParameterizedSkinType type) {
        type.setRegistryName("armourers:" + name);
        if (type.getParts().size() == 0) {
            SkinLog.warn("A mod tried to register a skin type no skin type parts.");
            return type;
        }
        if (ALL_TYPES.containsKey(type.getRegistryName())) {
            SkinLog.warn("A mod tried to register a skin type with a registry name that is in use.");
            return type;
        }
        ALL_TYPES.put(type.getRegistryName(), type);
        SkinLog.debug("Registering Skin '{}'", type.getRegistryName());
        return type;
    }


    private static class ParameterizedSkinType implements ISkinType {

        protected final String name;
        protected String registryName;
        protected List<? extends ISkinPartType> parts;

        public ParameterizedSkinType(String name, List<? extends ISkinPartType> parts) {
            this.parts = parts;
            this.name = name;
        }

        @Override
        public String getRegistryName() {
            return registryName;
        }

        public void setRegistryName(String registryName) {
            this.registryName = registryName;
        }

        @Override
        public String toString() {
            return registryName;
        }

        @Override
        public List<? extends ISkinPartType> getParts() {
            return parts;
        }
    }

    private static class ArmorType extends ParameterizedSkinType implements ISkinArmorType {
        public ArmorType(String name, List<? extends ISkinPartType> parts) {
            super(name, parts);
        }
    }

    private static class ItemType extends ParameterizedSkinType implements ISkinToolType {

        protected ITag<Item> tag;

        public ItemType(String name, List<? extends ISkinPartType> parts, ITag<Item> tag) {
            super(name, parts);
            this.tag = tag;
        }

        @Override
        public ITag<Item> getTag() {
            return tag;
        }
    }
}
