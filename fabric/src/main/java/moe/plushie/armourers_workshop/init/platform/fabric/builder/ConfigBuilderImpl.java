package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.core.AbstractConfigSpec;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricConfigSpec;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.platform.Platform;

public class ConfigBuilderImpl implements Platform.Selector<IConfigSpec, IConfigSpec> {

    @Override
    public IConfigSpec client() {
        return AbstractFabricConfigSpec.create(AbstractConfigSpec.Type.CLIENT, proxy -> new ModConfigSpec.Client() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }

    @Override
    public IConfigSpec common() {
        return AbstractFabricConfigSpec.create(AbstractConfigSpec.Type.COMMON, proxy -> new ModConfigSpec.Common() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }
}
