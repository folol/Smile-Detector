package com.example.devansh.sampleorai;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by devansh on 24/11/17.
 */

public class DefaultExecutorSupplier {

    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor mForBackgroundTasks;
    private static DefaultExecutorSupplier sInstance;

    private DefaultExecutorSupplier() {

        // setting the thread factory
        //ThreadFactory backgroundPriorityThreadFactory = new
        //      PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);

        // setting the thread pool executor for mForBackgroundTasks;
        mForBackgroundTasks = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }

    public static DefaultExecutorSupplier getInstance() {
        if (sInstance == null) {
            synchronized (DefaultExecutorSupplier.class) {
                sInstance = new DefaultExecutorSupplier();
            }
            return sInstance;
        }
        else {
            return sInstance;
        }
    }

    public ThreadPoolExecutor forBackgroundTasks() {
        return mForBackgroundTasks;
    }


}
