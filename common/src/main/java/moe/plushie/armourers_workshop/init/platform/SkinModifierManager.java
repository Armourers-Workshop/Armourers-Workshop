package moe.plushie.armourers_workshop.init.platform;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.data.IDataPackBuilder;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.armature.ArmatureManager;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmatureManager;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightArmatureManager;
import moe.plushie.armourers_workshop.core.data.DataPackLoader;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class SkinModifierManager {

    public static final DefaultArmatureManager DEFAULT = new DefaultArmatureManager();
    public static final EpicFlightArmatureManager EPICFIGHT = new EpicFlightArmatureManager();

    private static final ImmutableMap<String, ArmatureManager> MANAGERS = ImmutableMap.<String, ArmatureManager>builder()
            .put("armourers_workshop:armature", DEFAULT)
            .put("epicfight:armature", EPICFIGHT)
            .build();

    public static void init() {
        DataPackManager.register(new DataPackLoader("skin/modifiers", SimpleLoader::new, SimpleLoader::clean, SimpleLoader::freeze));
    }

    public static class SimpleLoader implements IDataPackBuilder {

        private final ResourceLocation location;

        public SimpleLoader(ResourceLocation location) {
            this.location = location;
        }

        @Override
        public void append(IDataPackObject object, ResourceLocation file) {
            String type = object.get("type").stringValue();
            ArmatureManager manager = MANAGERS.get(type);
            if (manager != null) {
                manager.append(object, location);
            }
        }

        @Override
        public void build() {
        }

        public static void clean() {
            MANAGERS.values().forEach(ArmatureManager::clear);
        }

        public static void freeze() {
            MANAGERS.values().forEach(ArmatureManager::freeze);
        }
    }
}


