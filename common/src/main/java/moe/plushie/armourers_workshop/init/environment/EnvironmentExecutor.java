package moe.plushie.armourers_workshop.init.environment;

import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnvironmentExecutor {

    private static final Manager WILL_INIT = new Manager();
    private static final Manager DID_INIT = new Manager();

    private static final Manager WILL_SETUP = new Manager();
    private static final Manager DID_SETUP = new Manager();

    public synchronized static void willInit(EnvironmentType type) {
        WILL_INIT.run(type);
    }

    public synchronized static void willInit(EnvironmentType type, Supplier<Runnable> task) {
        WILL_INIT.add(type, task);
    }

    public synchronized static void didInit(EnvironmentType type) {
        DID_INIT.run(type);
    }

    public synchronized static void didInit(EnvironmentType type, Supplier<Runnable> task) {
        DID_INIT.add(type, task);
    }

    public synchronized static <T> void didInit(EnvironmentType type, Supplier<Consumer<T>> task, T value) {
        if (task != null && value != null) {
            didInit(type, () -> () -> task.get().accept(value));
        }
    }

    public synchronized static void willSetup(EnvironmentType type) {
        WILL_SETUP.run(type);
    }

    public synchronized static void willSetup(EnvironmentType type, Supplier<Runnable> task) {
        WILL_SETUP.add(type, task);
    }

    public synchronized static void didSetup(EnvironmentType type) {
        DID_SETUP.run(type);
    }

    public synchronized static void didSetup(EnvironmentType type, Supplier<Runnable> task) {
        DID_SETUP.add(type, task);
    }

    public static <T> T call(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (EnvironmentManager.getEnvironmentType() == EnvironmentType.CLIENT) {
            return clientSupplier.get().get();
        }
        return serverSupplier.get().get();
    }

    public static <T> Optional<T> callOn(EnvironmentType envType, Supplier<Supplier<T>> supplier) {
        if (EnvironmentManager.getEnvironmentType() == envType) {
            return Optional.ofNullable(supplier.get().get());
        }
        return Optional.empty();
    }

    public static void run(Supplier<Runnable> clientSupplier, Supplier<Runnable> serverSupplier) {
        if (EnvironmentManager.getEnvironmentType() == EnvironmentType.CLIENT) {
            clientSupplier.get().run();
        } else {
            serverSupplier.get().run();
        }
    }

    public static void runOn(EnvironmentType envType, Supplier<Runnable> supplier) {
        if (EnvironmentManager.getEnvironmentType() == envType) {
            supplier.get().run();
        }
    }

    public static void runOnBackground(Supplier<Runnable> handler) {
        Util.backgroundExecutor().execute(handler.get());
    }

    protected static class Manager {

        private final HashSet<EnvironmentType> status = new HashSet<>();
        private final HashMap<EnvironmentType, ArrayList<Supplier<Runnable>>> tasks = new HashMap<>();

        public synchronized void add(EnvironmentType type, Supplier<Runnable> task) {
            if (task == null) {
                return;
            }
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
