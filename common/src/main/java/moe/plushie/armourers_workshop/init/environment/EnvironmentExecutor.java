package moe.plushie.armourers_workshop.init.environment;

import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnvironmentExecutor {

    private static final Manager SETUP = new Manager();
    private static final Manager FINISH = new Manager();

    public synchronized static void initOn(EnvironmentType type, Supplier<Runnable> task) {
        SETUP.add(type, task);
    }

    public synchronized static <T> void initOn(EnvironmentType type, Supplier<Consumer<T>> task, Supplier<T> value) {
        if (task == null || value == null) {
            return;
        }
        initOn(type, () -> () -> task.get().accept(value.get()));
    }

    public synchronized static void init(EnvironmentType type) {
        SETUP.run(type);
    }

    public synchronized static void loadOn(EnvironmentType type, Supplier<Runnable> task) {
        FINISH.add(type, task);
    }

    public synchronized static void load(EnvironmentType type) {
        FINISH.run(type);
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

    protected static class Manager {

        private final HashSet<EnvironmentType> status = new HashSet<>();
        private final HashMap<EnvironmentType, ArrayList<Supplier<Runnable>>> tasks = new HashMap<>();

        public synchronized void add(EnvironmentType type, Supplier<Runnable> task) {
            // when the setup did complete, direct call the task.
            if (status.contains(type)) {
                task.get().run();
                return;
            }
            tasks.computeIfAbsent(type, k -> new ArrayList<>()).add(task);
        }

        public synchronized void run(EnvironmentType type) {
            ArrayList<Supplier<Runnable>> tasks = this.tasks.remove(type);
            status.add(type);
            if (tasks != null) {
                tasks.forEach(task -> task.get().run());
            }
        }
    }
}
