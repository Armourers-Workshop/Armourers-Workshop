package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.common.ServerStartingEvent;
import moe.plushie.armourers_workshop.init.event.common.ServerStoppedEvent;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonPlatform;
import net.minecraft.server.MinecraftServer;

import java.util.function.Supplier;

public abstract class PlatformManager implements Platform {

    protected static final PlatformManager INSTANCE = PlatformLoader.load(PlatformManager.class);

    protected MinecraftServer currentServer;

    protected final CommonPlatform commonImpl;
    protected final ClientPlatform clientImpl;

    public PlatformManager(Supplier<Supplier<CommonPlatform>> commonImpl, Supplier<Supplier<ClientPlatform>> clientImpl) {
        // create the platform implement by the environment type.
        this.commonImpl = eval(EnvironmentType.COMMON, commonImpl);
        this.clientImpl = eval(EnvironmentType.CLIENT, clientImpl);
        // we must listen to the server event to save the server instance.
        EnvironmentExecutor.willInit(EnvironmentType.COMMON, () -> () -> {
            // prioritize handle.
            EventBus.register(ServerStartingEvent.class, event -> currentServer = event.server());
            EventBus.register(ServerStoppedEvent.class, event -> currentServer = null);
        });
    }

    @Override
    public CommonPlatform common() {
        return commonImpl;
    }

    @Override
    public ClientPlatform client() {
        return clientImpl;
    }

    @Override
    public MinecraftServer currentServer() {
        return currentServer;
    }

    private <T> T eval(EnvironmentType envType, Supplier<Supplier<T>> supplier) {
        if (envType == EnvironmentType.COMMON) {
            return supplier.get().get();
        }
        if (envType == environmentType()) {
            return supplier.get().get();
        }
        return null;
    }
}
