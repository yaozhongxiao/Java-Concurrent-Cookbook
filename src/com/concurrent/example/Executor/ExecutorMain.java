/**
 * @author zhongxiao.yzx
 * Topic 2. 创建一个线程执行者 
 * Topic 3. 创建一个大小固定的线程执行者 
 */

package com.concurrent.example.Executor;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Task implements Runnable {
    private Date initDate;
    public String name;

    public Task(String name) {
        initDate = new Date();
        this.name = name;
    }

    public void run() {
        System.out.printf("%s: Task %s: Created on: %s\n", Thread
                .currentThread().getName(), name, initDate);
        System.out.printf("%s: Task %s: Started on: %s\n", Thread
                .currentThread().getName(), name, new Date());
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Task %s: Doing a task during %dseconds\n",
                    Thread.currentThread().getName(), name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Task %s: Finished on: %s\n", Thread
                .currentThread().getName(), name, new Date());
    }
}

class Server {
    private ThreadPoolExecutor executor;
    public Server() {
        // executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    public void ServerStatus(){
        System.out.printf("Server: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n",
                executor.getActiveCount());
        System.out.printf("Server: Commit Tasks: %d\n",executor.getTaskCount());
        System.out.printf("Server: Completed Tasks: %d\n",
                executor.getCompletedTaskCount());
    }
    public void waitAll(){
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void executeTask(Task task) {
        System.out.printf("Server: A new task has arrived %s\n",task.name);
        executor.execute(task);
        ServerStatus();
    }

    public void endServer() {
        executor.shutdown();
    }
}

public class ExecutorMain {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server();
        for (int i = 0; i < 20; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }
       
        server.waitAll();
        
        server.endServer();
        
        System.out.printf("=======================");
      
        server.ServerStatus();
        System.out.printf("main thread exit...");
        
    }
}
