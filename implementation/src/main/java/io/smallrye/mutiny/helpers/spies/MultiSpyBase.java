package io.smallrye.mutiny.helpers.spies;

import java.util.concurrent.atomic.AtomicLong;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.AbstractMultiOperator;

abstract class MultiSpyBase<T> extends AbstractMultiOperator<T, T> {

    private AtomicLong invocationCount = new AtomicLong();

    protected void incrementInvocationCount() {
        invocationCount.incrementAndGet();
    }

    public long invocationCount() {
        return invocationCount.get();
    }

    public boolean invoked() {
        return invocationCount() > 0;
    }

    MultiSpyBase(Multi<? extends T> upstream) {
        super(upstream);
    }
}
