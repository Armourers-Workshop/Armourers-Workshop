package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.core.AbstractConfigSpec;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeConfigSpec;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.platform.Platform;

public class ConfigBuilderImpl implements Platform.Selector<IConfigSpec, IConfigSpec> {

    @Override
    public IConfigSpec client() {
        return AbstractForgeConfigSpec.create(AbstractConfigSpec.Type.CLIENT, proxy -> new ModConfigSpec.Client() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }

    @Override
    public IConfigSpec common() {
        return AbstractForgeConfigSpec.create(AbstractConfigSpec.Type.COMMON, proxy -> new ModConfigSpec.Common() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }
}
