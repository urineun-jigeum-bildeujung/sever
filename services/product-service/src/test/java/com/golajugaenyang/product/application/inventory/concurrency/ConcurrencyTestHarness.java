package com.golajugaenyang.product.application.inventory.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ConcurrencyTestHarness {

    private ConcurrencyTestHarness() {
    }

    public static List<ConcurrentOutcome> runSimultaneously(List<Runnable> tasks) {
        int n = tasks.size();
        CountDownLatch ready = new CountDownLatch(n);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(n);
        List<ConcurrentOutcome> outcomes = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(n);
        try {
            for (Runnable task : tasks) {
                executor.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        task.run();
                        outcomes.add(ConcurrentOutcome.succeeded());
                    } catch (Exception e) {
                        outcomes.add(ConcurrentOutcome.failed(e));
                    } finally {
                        done.countDown();
                    }
                });
            }

            awaitReadyThenStart(ready, start);
            awaitDone(done);
        } finally {
            start.countDown();
            executor.shutdownNow();
            awaitTerminationQuietly(executor);
        }

        return outcomes;
    }

    private static void awaitReadyThenStart(CountDownLatch ready, CountDownLatch start) {
        try {
            if (!ready.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("일부 작업이 준비 상태에 도달하지 못했습니다.");
            }
            start.countDown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private static void awaitDone(CountDownLatch done) {
        try {
            if (!done.await(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                    "동시 실행 작업이 제한 시간 내에 끝나지 않았습니다. 교착 상태일 수 있습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private static void awaitTerminationQuietly(ExecutorService executor) {
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("[ConcurrencyTestHarness] 일부 워커 스레드가 5초 내에 종료되지 않았습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
