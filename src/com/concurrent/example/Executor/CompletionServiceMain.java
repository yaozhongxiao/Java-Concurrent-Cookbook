/**
 * @author zhongxiao.yzx
 * Topic 11. 执行者分离运行任务和处理结果 
 */

package com.concurrent.example.Executor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//1.创建ReportGenerator类，并指定其实现Callable接口，参数化为String类型。
class ReportGenerator implements Callable<String> {
    // 2.声明两个私有的、String类型的属性，sender和title，用来表示报告的数据。
    private String sender;
    private String title;
    // 3.实现这个类的构造器，初始化这两个属性。
    public ReportGenerator(String sender, String title) {
        this.sender = sender;
        this.title = title;
    }
    // 4.实现call()方法。首先，让线程睡眠一段随机时间。
    public String call() throws Exception {
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s_%s: ReportGenerator:"
                    + "Generating report in %d seconds\n", this.sender,
                    this.title, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 5.然后，生成一个有sender和title属性的字符串的报告，返回这个字符串。
        String ret = sender + ": " + title;
        return ret;
    }
}
// 6.创建ReportRequest类，实现Runnable接口。这个类将模拟一些报告请求。
class ReportRequest implements Runnable {
    // 7.声明私有的、String类型的属性name，用来存储ReportRequest的名称。
    private String name;
    // 8.声明私有的、CompletionService类型的属性service。CompletionService接口是个参数化接口，使用String类型参数化它。
    private CompletionService<String> service;
    // 9.实现这个类的构造器，初始化这两个属性。
    public ReportRequest(String name, CompletionService<String> service) {
        this.name = name;
        this.service = service;
    }
    // 10.实现run()方法。创建1个ReportGenerator对象，并使用submit()方法把它提交给CompletionService对象。
    public void run() {
        ReportGenerator reportGenerator = new ReportGenerator(name, "Report");
        service.submit(reportGenerator);
    }
}
// 11.创建ReportProcessor类。这个类将获取ReportGenerator任务的结果，指定它实现Runnable接口。
class ReportProcessor implements Runnable {
    // 12.声明一个私有的、CompletionService类型的属性service。
    // 由于CompletionService接口是个参数化接口，使用String类作为这个CompletionService接口的参数。
    private CompletionService<String> service;
    // 13.声明一个私有的、boolean类型的属性end。
    private boolean finish;
    // 14.实现这个类的构造器，初始化这两个属性。
    public ReportProcessor(CompletionService<String> service) {
        this.service = service;
        finish = false;
    }
    // 15.实现run()方法。当属性end值为false，调用CompletionService接口的poll()方法
    //    获取CompletionService执行的下个已完成任务的Future对象。
    public void run() {
        while (!finish) {
            try {
                Future<String> result = service.poll(20, TimeUnit.SECONDS);
                // 16.然后，使用Future对象的get()方法获取任务的结果，并且将这些结果写入到控制台。
                if (result != null) {
                    String report = result.get();
                    System.out.printf("ReportReceiver: Report Received:%s\n",report);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("ReportSender: End\n");
    }

    // 17.实现setEnd()方法，用来修改属性end的值。
    public void setEnd(boolean end) {
        this.finish = end;
    }
}

// 18.实现这个示例的主类，通过创建Main类，并实现main()方法。
public class CompletionServiceMain {
    public static void main(String[] args) {
        // 19.使用Executors类的newCachedThreadPool()方法创建ThreadPoolExecutor。
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();
        // 20.创建CompletionService，使用前面创建的执行者作为构造器的参数。
        CompletionService<String> service = new ExecutorCompletionService<>(executor);
        // 21.创建两个ReportRequest对象，并用线程执行它们。
        ReportRequest faceRequest = new ReportRequest("Face", service);
        ReportRequest onlineRequest = new ReportRequest("Online", service);
        Thread faceThread = new Thread(faceRequest);
        Thread onlineThread = new Thread(onlineRequest);
        // 22.创建一个ReportProcessor对象，并用线程执行它。
        ReportProcessor processor = new ReportProcessor(service);
        Thread senderThread = new Thread(processor);
        // 23.启动这3个线程。
        System.out.printf("Main: Starting the Threads\n");
        faceThread.start();
        onlineThread.start();
        senderThread.start();
        // 24.等待ReportRequest线程的结束。
        try {
            System.out.printf("Main: Waiting for the report generators.\n");
            faceThread.join();
            onlineThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 25.使用shutdown()方法关闭执行者，使用awaitTermination()方法等待任务的结果。
        System.out.printf("Main: Shutting down the executor.\n");
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 26.设置ReportSender对象的end属性值为true，结束它的执行。
        processor.setEnd(true);
        System.out.println("Main: Ends");
    }
}
