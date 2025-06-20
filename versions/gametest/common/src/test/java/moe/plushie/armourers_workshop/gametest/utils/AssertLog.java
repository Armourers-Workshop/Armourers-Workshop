package moe.plushie.armourers_workshop.gametest.utils;

import moe.plushie.armourers_workshop.gametest.init.Logger;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.ExceptionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AssertLog {

    private static final TaskManager TASK_QUEUE = new TaskManager();

    public static void assertPrintLog(String message) {
        TASK_QUEUE.add(event -> message.equals(event.contents()), message, null, true);
    }

    public static void assertPrintLog(String message, Object messageOrSupplier) {
        TASK_QUEUE.add(event -> message.equals(event.contents()), message, messageOrSupplier, true);
    }

    public static void assertPrintLogRE(String re) {
        TASK_QUEUE.add(compile(re), re, null, true);
    }

    public static void assertPrintLogRE(String re, Object messageOrSupplier) {
        TASK_QUEUE.add(compile(re), re, messageOrSupplier, true);
    }

    private static Predicate<Logger.Message> compile(String re) {
        // a*b*c => ^\Qa\E.+\Qb\E.+\Qc\E$
        var pattern = new StringBuilder();
        for (var part : re.split("\\*")) {
            if (pattern.length() != 0) {
                pattern.append(".*");
            }
            pattern.append(Pattern.quote(part));
        }
        var regex = Pattern.compile("^" + pattern + "$");
        return event -> regex.matcher(event.contents()).matches();
    }

    protected static AsynchronousImpl.Executor getExecutor() {
        return TASK_QUEUE;
    }

    private static class Entry {

        private final Predicate<Logger.Message> predicate;
        private final String expected;
        private final Object messageOrSupplier;

        private final Duration timeout;
        private final StackTraceElement[] calledStackTrace;

        private boolean passed = false;
        private CountDownLatch listener;

        private Entry(Predicate<Logger.Message> predicate, Duration timeout, String expected, Object messageOrSupplier) {
            this.predicate = predicate;
            this.expected = expected;
            this.messageOrSupplier = messageOrSupplier;
            this.timeout = timeout;
            this.calledStackTrace = Thread.currentThread().getStackTrace();
        }

        public void prepare(CountDownLatch latch) {
            if (passed) {
                latch.countDown();
            } else {
                listener = latch;
            }
        }

        public void resolve(Logger.Message event) {
            if (passed) {
                return; // the test is passed.
            }
            if (!predicate.test(event)) {
                return; // the message not match.
            }
            passed = true;
            if (listener == null) {
                return; // not complete listener
            }
            listener.countDown();
            listener = null;
        }

        public void check() {
            if (passed) {
                return; // the test is passed
            }
            var error = AssertionFailureBuilder.assertionFailure().message(messageOrSupplier).expected(expected).actual("read timeout of " + timeout).build();
            var stackTrace = calledStackTrace;
            if (stackTrace != null) {
                for (int i = 0; i < stackTrace.length; i++) {
                    if (stackTrace[i].getMethodName().startsWith("assert")) {
                        stackTrace = Arrays.copyOfRange(stackTrace, i, stackTrace.length);
                        break;
                    }
                }
                error.setStackTrace(stackTrace);
            }
            throw error;
        }
    }

    private static class Task {

        private final Duration timeout;
        private final List<Entry> entries = new ArrayList<>();

        public Task(Duration timeout) {
            this.timeout = timeout;
        }

        public void execute() {
            // config the entry
            var latch = new CountDownLatch(entries.size());
            for (var entry : entries) {
                entry.prepare(latch);
            }
            // check in history message.
            var history = Logger.getHistory();
            synchronized (history) {
                for (var event : history) {
                    for (var entry : entries) {
                        entry.resolve(event);
                    }
                }
            }
            try {
                // when all tasks are completed, go through
                if (latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                    return;
                }
            } catch (Throwable ex) {
                ExceptionUtils.throwAsUncheckedException(ex);
            }
            // check all task is complete?
            for (var entry : entries) {
                entry.check();
            }
        }

        public synchronized void add(Predicate<Logger.Message> predicate, String expected, @Nullable Object messageOrSupplier) {
            entries.add(new Entry(predicate, timeout, expected, messageOrSupplier));
        }

        public synchronized void print(Logger.Message event) {
            for (var entry : entries) {
                if (!entry.passed) {
                    entry.resolve(event);
                }
            }
        }
    }

    private static class TaskManager implements AsynchronousImpl.Executor {

        private final Duration timeout = Duration.ofSeconds(90);
        private final ThreadLocal<Task> currentTask = ThreadLocal.withInitial(() -> null);

        private final List<Task> listeners = Collections.synchronizedList(new ArrayList<>());
        private final ExecutorService executor = Executors.newSingleThreadExecutor(action -> {
            var thread = new Thread(action, "jupiter-logger");
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        public TaskManager() {
            Logger.addChangeListener(this::print);
        }

        @Override
        public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
            var task = new Task(timeout);
            listeners.add(task);
            currentTask.set(task);
        }

        @Override
        public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
            var task = currentTask.get();
            if (task == null) {
                throw new RuntimeException("can't found a task!"); // not call before test execution?
            }
            currentTask.remove(); // no more task accepted.
            task.execute();
            listeners.remove(task);
        }

        protected void add(Predicate<Logger.Message> predicate, String expected, @Nullable Object messageOrSupplier, boolean combinable) {
            // when exists a combined task, we only append it.
            if (combinable) {
                var task = currentTask.get();
                if (task != null) {
                    task.add(predicate, expected, messageOrSupplier);
                    return; // add into queue
                }
            }
            // create a new task and execute it now.
            var task = new Task(timeout);
            listeners.add(task);
            task.add(predicate, expected, messageOrSupplier);
            task.execute();
            listeners.remove(task);
        }

        protected void print(Logger.Message event) {
            executor.execute(() -> listeners.forEach(it -> it.print(event)));
        }
    }
}
