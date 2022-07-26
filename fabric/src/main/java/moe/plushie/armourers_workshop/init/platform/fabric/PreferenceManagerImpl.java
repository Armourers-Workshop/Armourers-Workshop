package moe.plushie.armourers_workshop.init.platform.fabric;

import com.electronwill.nightconfig.core.ConfigSpec;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PreferenceManagerImpl {

    public static Map<String, Object> getServerEntries() {
        return new HashMap<>();
    }

    public static void setServerEntries(Map<String, Object> entries) {
    }

    public static void init() {
        Class<?> z = ConfigSpec.class;
        // TODO: @SAGESSE
    }

    public static void save() {
    }
}
