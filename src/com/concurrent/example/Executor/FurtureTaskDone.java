/**
 * @author zhongxiao.yzx
 * Topic 10. 执行者控制一个任务完成 
 */

package com.concurrent.example.Executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

//1.创建ExecutableTask类，并指定其实现Callable接口，参数化为String类型。
class ExecutableTask implements Callable<String> {
    // 2.声明一个私有的、类型为String、名为name的属性，用来存储任务的名称。
    //   实现getName()方法，返回这个属性值。
    private String name;
    public String getName() {
        return name;
    }
    // 3.实现这个类的构造器，初始化任务的名称。
    public ExecutableTask(String name) {
        this.name = name;
    }
    // 4.实现call()方法。使这个任务睡眠一个随机时间，返回任务名称的信息。
    // call返回的结果表示任务成功完成与否
    // {返回有效数据，即使Executor执行cancel操作触发中断，cancel获得的返回结果仍然表示cancel操作失败}
    public String call() throws Exception {
        try {
            long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n",this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.printf("%s: is cancelled.\n",this.name);
            return null;
        }
        return "Hello, world. I'm " + name;
    }
}
// 5.实现ResultTask类，继承FutureTask类，参数化为String类型。
class ResultTask extends FutureTask<String> {
    // 6.声明一个私有的、类型为String、名为name的属性，用来存储任务的名称。
    public String name;
    // 7.实现这个类的构造器。它接收一个Callable对象参数。调用父类构造器，使用接收到的任务的属性初始化name属性。
    public ResultTask(Callable<String> callable) {
        super(callable);
        this.name = ((ExecutableTask) callable).getName();
    }
    // 8.重写done()方法。检查isCancelled()方法返回值，并根据这个返回值的不同，写入不同的信息到控制台。
    protected void done() {
        if (isCancelled()) {
            System.out.printf("%s: Has been canceled\n", name);
        } else {
            System.out.printf("%s: Has finished\n", name);
        }
    }
}

// 9.实现示例的主类，创建Main类，实现main()方法。
public class FurtureTaskDone {
    public static void main(String[] args) {
        // 10.使用Executors类的newCachedThreadPool()方法创建ExecutorService。
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();
        // 11.创建存储5个ResultTask对象的一个数组。
        ResultTask resultTasks[] = new ResultTask[5];
        // 12.初始化ResultTask对象。对于数据的每个位置，
        // 首先，你必须创建ExecutorTask，
        // 然后，ResultTask使用这个对象，
        // 然后submit()方法提交ResultTask给执行者。
        for (int i = 0; i < 5; i++) {
            ExecutableTask executableTask = new ExecutableTask("Task " + i);
            resultTasks[i] = new ResultTask(executableTask);
            executor.submit(resultTasks[i]);
        }
        // 13.令主线程睡眠5秒。
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        boolean cancel_op = false;
        // 14.取消你提交给执行者的所有任务。
        for (int i = 0; i < resultTasks.length; i++) {
            cancel_op = resultTasks[i].cancel(true);
            System.out.printf("%s cancel success %s\n", resultTasks[i].name,cancel_op);
        }
        // 15.将没有被使用ResultTask对象的get()方法取消的任务的结果写入到控制台。
        for (int i = 0; i < resultTasks.length; i++) {
            try {
                if (!resultTasks[i].isCancelled()) {
                    System.out.printf("%s\n", resultTasks[i].get());
                }
            } catch (InterruptedException | ExecutionException e) {
                //e.printStackTrace();
            }
        }
        // 16.使用shutdown()方法关闭执行者。
        executor.shutdown();
    }
}
