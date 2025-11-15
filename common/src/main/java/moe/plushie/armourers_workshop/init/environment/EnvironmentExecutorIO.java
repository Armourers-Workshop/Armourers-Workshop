package moe.plushie.armourers_workshop.init.environment;

import com.apple.library.impl.InputManagerImpl;

public class EnvironmentExecutorIO {

    public static boolean hasAltDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasAltDown, () -> () -> false);
    }

    public static boolean hasShiftDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasShiftDown, () -> () -> false);
    }

    public static boolean hasControlDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasControlDown, () -> () -> false);
    }
}
