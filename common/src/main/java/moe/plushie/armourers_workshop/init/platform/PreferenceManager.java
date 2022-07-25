package moe.plushie.armourers_workshop.init.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.Map;

public class PreferenceManager {

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Map<String, Object> getServerEntries() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setServerEntries(Map<String, Object> entries) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void save() {
        throw new AssertionError();
    }
}
