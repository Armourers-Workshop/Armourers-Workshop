package moe.plushie.armourers_workshop.core.client.skinrender;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractSkinnableModels;
import moe.plushie.armourers_workshop.core.armature.ArmatureSerializers;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.Armatures;
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
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.TridentModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.skinrender.plugin.VillagerModelArmaturePlugin;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import net.minecraft.resources.ResourceLocation;

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
        registerClasses();
        Armatures.init();
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

        registerPlugin("armourers_workshop:plugin/fix_villager_layer", DefaultLayerArmaturePlugin::villager);
        registerPlugin("armourers_workshop:plugin/fix_mob_layer", DefaultLayerArmaturePlugin::mob);
        registerPlugin("armourers_workshop:plugin/fix_slime_layer", DefaultLayerArmaturePlugin::slime);
        registerPlugin("armourers_workshop:plugin/fix_villager_model", VillagerModelArmaturePlugin::new);

        registerPlugin("armourers_workshop:plugin/fix_trident_model", TridentModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_arrow_model", ArrowModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_fishing_model", FishingModelArmaturePlugin::new);
        registerPlugin("armourers_workshop:plugin/fix_boat_model", BoatModelArmaturePlugin::new);
    }

    private static void registerClasses() {

//        register("renderer/arrow", AbstractSkinnableRenderers.ARROW);
//        register("renderer/thrown_trident", AbstractSkinnableRenderers.THROWN_TRIDENT);
//        register("renderer/fishing_hook", AbstractSkinnableRenderers.FISHING_HOOK);

//        register2("model/illager", AbstractSkinnableModels.ILLAGER, CachedModel.Humanoid::new, (model, it) -> {
//            ModelPart root = model.root();
//            ModelPart head = root.getSafeChild("head");
//            ModelPart hat = head.getSafeChild("hat");
//            it.put("hat", hat);
//            it.put("head", head);
//            it.put("body", root.getSafeChild("body"));
//            it.put("arms", root.getSafeChild("arms"));
//            it.put("left_arm", root.getSafeChild("left_arm"));
//            it.put("right_arm", root.getSafeChild("right_arm"));
//            it.put("left_leg", root.getSafeChild("left_leg"));
//            it.put("right_leg", root.getSafeChild("right_leg"));
//        });

        registerClass("minecraft:model/illager", AbstractSkinnableModels.ILLAGER);
        registerClass("minecraft:model/zombie_villager", AbstractSkinnableModels.ZOMBIE_VILLAGER);

        registerClass("minecraft:model/villager", AbstractSkinnableModels.VILLAGER);
        registerClass("minecraft:model/iron_golem", AbstractSkinnableModels.IRON_GOLEM);
        registerClass("minecraft:model/enderman", AbstractSkinnableModels.ENDERMAN);

        registerClass("minecraft:model/player", AbstractSkinnableModels.PLAYER);
        registerClass("minecraft:model/humanoid", AbstractSkinnableModels.HUMANOID);

        registerClass("minecraft:model/slime", AbstractSkinnableModels.SLIME);
        registerClass("minecraft:model/ghast", AbstractSkinnableModels.GHAST);
        registerClass("minecraft:model/chicken", AbstractSkinnableModels.CHICKEN);
        registerClass("minecraft:model/creeper", AbstractSkinnableModels.CREEPER);
        registerClass("minecraft:model/horse", AbstractSkinnableModels.HORSE);

        registerClass("minecraft:model/boat", AbstractSkinnableModels.BOAT);
        registerClass("minecraft:model/raft", AbstractSkinnableModels.RAFT);

        registerClass("minecraft:model/allay", AbstractSkinnableModels.ALLAY);
    }


    public static class SimpleLoader implements IDataPackBuilder {

        private final ResourceLocation location;

        public SimpleLoader(ResourceLocation location) {
            this.location = location;
        }

        @Override
        public void append(IDataPackObject object, ResourceLocation file) {
            String type = object.get("type").stringValue();
            ArmatureTransformerManager manager = MANAGERS.get(type);
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


