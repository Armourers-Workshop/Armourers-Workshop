package moe.plushie.armourers_workshop.init.platform.environment;

import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnvironmentExecutor {

    private static final HashSet<EnvironmentType> STATUS = new HashSet<>();
    private static final HashMap<EnvironmentType, ArrayList<Supplier<Runnable>>> SETUP_TASKS = new HashMap<>();

    public synchronized static void setupOn(EnvironmentType type, Supplier<Runnable> task) {
        // when the setup did complete, direct call the task.
        if (STATUS.contains(type)) {
            task.get().run();
            return;
        }
        SETUP_TASKS.computeIfAbsent(type, k -> new ArrayList<>()).add(task);
    }

    public synchronized static <T> void setupOn(EnvironmentType type, Supplier<Consumer<T>> task, Supplier<T> value) {
        if (task == null || value == null) {
            return;
        }
        setupOn(type, () -> () -> task.get().accept(value.get()));
    }

    public synchronized static void setup(EnvironmentType type) {
        ArrayList<Supplier<Runnable>> tasks = SETUP_TASKS.remove(type);
        STATUS.add(type);
        if (tasks != null) {
            tasks.forEach(task -> task.get().run());
        }
    }

    public static <T> Optional<T> callWhenOn(EnvironmentType envType, Supplier<Supplier<T>> supplier) {
        if (EnvironmentManager.getEnvironmentType() == envType) {
            return Optional.ofNullable(supplier.get().get());
        }
        return Optional.empty();
    }

    public static void runWhenOn(EnvironmentType envType, Supplier<Runnable> supplier) {
        if (EnvironmentManager.getEnvironmentType() == envType) {
            supplier.get().run();
        }
    }

    public static <T> T call(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (EnvironmentManager.getEnvironmentType() == EnvironmentType.CLIENT) {
            return clientSupplier.get().get();
        }
        return serverSupplier.get().get();
    }

    public static void run(Supplier<Runnable> clientSupplier, Supplier<Runnable> serverSupplier) {
        if (EnvironmentManager.getEnvironmentType() == EnvironmentType.CLIENT) {
            clientSupplier.get().run();
        } else {
            serverSupplier.get().run();
        }
    }
}
