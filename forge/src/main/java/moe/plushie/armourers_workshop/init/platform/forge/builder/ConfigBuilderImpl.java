package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compatibility.AbstractConfigSpec;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeConfigSpec;
import moe.plushie.armourers_workshop.init.ModConfigSpec;

public class ConfigBuilderImpl {

    public static IConfigSpec createClientSpec() {
        return AbstractForgeConfigSpec.create(AbstractConfigSpec.Type.CLIENT, proxy -> new ModConfigSpec.Client() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }

    public static IConfigSpec createCommonSpec() {
        return AbstractForgeConfigSpec.create(AbstractConfigSpec.Type.COMMON, proxy -> new ModConfigSpec.Common() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }
}
