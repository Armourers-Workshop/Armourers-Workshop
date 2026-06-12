package moe.plushie.armourers_workshop.init.environment;

import moe.plushie.armourers_workshop.init.platform.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnvironmentExecutor {

    private static final EventDispatcher WILL_INIT = new EventDispatcher();
    private static final EventDispatcher DID_INIT = new EventDispatcher();

    private static final EventDispatcher WILL_SETUP = new EventDispatcher();
    private static final EventDispatcher DID_SETUP = new EventDispatcher();

    public static void willInit(EnvironmentType type) {
        WILL_INIT.run(type);
    }

    public static void willInit(EnvironmentType type, Supplier<Runnable> task) {
        WILL_INIT.add(type, task);
    }

    public static void didInit(EnvironmentType type) {
        DID_INIT.run(type);
    }

    public static void didInit(EnvironmentType type, Supplier<Runnable> task) {
        DID_INIT.add(type, task);
    }

    public static <T> void didInit(EnvironmentType type, Supplier<Consumer<T>> task, T value) {
        if (task != null && value != null) {
            didInit(type, () -> () -> task.get().accept(value));
        }
    }

    public static void willSetup(EnvironmentType type) {
        WILL_SETUP.run(type);
    }

    public static void willSetup(EnvironmentType type, Supplier<Runnable> task) {
        WILL_SETUP.add(type, task);
    }

    public static void didSetup(EnvironmentType type) {
        DID_SETUP.run(type);
    }

    public static void didSetup(EnvironmentType type, Supplier<Runnable> task) {
        DID_SETUP.add(type, task);
    }

    public static <T> T call(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (Platform.get().environmentType() == EnvironmentType.CLIENT) {
            return clientSupplier.get().get();
        }
        return serverSupplier.get().get();
    }

    public static <T> Optional<T> callOn(EnvironmentType envType, Supplier<Supplier<T>> supplier) {
        if (Platform.get().environmentType() == envType) {
            return Optional.ofNullable(supplier.get().get());
        }
        return Optional.empty();
    }

    public static <T> Optional<T> callOnClient(Supplier<Supplier<T>> supplier) {
        return callOn(EnvironmentType.CLIENT, supplier);
    }

    public static void run(Supplier<Runnable> clientSupplier, Supplier<Runnable> serverSupplier) {
        if (Platform.get().environmentType() == EnvironmentType.CLIENT) {
            clientSupplier.get().run();
        } else {
            serverSupplier.get().run();
        }
    }

    public static void runOn(EnvironmentType envType, Supplier<Runnable> supplier) {
        if (Platform.get().environmentType() == envType) {
            supplier.get().run();
        }
    }

    public static void runOnClient(Supplier<Runnable> supplier) {
        runOn(EnvironmentType.CLIENT, supplier);
    }

    public static void runOnBackground(Supplier<Runnable> handler) {
        Platform.get().backgroundExecutor().execute(handler.get());
    }

    private static class EventDispatcher {

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
            var tasks = this.tasks.remove(type);
            status.add(type);
            if (tasks != null) {
                tasks.forEach(task -> task.get().run());
            }
        }
    }
}
