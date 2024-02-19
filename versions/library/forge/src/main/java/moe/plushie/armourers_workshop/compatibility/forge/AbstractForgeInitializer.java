package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.proxy.ClientProxyImpl;
import moe.plushie.armourers_workshop.init.platform.forge.proxy.CommonProxyImpl;
import net.neoforged.fml.common.Mod;

@Available("[1.21, )")
@Mod(ModConstants.MOD_ID)
public class AbstractForgeInitializer {

    public AbstractForgeInitializer() {
        CommonProxyImpl.init();
        EnvironmentExecutor.runOn(EnvironmentType.CLIENT, () -> ClientProxyImpl::init);
    }
}
