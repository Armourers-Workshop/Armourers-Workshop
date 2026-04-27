package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.data.slot.ItemOverrideType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class SkinTypes {

    private static final ArrayList<SkinType> ALL_SORTED_TYPES = new ArrayList<>();
    private static final LinkedHashMap<String, SkinType> ALL_TYPES = new LinkedHashMap<>();

    public static final IDataCodec<SkinType> CODEC = IDataCodec.STRING.xmap(SkinTypes::byName, SkinType::name);

    public static final SkinType UNKNOWN = normal(255).part(SkinPartTypes.UNKNOWN).build("unknown");

    public static final SkinType ARMOR_HEAD = armour(1).part(SkinPartTypes.BIPPED_HEAD).equipmentSlot(OpenEquipmentSlot.HEAD).build("head");
    public static final SkinType ARMOR_CHEST = armour(2).part(SkinPartTypes.BIPPED_CHEST).part(SkinPartTypes.BIPPED_LEFT_ARM).part(SkinPartTypes.BIPPED_RIGHT_ARM).equipmentSlot(OpenEquipmentSlot.CHEST).build("chest");
    public static final SkinType ARMOR_LEGS = armour(3).part(SkinPartTypes.BIPPED_LEFT_THIGH).part(SkinPartTypes.BIPPED_RIGHT_THIGH).part(SkinPartTypes.BIPPED_SKIRT).equipmentSlot(OpenEquipmentSlot.LEGS).build("legs");
    public static final SkinType ARMOR_FEET = armour(4).part(SkinPartTypes.BIPPED_LEFT_FOOT).part(SkinPartTypes.BIPPED_RIGHT_FOOT).equipmentSlot(OpenEquipmentSlot.FEET).build("feet");
    public static final SkinType ARMOR_WINGS = armour(5).part(SkinPartTypes.BIPPED_LEFT_WING).part(SkinPartTypes.BIPPED_RIGHT_WING).build("wings");

    public static final SkinType OUTFIT = armour(6).part(SkinTypes.ARMOR_HEAD).part(SkinTypes.ARMOR_CHEST).part(SkinTypes.ARMOR_LEGS).part(SkinTypes.ARMOR_FEET).part(SkinTypes.ARMOR_WINGS).build("outfit");

    public static final SkinType ITEM_SWORD = item(7).part(SkinPartTypes.ITEM_SWORD).override(ItemOverrideType.SWORD).build("sword");
    public static final SkinType ITEM_SHIELD = item(8).part(SkinPartTypes.ITEM_SHIELD).override(ItemOverrideType.SHIELD).build("shield");
    public static final SkinType ITEM_BOW = item(9).part(SkinPartTypes.ITEM_BOW0).part(SkinPartTypes.ITEM_BOW1).part(SkinPartTypes.ITEM_BOW2).part(SkinPartTypes.ITEM_BOW3).part(SkinPartTypes.ITEM_ARROW).override(ItemOverrideType.BOW).build("bow");
    public static final SkinType ITEM_TRIDENT = item(17).part(SkinPartTypes.ITEM_TRIDENT).override(ItemOverrideType.TRIDENT).build("trident");

    public static final SkinType ITEM_PICKAXE = item(10).part(SkinPartTypes.ITEM_PICKAXE).override(ItemOverrideType.PICKAXE).build("pickaxe");
    public static final SkinType ITEM_AXE = item(11).part(SkinPartTypes.ITEM_AXE).override(ItemOverrideType.AXE).build("axe");
    public static final SkinType ITEM_SHOVEL = item(12).part(SkinPartTypes.ITEM_SHOVEL).override(ItemOverrideType.SHOVEL).build("shovel");
    public static final SkinType ITEM_HOE = item(13).part(SkinPartTypes.ITEM_HOE).override(ItemOverrideType.HOE).build("hoe");

    public static final SkinType ITEM = normal(14).part(SkinPartTypes.ITEM).build("item");
    public static final SkinType BLOCK = normal(15).part(SkinPartTypes.BLOCK).part(SkinPartTypes.BLOCK_MULTI).build("block");

    public static final SkinType HORSE = armour(18).part(SkinPartTypes.HORSE_HEAD).part(SkinPartTypes.HORSE_NECK).part(SkinPartTypes.HORSE_CHEST).part(SkinPartTypes.HORSE_RIGHT_FRONT_THIGH).part(SkinPartTypes.HORSE_LEFT_FRONT_THIGH).part(SkinPartTypes.HORSE_RIGHT_FRONT_LEG).part(SkinPartTypes.HORSE_LEFT_FRONT_LEG).part(SkinPartTypes.HORSE_RIGHT_HIND_THIGH).part(SkinPartTypes.HORSE_LEFT_HIND_THIGH).part(SkinPartTypes.HORSE_RIGHT_HIND_LEG).part(SkinPartTypes.HORSE_LEFT_HIND_LEG).part(SkinPartTypes.HORSE_TAIL).build("horse");
    public static final SkinType BOAT = item(19).part(SkinPartTypes.BOAT_BODY).part(SkinPartTypes.BOAT_LEFT_PADDLE).part(SkinPartTypes.BOAT_RIGHT_PADDLE).override(ItemOverrideType.BOAT).build("boat");
    public static final SkinType MINECART = item(21).part(SkinPartTypes.MINECART_BODY).override(ItemOverrideType.MINECART).build("minecart");

    public static final SkinType ITEM_FISHING = item(20).part(SkinPartTypes.ITEM_FISHING_ROD).part(SkinPartTypes.ITEM_FISHING_HOOK).override(ItemOverrideType.FISHING_ROD).build("fishing");
    public static final SkinType ITEM_BACKPACK = item(24).part(SkinPartTypes.ITEM_BACKPACK).override(ItemOverrideType.BACKPACK).build("backpack");

    public static final SkinType ADVANCED = normal(16).part(SkinPartTypes.ADVANCED).build("part");

    public static SkinType byName(String registryName) {
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

    public static ArrayList<SkinType> values() {
        return ALL_SORTED_TYPES;
    }

    private static Builder normal(int id) {
        return new Builder((builder, name) -> new SkinType(name, id, builder.partTypes));
    }

    private static Builder item(int id) {
        return new Builder((builder, name) -> new SkinType.Tool(name, id, builder.partTypes, builder.overrideType::isOverrideItem));
    }

    private static Builder armour(int id) {
        return new Builder((builder, name) -> new SkinType.Armor(name, id, builder.equipmentSlot, builder.partTypes));
    }

    private static class Builder {

        private ItemOverrideType overrideType;
        private OpenEquipmentSlot equipmentSlot;

        private final ArrayList<SkinPartType> partTypes = new ArrayList<>();

        private final BiFunction<Builder, String, SkinType> factory;

        public Builder(BiFunction<Builder, String, SkinType> factory) {
            this.factory = factory;
        }

        public Builder part(SkinPartType partType) {
            this.partTypes.add(partType);
            return this;
        }

        public Builder part(SkinType skinType) {
            this.partTypes.addAll(skinType.parts());
            return this;
        }

        public Builder override(ItemOverrideType overrideType) {
            this.overrideType = overrideType;
            return this;
        }

        public Builder equipmentSlot(OpenEquipmentSlot equipmentSlot) {
            this.equipmentSlot = equipmentSlot;
            return this;
        }

        public SkinType build(String name) {
            var type = factory.apply(this, name);
            type.setRegistryName(OpenResourceKey.create("armourers", name));
            if (type.parts().isEmpty()) {
                ModLog.warn("A mod tried to register a skin type no skin type parts.");
                return type;
            }
            if (ALL_TYPES.containsKey(type.registryName().toString())) {
                ModLog.warn("A mod tried to register a skin type with a registry name that is in use.");
                return type;
            }
            ALL_SORTED_TYPES.add(type);
            ALL_TYPES.put(type.registryName().toString(), type);
            ModLog.debug("Registering Skin '{}'", type.registryName());
            return type;
        }
    }
}
