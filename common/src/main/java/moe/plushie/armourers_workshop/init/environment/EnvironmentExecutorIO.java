package moe.plushie.armourers_workshop.init.environment;

import com.apple.library.impl.InputManagerImpl;

public class EnvironmentExecutorIO {

    public static boolean hasShiftDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasShiftDown, () -> () -> false);
    }

    public static boolean hasControlDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasControlDown, () -> () -> false);
    }

    public static boolean hasSprintDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasSprintDown, () -> () -> false);
    }

    public static boolean hasSneakDown() {
        return EnvironmentExecutor.call(() -> InputManagerImpl::hasSneakDown, () -> () -> false);
    }
}
