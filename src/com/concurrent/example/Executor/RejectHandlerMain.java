/**
 * @author zhongxiao.yzx
 * Topic 12. 执行者控制被拒绝的任务
 */

package com.concurrent.example.Executor;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//1.创建RejectedTaskController类，实现RejectedExecutionHandler接口。
//实现这个接口的rejectedExecution()方法。写入被拒绝任务的名称和执行者的名称与状态到控制台。
class RejectedTaskController implements RejectedExecutionHandler {
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      System.out.printf("RejectedTaskController: The task %s has been rejected\n",
              r.toString());
      System.out.printf("RejectedTaskController: %s\n", executor.toString());
      System.out.printf("RejectedTaskController: Terminating:%s\n",
              executor.isTerminating());
      System.out.printf("RejectedTaksController: Terminated:%s\n",
              executor.isTerminated());
  }
}
//2.实现Task类，实现Runnable接口。
class RejectTask implements Runnable {
  // 3.声明私有的、String类型的属性name， 用来存储任务的名称。
  private String name;
  // 4.实现这个类的构造器，初始化这个类的属性。
  public RejectTask(String name) {
      this.name = name;
  }
  // 5.实现run()方法，写入信息到控制台，表明这个方法开始执行。
  public void run() {
      System.out.println("Task " + name + ": Starting");
      // 6.等待一段随机时间。
      try {
          long duration = (long) (Math.random() * 10);
          System.out.printf("Task %s: ReportGenerator: Generating a report during %d seconds\n",
                          name, duration);
          TimeUnit.SECONDS.sleep(duration);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      // 7.写入信息到控制台，表明方法的结束。
      System.out.printf("Task %s: Ending\n", name);
  }
  // 8.重写toString()方法，返回任务的名称。
  public String toString() {
      return name;
  }
}
//9.实现这个示例的主类，通过创建Main类，并实现main()方法。
public class RejectHandlerMain {
  public static void main(String[] args) {
      // 10.创建一个RejectedTaskController对象，管理拒绝的任务。
      RejectedTaskController controller = new RejectedTaskController();
      // 11.使用Executors类的newCachedThreadPool()方法，创建ThreadPoolExecutor。
      ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
      // 12.建立执行者的拒绝任务控制器。
      executor.setRejectedExecutionHandler(controller);
      // 13.创建任务并提交它们给执行者。
      System.out.printf("Main: Starting.\n");
      for (int i = 0; i < 3; i++) {
          RejectTask task = new RejectTask("Task" + i);
          //executor.submit(task);
          executor.execute(task);
      }
      // 14.使用shutdown()方法，关闭执行者。
      System.out.printf("Main: Shutting down the Executor.\n");
      executor.shutdown();
      // 15.创建其他任务并提交给执行者。
      System.out.printf("Main: Sending another Task.\n");
      RejectTask task = new RejectTask("RejectedTask");
      //executor.submit(task);
      executor.execute(task);
      // 16.写入信息到控制台，表明程序结束。
      System.out.printf("Main: End.\n");
  }
}

