package moe.plushie.armourers_workshop.core.client.skinrender;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelHolder;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractSkinnableModels;
import moe.plushie.armourers_workshop.core.armature.ArmatureSerializers;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.core.DefaultLayerArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.AllayBodyJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.AllayHeadJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.AllayWingJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.DefaultBabyJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.DefaultSkirtJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.FlatWingJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.modifier.HorseBodyJointModifier;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.ArrowModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.BoatModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.FishingModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.MinecartModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.TridentModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.VillagerModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import net.minecraft.client.model.Model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SkinRendererManager2 extends ArmatureSerializers {

    public static final ArmatureTransformerManager DEFAULT = new DefaultArmatureTransformerManager();
    public static final ArmatureTransformerManager EPICFIGHT = new EpicFlightArmatureTransformerManager();

    private static final ImmutableMap<String, ArmatureTransformerManager> MANAGERS = ImmutableMap.<String, ArmatureTransformerManager>builder()
            .put("armourers_workshop:armature", DEFAULT)
            .put("epicfight:armature", EPICFIGHT)
            .build();

    public static void init() {
        registerModifiers();
        registerPlugins();
        registerRenderers();
        registerModels();
        DataPackManager.register(DataPackType.BUNDLED_DATA, "skin/transformers", SimpleLoader::new, SimpleLoader::clean, SimpleLoader::freeze, 0);
    }

    private static void registerModifiers() {

        registerModifier("armourers_workshop:modifier/baby_head_apt", DefaultBabyJointModifier::new);
        registerModifier("armourers_workshop:modifier/body_to_skirt", DefaultSkirtJointModifier::new);
        registerModifier("armourers_workshop:modifier/body_to_flat_wing", FlatWingJointModifier::new);
        registerModifier("armourers_workshop:modifier/apply_ally_head", AllayHeadJointModifier::new);
        registerModifier("armourers_workshop:modifier/apply_ally_body", AllayBodyJointModifier::new);
        registerModifier("armourers_workshop:modifier/apply_ally_wing", AllayWingJointModifier::new);
        registerModifier("armourers_workshop:modifier/apply_horse_body", HorseBodyJointModifier::new);
    }

    private static void registerPlugins() {

        registerPlugin("armourers_workshop:plugin/fix_shulker_layer", DefaultLayerArmaturePlugin::shulker);
        registerPlugin("armourers_workshop:plugin/fix_villager_layer", DefaultLayerArmaturePlugin::villager);
        registerPlugin("armourers_workshop:plugin/fix_mob_layer", DefaultLayerArmaturePlugin::mob);
        registerPlugin("armourers_workshop:plugin/fix_slime_layer", DefaultLayerArmaturePlugin::slime);
        registerPlugin("armourers_workshop:plugin/fix_villager_model", VillagerModelArmaturePlugin::new);

        registerPlugin("armourers_workshop:plugin/fix_trident_model", TridentModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_arrow_model", ArrowModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_fishing_model", FishingModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_boat_model", BoatModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_minecart_model", MinecartModelArmaturePlugin::new);
    }

    private static void registerRenderers() {
//        register("renderer/arrow", AbstractSkinnableRenderers.ARROW);
//        register("renderer/thrown_trident", AbstractSkinnableRenderers.THROWN_TRIDENT);
//        register("renderer/fishing_hook", AbstractSkinnableRenderers.FISHING_HOOK);
    }

    private static void registerModels() {

        registerModel("minecraft:model/slime", AbstractSkinnableModels.SLIME);
        registerModel("minecraft:model/ghast", AbstractSkinnableModels.GHAST);

        registerModel("minecraft:model/enderman", AbstractSkinnableModels.ENDERMAN);
        registerModel("minecraft:model/zombie_villager", AbstractSkinnableModels.ZOMBIE_VILLAGER);

        registerModel("minecraft:model/illager", AbstractSkinnableModels.ILLAGER, it -> {
            it.put("head", "root.head");
            it.put("body", "root.body");
            it.put("left_leg", "root.left_leg");
            it.put("right_leg", "root.right_leg");
            it.put("arms", "root.arms");
            it.put("right_arm", "root.right_arm");
            it.put("left_arm", "root.left_arm");
            it.put("hat", "root.head.hat");
            it.put("nose", "root.head.nose");
        });

        registerModel("minecraft:model/villager", AbstractSkinnableModels.VILLAGER, it -> {
            it.put("hat", "root.head.hat");
            it.put("hat_rim", "root.head.hat.hat_rim");
            it.put("head", "root.head");
            it.put("nose", "root.head.nose");
            it.put("body", "root.body");
            it.put("right_leg", "root.right_leg");
            it.put("left_leg", "root.left_leg");
            it.put("left_arm", "root.arms");
            it.put("right_arm", "root.arms");
            it.put("jacket", "root.body.jacket");
        });

        registerModel("minecraft:model/iron_golem", AbstractSkinnableModels.IRON_GOLEM, it -> {
            it.put("head", "root.head");
            it.put("body", "root.body");
            it.put("right_leg", "root.right_leg");
            it.put("left_leg", "root.left_leg");
            it.put("right_arm", "root.right_arm");
            it.put("left_arm", "root.left_arm");
        });

        registerModel("minecraft:model/humanoid", AbstractSkinnableModels.HUMANOID, it -> {
            it.put("head", "headParts[0]");
            it.put("body", "bodyParts[0]");
            it.put("right_arm", "bodyParts[1]");
            it.put("left_arm", "bodyParts[2]");
            it.put("right_leg", "bodyParts[3]");
            it.put("left_leg", "bodyParts[4]");
            it.put("hat", "bodyParts[5]");
        });
        registerModel("minecraft:model/player", AbstractSkinnableModels.PLAYER, it -> {
            it.put("left_pants", "bodyParts[6]");
            it.put("right_pants", "bodyParts[7]");
            it.put("left_sleeve", "bodyParts[8]");
            it.put("right_sleeve", "bodyParts[9]");
            it.put("jacket", "bodyParts[10]");
        });

        registerModel("minecraft:model/chicken", AbstractSkinnableModels.CHICKEN, it -> {
            it.put("head", "headParts[0]");
            it.put("beak", "headParts[1]");
            it.put("red_thing", "headParts[2]");
            it.put("body", "bodyParts[0]");
            it.put("right_leg", "bodyParts[1]");
            it.put("left_leg", "bodyParts[2]");
            it.put("right_wing", "bodyParts[3]");
            it.put("left_wing", "bodyParts[4]");
        });

        registerModel("minecraft:model/creeper", AbstractSkinnableModels.CREEPER, it -> {
            it.put("head", "root.head");
        });

        registerModel("minecraft:model/horse", AbstractSkinnableModels.HORSE, it -> {
            it.put("head", "headParts[0]");
            it.put("body", "bodyParts[0]");
            it.put("right_hind_leg", "bodyParts[1]");
            it.put("left_hind_leg", "bodyParts[2]");
            it.put("right_front_leg", "bodyParts[3]");
            it.put("left_front_leg", "bodyParts[4]");
            it.put("right_front_baby_leg", "bodyParts[5]");
            it.put("left_front_baby_leg", "bodyParts[6]");
            it.put("right_hind_baby_leg", "bodyParts[7]");
            it.put("left_hind_baby_leg", "bodyParts[8]");
            it.put("tail", "bodyParts[0].tail");
        });

        registerModel("minecraft:model/pig", AbstractSkinnableModels.PIG);
        registerModel("minecraft:model/shulker", AbstractSkinnableModels.SHULKER);

        registerModel("minecraft:model/boat", AbstractSkinnableModels.BOAT, it -> {
            it.put("bottom", "parts[0]");
            it.put("back", "parts[1]");
            it.put("front", "parts[2]");
            it.put("right", "parts[3]");
            it.put("left", "parts[4]");
            it.put("left_paddle", "parts[5]");
            it.put("right_paddle", "parts[6]");
        });

        registerModel("minecraft:model/raft", AbstractSkinnableModels.RAFT, it -> {
            it.put("bottom", "parts[0]");
            it.put("left_paddle", "parts[1]");
            it.put("right_paddle", "parts[2]");
        });

        registerModel("minecraft:model/allay", AbstractSkinnableModels.ALLAY, it -> {
            it.put("root", "root");
            it.put("head", "root.head");
            it.put("body", "root.body");
            it.put("right_arm", "root.body.right_arm");
            it.put("left_arm", "root.body.left_arm");
            it.put("right_leg", "root.body.right_wing");
            it.put("left_leg", "root.body.left_wing");
            it.put("right_wing", "root.body.right_wing");
            it.put("left_wing", "root.body.left_wing");
        });
    }

    public static <T extends Model> void registerModel(String registryName, Class<T> clazz) {
        registerModel(registryName, clazz, it -> {
            // nope.
        });
    }

    public static <T extends Model> void registerModel(String registryName, Class<T> clazz, Consumer<Map<String, String>> provider) {
        if (clazz != null) {
            var mapper = new LinkedHashMap<String, String>();
            provider.accept(mapper);
            registerClass(registryName, clazz);
            AbstractModelHolder.register(clazz, mapper);
        }
    }


    public static class SimpleLoader implements IDataPackBuilder {

        private final IResourceLocation location;

        public SimpleLoader(IResourceLocation location) {
            this.location = location;
        }

        @Override
        public void append(IDataPackObject object, IResourceLocation file) {
            var type = object.get("type").stringValue();
            var manager = MANAGERS.get(type);
            if (manager != null) {
                manager.append(object, location);
            }
        }

        @Override
        public void build() {
        }

        public static void clean() {
            MANAGERS.values().forEach(ArmatureTransformerManager::clear);
        }

        public static void freeze() {
            MANAGERS.values().forEach(ArmatureTransformerManager::freeze);
        }
    }
}


