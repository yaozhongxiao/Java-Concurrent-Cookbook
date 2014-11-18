/**
 * @author zhongxiao.yzx
 * Topic 8. 执行者定期的执行任务
 */

package com.concurrent.example.Executor;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//1.创建Task类，并指定它实现Callable接口。
class PeriodicTask implements Runnable {
  // 2.声明一个私有的、类型为String、名为name的属性，用来存储任务的名称。
  private String name;
  // 3.实现Task类的构造器，初始化name属性。
  public PeriodicTask(String name) {
      this.name = name;
  }
  // 4.实现call()方法，写入实际日期到控制台，检查任务在指定的时间内执行。
  public void run() {
      System.out.printf("%s, %s: Starting at : %s\n", Thread.currentThread().getName(),name, new Date());
  }
}
//5.实现示例的主类，创建Main类，实现main()方法。
public class PeriodicSchedule {
  public static void main(String[] args) {
      // 6.使用Executors类的newScheduledThreadPool()方法，
      // 创建ScheduledThreadPoolExecutor。传入参数1给这个方法。
      ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
      // 7.写入实际日期到控制台。
      System.out.printf("Main: Starting at: %s\n", new Date());
      // 8.创建一个新的Task对象。
      PeriodicTask task = new PeriodicTask("Task");
      // 9.使用scheduledAtFixRate()方法把它提交给执行者。使用前面创建的任务，数字1，数字2
//和常量TimeUnit.SECONDS作为参数。这个方法返回ScheduledFuture对象，
//它可以用来控制任务的状态。
      ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 1, 2,TimeUnit.SECONDS);
      // 10.创建10个循环步骤，写入任务下次执行的剩余时间。在循环中，
//使用ScheduledFuture对象的getDelay()方法，获取任务下次执行的毫秒数。
      for (int i = 0; i < 10; i++) {
          System.out.printf("Main: Delay: %d\n", result.getDelay(TimeUnit.MILLISECONDS));
          // 线程睡眠500毫秒
          try {
              TimeUnit.MILLISECONDS.sleep(500);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
      // 11.使用shutdown()方法关闭执行者。
      executor.shutdown();
      // 12.使线程睡眠5秒，检查周期性任务是否完成。
      try {
          TimeUnit.SECONDS.sleep(5);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      // 13.写入一条信息到控制台，表明程序结束。
      System.out.printf("Main: Finished at: %s\n", new Date());
  }
}

