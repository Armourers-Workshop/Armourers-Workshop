package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.proxy.ClientProxyImpl;
import moe.plushie.armourers_workshop.init.platform.forge.proxy.CommonProxyImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Available("[1.21, )")
@Mod(ModConstants.MOD_ID)
public class AbstractForgeInitializer {

    private static ModContainer MOD_CONTAINER;
    private static IEventBus MOD_EVENT_BUS;

    public AbstractForgeInitializer(IEventBus modEventBus, ModContainer modContainer) {
        AbstractForgeInitializer.MOD_CONTAINER = modContainer;
        AbstractForgeInitializer.MOD_EVENT_BUS = modEventBus;
        CommonProxyImpl.init();
        EnvironmentExecutor.runOn(EnvironmentType.CLIENT, () -> ClientProxyImpl::init);
    }

    public static IEventBus getEventBus() {
        return NeoForge.EVENT_BUS;
    }

    public static IEventBus getModEventBus() {
        return MOD_EVENT_BUS;
    }

    public static ModContainer getModContainer() {
        return MOD_CONTAINER;
    }
}
