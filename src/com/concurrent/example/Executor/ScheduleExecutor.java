/**
 * @author zhongxiao.yzx
 * Topic 7. 在延迟后执行者运行任务  
 */

package com.concurrent.example.Executor;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//1.创建Task类，实现Callable接口，参数化为String类型。
class ScheduleTask implements Callable<String> {
  // 2.声明一个私有的、类型为String、名为name的属性，用来存储任务的名称。
  private String name;
  // 3.实现Task类的构造器，初始化name属性。
  public ScheduleTask(String name) {
      this.name = name;
  }
  // 4.实现call()方法，写入实际日期到控制台，返回一个文本，如：Hello, world。
  public String call() throws Exception {
      System.out.printf("%s %s: Starting at : %s\n", Thread.currentThread().getName(), name, new Date());
      TimeUnit.SECONDS.sleep(1);
      return "Hello, world";
  }
}
//5.实现示例的主类，创建Main类，实现main()方法。
public class ScheduleExecutor {
  public static void main(String[] args) {
      // 6.使用Executors类的newScheduledThreadPool()方法，
      // 创建ScheduledThreadPoolExecutor类的一个执行者。传入参数1。
      ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors
              .newScheduledThreadPool(1);
      // 7.使用ScheduledThreadPoolExecutor实例的schedule()方法，初始化和开始一些任务（本例中5个任务）。
      System.out.printf("Main: Starting at: %s\n", new Date());
      for (int i = 0; i < 15; i++) {
          ScheduleTask task = new ScheduleTask("Task " + i);
          executor.schedule(task, i + 1, TimeUnit.SECONDS);
      }
      // 8.使用shutdown()方法关闭执行者。
      executor.shutdown();
      // 9.使用执行者的awaitTermination()方法，等待所有任务完成。
      try {
          executor.awaitTermination(1, TimeUnit.DAYS);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      // 10.写入一条信息表明程序结束时间。
      System.out.printf("Main: Ends at: %s\n", new Date());
  }
}

