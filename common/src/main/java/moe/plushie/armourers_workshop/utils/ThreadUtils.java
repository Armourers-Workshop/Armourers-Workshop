package moe.plushie.armourers_workshop.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtils {

    private final static ConcurrentHashMap<Class<?>, AtomicInteger> IDS = new ConcurrentHashMap<>();

    public static ExecutorService newFixedThreadPool(int nThreads, String name) {
        return newFixedThreadPool(nThreads, name, Thread.NORM_PRIORITY);
    }

    public static ExecutorService newFixedThreadPool(int nThreads, String name, int priority) {
        return Executors.newFixedThreadPool(nThreads, task -> {
            Thread thread = new Thread(task, name);
            thread.setPriority(priority);
            thread.setDaemon(true);
            return thread;
        });
    }

    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(nThreads, threadFactory);
    }

//    public static ExecutorService newCachedThreadPool(String name) {
//        return newCachedThreadPool(name, Thread.NORM_PRIORITY);
//    }
//
//    public static ExecutorService newCachedThreadPool(String name, int priority) {
//        return Executors.newCachedThreadPool(task -> {
//            Thread thread = new Thread(task, name);
//            thread.setPriority(priority);
//            thread.setDaemon(true);
//            return thread;
//        });
//    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    public static int nextId(Class<?> clazz) {
        return IDS.computeIfAbsent(clazz, k -> new AtomicInteger()).incrementAndGet();
    }
}
