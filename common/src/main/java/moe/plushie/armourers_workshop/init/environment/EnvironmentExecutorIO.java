package moe.plushie.armourers_workshop.init.environment;

import com.apple.library.impl.KeyboardManagerImpl;

public class EnvironmentExecutorIO {

    public static boolean hasShiftDown() {
        return EnvironmentExecutor.call(() -> KeyboardManagerImpl::hasShiftDown, () -> () -> false);
    }

    public static boolean hasControlDown() {
        return EnvironmentExecutor.call(() -> KeyboardManagerImpl::hasControlDown, () -> () -> false);
    }

    public static boolean hasSprintDown() {
        return EnvironmentExecutor.call(() -> KeyboardManagerImpl::hasSprintDown, () -> () -> false);
    }

    public static boolean hasSneakDown() {
        return EnvironmentExecutor.call(() -> KeyboardManagerImpl::hasSneakDown, () -> () -> false);
    }
}
