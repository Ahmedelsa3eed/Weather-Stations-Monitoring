package org.example.thread_pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadOwner {
    static private ThreadOwner obj;
    private ExecutorService executorService;
    private ThreadOwner() {
        executorService = Executors.newCachedThreadPool();
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
