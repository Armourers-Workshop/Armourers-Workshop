package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compat.core.AbstractPlatform;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonPlatform;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public interface Platform {

    static Platform get() {
        return PlatformManager.INSTANCE;
    }

    PlatformType platformType();

    EnvironmentType environmentType();

    boolean isDevelopment();


    CommonPlatform common();

    ClientPlatform client();


    Selector<IConfigSpec, IConfigSpec> config();


    Path gameDir();


    MinecraftServer currentServer();

    default Executor backgroundExecutor() {
        return AbstractPlatform.backgroundExecutor();
    }

    interface Selector<A, B> {
        A common();

        B client();
    }
}
