package com.itdemo.gulimail.search.thread;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("------main----start");
//        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
//            System.out.println(Thread.currentThread().getName() + "开始执行");
//            int i = 2 + 5;
//            System.out.println("运行结果：" + i);
//        }, executorService);
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println(Thread.currentThread().getName() + "开始执行");
//            int i = 2 + 5;
//            return i;
//            /**
//             * whenComplete()只能感知结果和异常
//             * */
//        }, executorService).whenComplete((res, exception) -> {
//            System.out.println("运算结果为：" + res + ",运算异常原因为：" + exception);
//            /**
//             * handle()可对结果进行处理 无论成功或异常
//             * */
//        }).handle((res, throwable) -> {
//            if (res != null) {
//                return res * 2;
//            }
//            if (throwable != null) {
//                return 0;
//            }
//            return 0;
//        });
//        Integer integer = future.get();
        /**
         * 线程串行化处理
         * 1> thenRunAsync() 没有感知上一个线程的处理结果
         * 2> thenAcceptAsync() 可以拿到上一个线程的处理结果但无返回值
         * 3> thenApplyAsync() 可以拿到上一个线程的处理结果且有返回值
         * */
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "任务一开始执行");
            int i = 2 + 5;
            System.out.println("任务一结束");
            return i;
        });

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "任务二开始执行");
            String s = "Hello";
            System.out.println("任务二结束");
            return s;
        });

        /**
         * 两个任务都完成
         * */
//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务三开始");
//        },executorService);

//        future01.thenAcceptBothAsync(future02,(t,r)->{
//            System.out.println("任务三结束--"+t+"---"+r);
//        },executorService);

        CompletableFuture<String> future03 = future01.thenCombineAsync(future02, (t, r) -> {
            System.out.println("任务三开始");
            return t +":"+ r + "->Haha";
        }, executorService);
        System.out.println(future03.get());

        System.out.println("------main------end");

    }

    public static void thread() {
        FutureTask<Integer> futureTask = new FutureTask<>(new callable01());
        new Thread(futureTask).start();
        try {
            Integer integer = futureTask.get();
            System.out.println("integer----"+integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public static class callable01 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            System.out.println(Thread.currentThread().getName()+"开始执行");
            int i = 2 + 5;
            System.out.println("运行结果："+i);
            return i;
        }
    }
}
