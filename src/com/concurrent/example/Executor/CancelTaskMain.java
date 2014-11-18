/**
 * @author zhongxiao.yzx
 * Topic 9. 执行者取消任务 
 */


package com.concurrent.example.Executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//1.创建Task类，指定实现Callable接口，并参数化为String类型。
//  实现call()方法，写入一条信息到控制台，并使这个线程在循环中睡眠100毫秒。
class CancelTask implements Callable<String> {
    public String call() throws Exception {
        int index = 0;
        while (true) {
            System.out.printf("Task: Test %d \n", index++);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "successfully cancelled";
            }
        }
    }
}

// 2.实现示例的主类，创建Main类，实现main()方法。
public class CancelTaskMain {
    public static void main(String[] args) {
        // 3. 使用Executors类的newCachedThreadPool()方法创建ThreadPoolExecutor对象。
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newCachedThreadPool();
        // 4.创建Task对象。
        CancelTask task = new CancelTask();
        // 5.使用submit()方法提交任务给执行者。
        System.out.printf("Main: Executing the Task\n");
        Future<String> result = executor.submit(task);
        // 6.使主任务睡眠2秒。
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 7.使用通过submit()方法返回的Future对象result的cancel()方法，取消任务的执行。
        // 传入true值作为cancel()方法的参数。
        System.out.printf("Main: Canceling the Task\n");
        result.cancel(true);
        //result.cancel(false);
        // 8.将isCancelled()方法和isDone()的调用结果写入控制台，验证任务已取消，因此，已完成。
        System.out.printf("Main: Canceled: %s\n", result.isCancelled());
        System.out.printf("Main: Done: %s\n", result.isDone());
        // 9.使用shutdown()方法结束执行者，写入信息（到控制台）表明程序结束。
        executor.shutdown();
        //executor.shutdownNow();
        System.out.printf("Main: The executor has finished\n");
    }
}