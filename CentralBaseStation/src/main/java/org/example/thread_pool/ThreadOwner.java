package org.example.thread_pool;

import java.util.concurrent.*;

public class ThreadOwner {
    static private ThreadOwner obj;
    private ThreadPoolExecutor executorService;
    private ThreadOwner() {
        int corePoolSize = 5; // Number of threads to keep alive in the pool
        int maxPoolSize = 10; // Maximum number of threads in the pool
        long keepAliveTime = 60L; // Keep alive time for idle threads
        TimeUnit timeUnit = TimeUnit.SECONDS; // Time unit for keep alive time
        int queueCapacity = 5; // Maximum number of tasks that can be queued

        executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                timeUnit,
                new ArrayBlockingQueue<>(queueCapacity)
        );

        // Set the handler for rejected tasks
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

    }
 
    public synchronized static ThreadOwner getInstance()
    {
        if (obj==null)
            obj = new ThreadOwner();
        return obj;
    }
    public synchronized void addThrea(Runnable function){
        executorService.submit(()->function.run());
    }
}
