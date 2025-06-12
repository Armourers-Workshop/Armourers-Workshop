package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.skin.part.advanced.AdvancedPartType;
import moe.plushie.armourers_workshop.core.skin.part.block.BlockPartType;
import moe.plushie.armourers_workshop.core.skin.part.block.MultiBlockPartType;
import moe.plushie.armourers_workshop.core.skin.part.bow.ArrowPartType;
import moe.plushie.armourers_workshop.core.skin.part.bow.BowPartType;
import moe.plushie.armourers_workshop.core.skin.part.chest.ChestPartType;
import moe.plushie.armourers_workshop.core.skin.part.chest.LeftArmPartType;
import moe.plushie.armourers_workshop.core.skin.part.chest.RightArmPartType;
import moe.plushie.armourers_workshop.core.skin.part.feet.LeftFootPartType;
import moe.plushie.armourers_workshop.core.skin.part.feet.RightFootPartType;
import moe.plushie.armourers_workshop.core.skin.part.head.HatPartType;
import moe.plushie.armourers_workshop.core.skin.part.head.HeadPartType;
import moe.plushie.armourers_workshop.core.skin.part.item.ItemPartType;
import moe.plushie.armourers_workshop.core.skin.part.item.OverrideItemPartType;
import moe.plushie.armourers_workshop.core.skin.part.item.ShieldPartType;
import moe.plushie.armourers_workshop.core.skin.part.legs.LeftLegPartType;
import moe.plushie.armourers_workshop.core.skin.part.legs.RightLegPartType;
import moe.plushie.armourers_workshop.core.skin.part.legs.SkirtPartType;
import moe.plushie.armourers_workshop.core.skin.part.other.PartitionPartType;
import moe.plushie.armourers_workshop.core.skin.part.other.UnknownPartType;
import moe.plushie.armourers_workshop.core.skin.part.wings.LeftWingPartType;
import moe.plushie.armourers_workshop.core.skin.part.wings.RightWingPartType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

@SuppressWarnings("unused")
public final class SkinPartTypes {

    private static final LinkedHashMap<String, SkinPartType> ALL_PART_TYPES = new LinkedHashMap<>();

    public static final IDataCodec<SkinPartType> CODEC = IDataCodec.STRING.xmap(SkinPartTypes::byName, SkinPartType::name);

    public static final SkinPartType UNKNOWN = register("unknown", new UnknownPartType());

    public static final SkinPartType BIPPED_HAT = register("hat.base", new HatPartType());
    public static final SkinPartType BIPPED_HEAD = register("head.base", new HeadPartType());
    public static final SkinPartType BIPPED_CHEST = register("chest.base", new ChestPartType());
    public static final SkinPartType BIPPED_LEFT_ARM = register("chest.leftArm", new LeftArmPartType());
    public static final SkinPartType BIPPED_RIGHT_ARM = register("chest.rightArm", new RightArmPartType());
    public static final SkinPartType BIPPED_SKIRT = register("legs.skirt", new SkirtPartType());
    public static final SkinPartType BIPPED_LEFT_THIGH = register("legs.leftLeg", new LeftLegPartType());
    public static final SkinPartType BIPPED_RIGHT_THIGH = register("legs.rightLeg", new RightLegPartType());
    public static final SkinPartType BIPPED_LEFT_FOOT = register("feet.leftFoot", new LeftFootPartType());
    public static final SkinPartType BIPPED_RIGHT_FOOT = register("feet.rightFoot", new RightFootPartType());
    public static final SkinPartType BIPPED_LEFT_WING = register("wings.leftWing", new LeftWingPartType());
    public static final SkinPartType BIPPED_RIGHT_WING = register("wings.rightWing", new RightWingPartType());
    public static final SkinPartType BIPPED_LEFT_PHALANX = register("wings.leftWing2", new PartitionPartType(BIPPED_LEFT_WING));
    public static final SkinPartType BIPPED_RIGHT_PHALANX = register("wings.rightWing2", new PartitionPartType(BIPPED_RIGHT_WING));

    public static final SkinPartType BIPPED_TORSO = register("chest.base2", new PartitionPartType(BIPPED_CHEST));
    public static final SkinPartType BIPPED_LEFT_HAND = register("chest.leftArm2", new PartitionPartType(BIPPED_LEFT_ARM));
    public static final SkinPartType BIPPED_RIGHT_HAND = register("chest.rightArm2", new PartitionPartType(BIPPED_RIGHT_ARM));
    //public static final SkinPartType BIPPED_SKIRT2 = register("legs.skirt2", new PartitionPartType(BIPPED_SKIRT));
    public static final SkinPartType BIPPED_LEFT_LEG = register("legs.leftLeg2", new PartitionPartType(BIPPED_LEFT_THIGH));
    public static final SkinPartType BIPPED_RIGHT_LEG = register("legs.rightLeg2", new PartitionPartType(BIPPED_RIGHT_THIGH));

    public static final SkinPartType ITEM_PICKAXE = register("pickaxe.base", new ItemPartType());
    public static final SkinPartType ITEM_AXE = register("axe.base", new ItemPartType());
    public static final SkinPartType ITEM_SHOVEL = register("shovel.base", new ItemPartType());
    public static final SkinPartType ITEM_HOE = register("hoe.base", new ItemPartType());

    public static final SkinPartType ITEM_BOW0 = register("bow.frame0", new BowPartType(0));
    public static final SkinPartType ITEM_BOW1 = register("bow.frame1", new BowPartType(1));
    public static final SkinPartType ITEM_BOW2 = register("bow.frame2", new BowPartType(2));
    public static final SkinPartType ITEM_BOW3 = register("bow.frame3", new BowPartType(3));

    public static final SkinPartType ITEM_ARROW = register("bow.arrow", new ArrowPartType());
    public static final SkinPartType ITEM_SWORD = register("sword.base", new ItemPartType());
    public static final SkinPartType ITEM_SHIELD = register("shield.base", new ShieldPartType());
    public static final SkinPartType ITEM_TRIDENT = register("trident.base", new ItemPartType());

    public static final SkinPartType ITEM_FISHING_ROD = register("fishing.rod", new ItemPartType());
    public static final SkinPartType ITEM_FISHING_HOOK = register("fishing.hook", new ItemPartType());

    public static final SkinPartType ITEM_BACKPACK = register("backpack.base", new ItemPartType());

    public static final SkinPartType ITEM_SHIELD1 = register("shield.blocking", new OverrideItemPartType("blocking"));
    public static final SkinPartType ITEM_TRIDENT1 = register("trident.throwing", new OverrideItemPartType("throwing"));
    public static final SkinPartType ITEM_FISHING_ROD1 = register("fishing.rod1", new OverrideItemPartType("cast"));

    public static final SkinPartType ITEM = register("item.base", new ItemPartType());
    public static final SkinPartType BLOCK = register("block.base", new BlockPartType());
    public static final SkinPartType BLOCK_MULTI = register("block.multiblock", new MultiBlockPartType());

//    public static final SkinPartType PLAYER_JACKET = register("player.jacket.base", new UnknownPartType());
//    public static final SkinPartType PLAYER_LEFT_PANTS = register("player.pants.leftPants", new UnknownPartType());
//    public static final SkinPartType PLAYER_RIGHT_PANTS = register("player.pants.rightPants", new UnknownPartType());
//    public static final SkinPartType PLAYER_LEFT_SLEEVE = register("player.sleeves.leftSleeve", new UnknownPartType());
//    public static final SkinPartType PLAYER_RIGHT_SLEEVE = register("player.sleeves.rightSleeve", new UnknownPartType());

    public static final SkinPartType ADVANCED = register("part.advanced_part", new AdvancedPartType());

    public static final SkinPartType ADVANCED_LOCATOR = register("part.locator", new AdvancedPartType());
    public static final SkinPartType ADVANCED_STATIC = register("part.static", new AdvancedPartType());
    public static final SkinPartType ADVANCED_FLOAT = register("part.float", new AdvancedPartType());

    public static final SkinPartType BOAT_BODY = register("boat.base", new ItemPartType());
    public static final SkinPartType BOAT_LEFT_PADDLE = register("boat.leftPaddle", new ItemPartType());
    public static final SkinPartType BOAT_RIGHT_PADDLE = register("boat.rightPaddle", new ItemPartType());

    public static final SkinPartType MINECART_BODY = register("minecart.base", new ItemPartType());

    public static final SkinPartType HORSE_HEAD = register("horse.head", new AdvancedPartType());
    public static final SkinPartType HORSE_NECK = register("horse.neck", new AdvancedPartType());
    public static final SkinPartType HORSE_CHEST = register("horse.chest", new AdvancedPartType());
    //public static final SkinPartType HORSE_LOIN = register("horse.loin", new AdvancedPartType());
    public static final SkinPartType HORSE_LEFT_FRONT_THIGH = register("horse.left_front_leg", new AdvancedPartType());
    public static final SkinPartType HORSE_RIGHT_FRONT_THIGH = register("horse.right_front_leg", new AdvancedPartType());
    public static final SkinPartType HORSE_LEFT_HIND_THIGH = register("horse.left_hind_leg", new AdvancedPartType());
    public static final SkinPartType HORSE_RIGHT_HIND_THIGH = register("horse.right_hind_leg", new AdvancedPartType());
    public static final SkinPartType HORSE_LEFT_FRONT_LEG = register("horse.left_front_leg2", new PartitionPartType(HORSE_LEFT_FRONT_THIGH));
    public static final SkinPartType HORSE_RIGHT_FRONT_LEG = register("horse.right_front_leg2", new PartitionPartType(HORSE_RIGHT_FRONT_THIGH));
    public static final SkinPartType HORSE_LEFT_HIND_LEG = register("horse.left_hind_leg2", new PartitionPartType(HORSE_LEFT_HIND_THIGH));
    public static final SkinPartType HORSE_RIGHT_HIND_LEG = register("horse.right_hind_leg2", new PartitionPartType(HORSE_RIGHT_HIND_THIGH));
    //public static final SkinPartType HORSE_SADDLE = register("horse.saddle", new AdvancedPartType());
    public static final SkinPartType HORSE_TAIL = register("horse.tail", new AdvancedPartType());

    public static SkinPartType byName(String registryName) {
        if (registryName == null) {
            return UNKNOWN;
        }
        if (registryName.equals("armourers:skirt.base")) {
            return BIPPED_SKIRT;
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

    public static Collection<SkinPartType> registeredTypes() {
        return ALL_PART_TYPES.values();
    }

    private static SkinPartType register(String name, SkinPartType partType) {
        partType.setRegistryName(OpenResourceLocation.create("armourers", name));
        if (ALL_PART_TYPES.containsKey(partType.registryName().toString())) {
            ModLog.warn("A mod tried to register a skin type with a registry name that is in use.");
            return partType;
        }
        ALL_PART_TYPES.put(partType.registryName().toString(), partType);
        ModLog.debug("Registering Skin Part '{}'", partType.registryName());
        return partType;
    }
}

