/**
 * @author zhongxiao.yzx
 * Topic 4. 执行者执行返回结果的任务 
 */

package com.concurrent.example.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//1.创建FactorialCalculator类，指定它实现Callable接口，并参数化为Integer类型。
class FactorialCalculator implements Callable<Integer> {
    //2.声明一个私有的，类型为Integer，名为number的属性，用来存储任务将要计算出的数。
    private Integer number;
    //3.实现FactorialCalculator构造器，初始化这个属性。
    public FactorialCalculator(Integer number) {
        this.number = number;
    }

    // 4.实现call()方法。这个方法将返回FactorialCalculator的number属性的阶乘。
    public Integer call() throws Exception {
        // 5.首先，创建和初始化在这个方法中使用的局部变量。
        int result = 1;
        // 6.如果数是1或0，则返回1。否则，计算这个数的阶乘。
        //   出于测试目的，在两次乘之间，令这个任务睡眠20毫秒。
        if ((number == 0) || (number == 1)) {
            result = 1;
        } else {
            for (int i = 2; i <= number; i++) {
                result *= i;
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }
        // 7.操作结果的信息写入控制台。
        System.out.printf("%s: ------%d\n", Thread.currentThread().getName(), result);
        // 8.返回操作结果。
        return result;
        // 9.实现这个示例的主类，创建Main类，实现main()方法。
    }
}

public class CallableMain {
    public static void ExectorStatus(ThreadPoolExecutor executor){
        System.out.printf("Server: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n",
                executor.getActiveCount());
        System.out.printf("Server: Commit Tasks: %d\n",executor.getTaskCount());
        System.out.printf("Server: Completed Tasks: %d\n",
                executor.getCompletedTaskCount());
    }
    public static void main(String[] args) {
        // 10.使用Executors类的newFixedThreadPool()方法创建ThreadPoolExecutor来运行任务。传入参数2。
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(2);
        // 11.创建Future<Integer>对象的数列。
        List<Future<Integer>> resultList = new ArrayList<>();
        // 12.创建Random类产生的随机数。
        Random random = new Random();
        // 13.生成0到10之间的10个随机数。
        for (int i = 0; i < 1; i++) {
            Integer number = random.nextInt(10);
            // 14.创建一个FactorialCaculator对象，传入随机数作为参数。
            FactorialCalculator calculator = new FactorialCalculator(number);
            // 15.调用执行者的submit()方法来提交FactorialCalculator任务给执行者。
            //     这个方法返回Future<Integer>对象来管理任务，并且最终获取它的结果。
            Future<Integer> result = executor.submit(calculator);
            // 16.添加Future对象到之前创建的数列。
            resultList.add(result);
        }
        // 17.创建一个do循环来监控执行者的状态。
        do {
            System.out.printf("status monitor++++++++++++++++++++++");
            ExectorStatus(executor);
            // 18.首先，写入信息到控制台，表明使用执行者的getCompletedTaskNumber()方法获得的已完成的任务数。
            System.out.printf("Main: Number of Completed Tasks:%d\n",
                    executor.getCompletedTaskCount());
            // 19.然后，对于数列中的10个Future对象，使用isDone()方法，将信息写入（到控制台）
            //    表明它们所管理的任务是否已经完成
            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> result = resultList.get(i);
                System.out.printf("Main: Task %d: isDone = %s\n", i, result.isDone());
                System.out.printf("Main: Task %d: isCancelled = %s\n", i, result.isCancelled());
            }
            // 20.令这个线程睡眠50毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 21.如果执行者中的已完成任务数小于10，重复这个循环。
        } while (executor.getCompletedTaskCount() < resultList.size());
        
        // 22.将获得的每个任务的结果写入控制台。
        //    对于每个Future对象，通过它的任务使用get()方法获取返回的Integer对象。
        System.out.printf("Main: all done Results:\n#-----------------------\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            Integer number = null;
            try {
                number = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            // 23.然后，在控制台打印这个数。
            System.out.printf("Main: Task %d: %d\n", i, number);
        }
        // 24.最后，调用执行者的shutdown()方法来结束这个执行者。
        executor.shutdown();
        ExectorStatus(executor);
    }
}
