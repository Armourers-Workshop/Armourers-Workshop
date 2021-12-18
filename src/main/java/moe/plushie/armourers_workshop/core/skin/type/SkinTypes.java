package moe.plushie.armourers_workshop.core.skin.type;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.type.outfit.SkinOutfit;
import moe.plushie.armourers_workshop.core.utils.SkinLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SkinTypes {

    public static final Map<String, ISkinType> skinTypes = new HashMap<>();

    public static final ISkinType UNKNOWN = register("unknown", SkinPartTypes.UNKNOWN);

    public static final ISkinType BIPED_HEAD = register("head", SkinPartTypes.BIPED_HEAD);
    public static final ISkinType BIPED_CHEST = register("chest", SkinPartTypes.BIPED_CHEST, SkinPartTypes.BIPED_LEFT_ARM, SkinPartTypes.BIPED_RIGHT_ARM);
    public static final ISkinType BIPED_LEGS = register("legs", SkinPartTypes.BIPED_LEFT_LEG, SkinPartTypes.BIPED_RIGHT_LEG, SkinPartTypes.BIPED_SKIRT);
    public static final ISkinType BIPED_FEET = register("feet", SkinPartTypes.BIPED_LEFT_FOOT, SkinPartTypes.BIPED_RIGHT_FOOT);
    public static final ISkinType BIPED_WINGS = register("wings", SkinPartTypes.BIPED_LEFT_WING, SkinPartTypes.BIPED_RIGHT_WING);
    public static final ISkinType BIPED_OUTFIT = register("outfit", new SkinOutfit(BIPED_HEAD, BIPED_CHEST, BIPED_LEGS, BIPED_FEET, BIPED_WINGS));

    //public static final ISkinType HORSE = register("horse", SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType ITEM_SWORD = register("sword", SkinPartTypes.ITEM_SWORD);
    public static final ISkinType ITEM_SHIELD = register("shield", SkinPartTypes.ITEM_SHIELD);
    public static final ISkinType ITEM_BOW = register("bow", SkinPartTypes.ITEM_BOW1, SkinPartTypes.ITEM_BOW2, SkinPartTypes.ITEM_BOW3, SkinPartTypes.ITEM_ARROW);

    public static final ISkinType TOOL_PICKAXE = register("pickaxe", SkinPartTypes.TOOL_PICKAXE);
    public static final ISkinType TOOL_AXE = register("axe", SkinPartTypes.TOOL_AXE);
    public static final ISkinType TOOL_SHOVEL = register("shovel", SkinPartTypes.TOOL_SHOVEL);
    public static final ISkinType TOOL_HOPE = register("hope", SkinPartTypes.TOOL_SHOVEL);

    public static final ISkinType ITEM = register("item", SkinPartTypes.ITEM);
    public static final ISkinType BLOCK = register("block", SkinPartTypes.BLOCK, SkinPartTypes.BLOCK_MULTI);

    public static final ISkinType ADVANCED = register("part", SkinPartTypes.ADVANCED);

    public static ISkinType byName(String registryName) {
        if (registryName == null) {
            return UNKNOWN;
        }
        if (registryName.equals("armourers:skirt")) {
            return BIPED_LEGS;
        }
        if (registryName.equals("armourers:arrow")) {
            return ITEM_BOW;
        }
        return skinTypes.getOrDefault(registryName, UNKNOWN);
    }

    private static ISkinType register(String name, ISkinPartType... parts) {
        return register(name, new BaseSkinType(Arrays.asList(parts)));
    }

    private static ISkinType register(String name, AbstractSkinType skinType) {
        skinType.setRegistryName("armourers:" + name);
        if (skinType.getParts().size() == 0) {
            SkinLog.warn("A mod tried to register a skin type no skin type parts.");
            return skinType;
        }
        if (skinTypes.containsKey(skinType.getRegistryName())) {
            SkinLog.warn("A mod tried to register a skin type with a registry name that is in use.");
            return skinType;
        }
        skinTypes.put(skinType.getRegistryName(), skinType);
        SkinLog.info(String.format("Registering Skin '%s'", skinType.getRegistryName()));
        return skinType;
    }


    private static class BaseSkinType extends AbstractSkinType {

        protected List<? extends ISkinPartType> parts;

        public BaseSkinType(List<? extends ISkinPartType> parts) {
            this.parts = parts;
        }

        @Override
        public List<? extends ISkinPartType> getParts() {
            return parts;
        }
    }
}
