package moe.plushie.armourers_workshop.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class Executors {

    public static ExecutorService newFixedThreadPool(int nThreads, String name) {
        return newFixedThreadPool(nThreads, name, Thread.NORM_PRIORITY);
    }

    public static ExecutorService newFixedThreadPool(int nThreads, String name, int priority) {
        return java.util.concurrent.Executors.newFixedThreadPool(nThreads, task -> {
            Thread thread = new Thread(task, name);
            thread.setPriority(priority);
            thread.setDaemon(true);
            return thread;
        });
    }

    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return java.util.concurrent.Executors.newFixedThreadPool(nThreads, threadFactory);
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
        return java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }
    }
}
