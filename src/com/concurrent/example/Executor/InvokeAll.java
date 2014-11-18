/**
 * @author zhongxiao.yzx
 * Topic 6. 运行多个任务并处理所有的结果
 */

package com.concurrent.example.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//1.创建Result类，存储这个示例中并发任务产生的结果。
class Result {
    // 2.声明两个私有属性。一个String属性，名为name，另一个int属性，名为value。
    private String name;
    private int value;

    // 3.实现相应的get()和set()方法，用来设置和获取name和value属性的值。
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

// 4.创建Task类，实现Callable接口，参数化为Result类型。
class InvokeAll_Task implements Callable<Result> {
    // 5.声明一个私有String属性，名为name。
    private String name;

    // 6.实现Task类构造器，初始化这个属性。
    public InvokeAll_Task(String name) {
        this.name = name;
    }

    // 7.实现这个类的call()方法，在本例中，它将返回一个Result对象。

    @Override
    public Result call() throws Exception {
        // 8.首先，写入一个信息到控制台，表明任务开始。
        System.out.printf("%s: Staring\n", this.name);
        // 9.然后，等待一个随机时间。
        try {
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n",this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 10.在Result对象中返回一个计算5个随机数的总和的int值。
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value += (int) (Math.random() * 100);
        }
        // 11.创建Result对象，用任务的名称和前面操作结果来初始化它。
        Result result = new Result();
        result.setName(this.name);
        result.setValue(value);
        // 12.写入一个信息到控制台，表明任务已经完成。
        System.out.println(this.name + ": Ends");
        // 13.返回Result对象。
        return result;
    }
}

// 14.最后，实现这个示例的主类，创建Main类，实现main()方法。
public class InvokeAll {
    public static void main(String[] args) {
        // 15.使用Executors类的newCachedThreadPool()方法，创建ThreadPoolExecutor对象。
        ExecutorService executor = (ExecutorService) Executors
                .newCachedThreadPool();
        // 16.创建Task对象列表。创建3个Task对象并且用这个列表来存储。
        List<InvokeAll_Task> taskList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            InvokeAll_Task task = new InvokeAll_Task(i + "");
            taskList.add(task);
        }
        // 17.创建Future对象列表，参数化为Result类型。
        List<Future<Result>> resultList = null;
        // 18.调用ThreadPoolExecutor类的invokeAll()方法。这个类将会返回之前创建的Future对象列表。
        try {
            resultList = executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("=====================");

        // 19.使用shutdown()方法结束执行者。
        executor.shutdown();
        // 20.写入处理Future对象列表任务的结果。
        System.out.println("Main: Printing the results");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Result> future = resultList.get(i);
            try {
                Result result = future.get();
                System.out.println(result.getName() + ": " + result.getValue());

            } catch (InterruptedException | ExecutionException e) {

                e.printStackTrace();

            }
        }
    }
}
