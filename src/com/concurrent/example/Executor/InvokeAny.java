/**
 * @author zhongxiao.yzx
 * Topic 5. 运行多个任务并处理第一个结果  
 */

package com.concurrent.example.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//1.创建UserValidator类，实现用户验证过程。
class UserValidator {
    // 2.声明一个私有的、类型为String、名为name的属性，用来存储系统验证用户的名称。
    private String name;

    // 3.实现UserValidator类的构造器，初始化这个属性。
    public UserValidator(String name) {
        this.name = name;
    }

    // 4.实现validate()方法。接收你想要验证用户的两个String类型参数，一个为name，一个为password。
    public boolean validate(String name, String password) {
        // 5.创建Random对象，名为random。
        Random random = new Random();
        // 6.等待个随机时间，用来模拟用户验证的过程。
        try {
            long duration = (long) (Math.random() * 10);
            System.out.printf(
                    "Validator %s: Validating a user during %d seconds\n",
                    this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            return false;
        }
        // 7.返回一个随机Boolean值。如果用户验证通过，这个方法将返回true，否则，返回false。
        return random.nextBoolean();
    }

    // 8.实现getName()方法，返回name属性值。
    public String getName() {
        return name;
    }
}

// 9.现在，创建TaskValidator类，用来执行UserValidation对象作为并发任务的验证过程。
//   指定它实现Callable接口，并参数化为String类型。
class TaskValidator implements Callable<String> {
    // 10.声明一个私有的、类型为UserValidator、名为validator的属性。
    private UserValidator validator;
    // 11.声明两个私有的、类型为String、名分别为user和password的属性。
    private String user;
    private String password;

    // 12.实现TaskValidator类，初始化这些属性。
    public TaskValidator(UserValidator validator, String user, String password) {
        this.validator = validator;
        this.user = user;
        this.password = password;
    }

    // 13.实现call()方法，返回一个String类型对象。
    public String call() throws Exception {
        // 14.如果用户没有通过UserValidator对象验证，写入一条信息到控制台，表明这种情况，并且抛出一个Exception异常。
        if (!validator.validate(user, password)) {
            System.out.printf("%s: The user has not been found\n",
                    validator.getName());
            throw new Exception("Error validating user");
        }
        // 15.否则，写入一条信息到控制台表明用户已通过验证，并返回UserValidator对象的名称。
        System.out.printf("%s: The user has been found\n", validator.getName());
        return validator.getName();
    }
}

// 16.现在，实现这个示例的主类，创建Main类，实现main()方法。
public class InvokeAny {
    public static void main(String[] args) {
        // 17.创建两个String对象，一个名为name，另一个名为password，使用”test”值初始化它们。
        String username = "test";
        String password = "test";
        // 18.创建两个UserValidator对象，一个名为ldapValidator，另一个名为dbValidator。
        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DataBase");
        // 19.创建两个TaskValidator对象，分别为ldapTask和dbTask。分别使用ldapValidator和dbValidator初始化它们。

        TaskValidator ldapTask = new TaskValidator(ldapValidator, username,
                password);
        TaskValidator dbTask = new TaskValidator(dbValidator, username,
                password);
        // 20.创建TaskValidator队列，添加两个已创建的对象（ldapTask和dbTask）。
        List<TaskValidator> taskList = new ArrayList<>();
        taskList.add(ldapTask);
        taskList.add(dbTask);
        // 21.使用Executors类的newCachedThreadPool()方法创建一个新的ThreadPoolExecutor对象和一个类型为String，名为result的变量。
        ExecutorService executor = (ExecutorService) Executors
                .newCachedThreadPool();
        String result;
        // 22.调用executor对象的invokeAny()方法。该方法接收taskList参数，返回String类型。同样，它将该方法返回的String对象写入到控制台。
        try {
            result = executor.invokeAny(taskList);
            System.out.printf("Main: Result: %s\n", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // 23.使用shutdown()方法结束执行者，写入一条信息到控制台，表明程序已结束。
        executor.shutdown();
        System.out.printf("Main: End of the Execution\n");
    }
}
