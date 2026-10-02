package com.golajugaenyang.product.application.inventory.concurrency;

public record ConcurrentOutcome(
    boolean success,
    Exception exception
) {


    public static ConcurrentOutcome succeeded() {
        return new ConcurrentOutcome(true, null);
    }

    public static ConcurrentOutcome failed(Exception e) {
        return new ConcurrentOutcome(false, e);
    }
}
