package moe.plushie.armourers_workshop.core.client.skinrender;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractSkinnableModels;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightArmatureTransformerManager;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import moe.plushie.armourers_workshop.core.data.DataPackType;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

@SuppressWarnings("unused")
public class SkinRendererManager2 {

    public static final HashMap<ResourceLocation, Class<?>> NAMED_CLASSES = new HashMap<>();

    public static final ArmatureTransformerManager DEFAULT = new DefaultArmatureTransformerManager();
    public static final ArmatureTransformerManager EPICFIGHT = new EpicFlightArmatureTransformerManager();

    private static final ImmutableMap<String, ArmatureTransformerManager> MANAGERS = ImmutableMap.<String, ArmatureTransformerManager>builder()
            .put("armourers_workshop:armature", DEFAULT)
            .put("epicfight:armature", EPICFIGHT)
            .build();


    public static void init() {
        Armatures.init();
        registerAll();
        DataPackManager.register(DataPackType.JAR, new DataPackLoader("skin/transformers", SimpleLoader::new, SimpleLoader::clean, SimpleLoader::freeze));
    }

    private static void register(String name, Class<?> clazz) {
        NAMED_CLASSES.put(new ResourceLocation("minecraft", name), clazz);
    }

//    public static <T extends Model> void register2(String name, Class<T> clazz, Function<ModelHolder.Container, IModel> factory, BiConsumer<T, ModelHolder.Container> builder) {
////        ENTRIES.put(clazz, new ModelHolder.Entry<>(clazz, factory, builder));
//        NAMED_CLASSES.put(new ResourceLocation("minecraft", name), clazz);
//    }


    private static void registerAll() {

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


        register("model/illager", AbstractSkinnableModels.ILLAGER);
        register("model/zombie_villager", AbstractSkinnableModels.ZOMBIE_VILLAGER);

        register("model/villager", AbstractSkinnableModels.VILLAGER);
        register("model/iron_golem", AbstractSkinnableModels.IRON_GOLEM);
        register("model/enderman", AbstractSkinnableModels.ENDERMAN);

        register("model/player", AbstractSkinnableModels.PLAYER);
        register("model/humanoid", AbstractSkinnableModels.HUMANOID);

        register("model/slime", AbstractSkinnableModels.SLIME);
        register("model/ghast", AbstractSkinnableModels.GHAST);
        register("model/chicken", AbstractSkinnableModels.CHICKEN);
        register("model/creeper", AbstractSkinnableModels.CREEPER);

        register("model/boat", AbstractSkinnableModels.BOAT);
        register("model/raft", AbstractSkinnableModels.RAFT);

        register("model/allay", AbstractSkinnableModels.ALLAY);
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


