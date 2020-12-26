package io.smallrye.mutiny.operators;

import static io.smallrye.mutiny.helpers.ParameterValidation.nonNull;
import static io.smallrye.mutiny.helpers.ParameterValidation.nonNullNpe;

import java.util.concurrent.Executor;
import java.util.function.Predicate;

import org.reactivestreams.Subscriber;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.*;
import io.smallrye.mutiny.helpers.StrictMultiSubscriber;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.operators.multi.MultiCacheOp;
import io.smallrye.mutiny.operators.multi.MultiEmitOnOp;
import io.smallrye.mutiny.operators.multi.MultiSubscribeOnOp;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.smallrye.mutiny.subscription.MultiSubscriber;

public abstract class AbstractMulti<T> implements Multi<T> {

    public void subscribe(MultiSubscriber<? super T> subscriber) {
        this.subscribe(Infrastructure.onMultiSubscription(this, subscriber));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        if (subscriber instanceof MultiOperator || subscriber instanceof StrictMultiSubscriber) {
            this.subscribe((MultiSubscriber<? super T>) subscriber);
        } else {
            // NOTE The Reactive Streams TCK mandates throwing an NPE.
            nonNullNpe(subscriber, "subscriber");
            this.subscribe(new StrictMultiSubscriber<>(subscriber));
        }
    }

    @Override
    public MultiOnItem<T> onItem() {
        return new MultiOnItem<>(this);
    }

    @Override
    public MultiSubscribe<T> subscribe() {
        return new MultiSubscribe<>(this);
    }

    @Override
    public Uni<T> toUni() {
        return Uni.createFrom().publisher(this);
    }

    @Override
    public MultiOnFailure<T> onFailure() {
        return new MultiOnFailure<>(this, null);
    }

    @Override
    public MultiOnFailure<T> onFailure(Predicate<? super Throwable> predicate) {
        return new MultiOnFailure<>(this, predicate);
    }

    @Override
    public MultiOnFailure<T> onFailure(Class<? extends Throwable> typeOfFailure) {
        return new MultiOnFailure<>(this, typeOfFailure::isInstance);
    }

    @Override
    public MultiOnEvent<T> on() {
        return new MultiOnEvent<>(this);
    }

    @Override
    public Multi<T> cache() {
        return Infrastructure.onMultiCreation(new MultiCacheOp<>(this));
    }

    @Override
    public Multi<T> emitOn(Executor executor) {
        return Infrastructure.onMultiCreation(new MultiEmitOnOp<>(this, nonNull(executor, "executor")));
    }

    @Override
    public Multi<T> runSubscriptionOn(Executor executor) {
        return Infrastructure.onMultiCreation(new MultiSubscribeOnOp<>(this, executor));
    }

    @Override
    public MultiOnCompletion<T> onCompletion() {
        return new MultiOnCompletion<>(this);
    }

    @Override
    public MultiTransform<T> transform() {
        return new MultiTransform<>(this);
    }

    @Override
    public MultiSelect<T> select() {
        return new MultiSelect<>(this);
    }

    @Override
    public MultiSkip<T> skip() {
        return new MultiSkip<>(this);
    }

    @Override
    public MultiOverflow<T> onOverflow() {
        return new MultiOverflow<>(this);
    }

    @Override
    public MultiOnSubscribe<T> onSubscribe() {
        return new MultiOnSubscribe<>(this);
    }

    @Override
    public MultiBroadcast<T> broadcast() {
        return new MultiBroadcast<>(this);
    }

    @Override
    public MultiConvert<T> convert() {
        return new MultiConvert<>(this);
    }

    @Override
    public MultiOnTerminate<T> onTermination() {
        return new MultiOnTerminate<>(this);
    }

    @Override
    public MultiOnCancel<T> onCancellation() {
        return new MultiOnCancel<>(this);
    }

    @Override
    public MultiOnRequest<T> onRequest() {
        return new MultiOnRequest<>(this);
    }

    @Override
    public MultiCollect<T> collect() {
        return new MultiCollect<>(this);
    }

    @Override
    public MultiGroup<T> group() {
        return new MultiGroup<>(this);
    }

    public Multi<T> toHotStream() {
        BroadcastProcessor<T> processor = BroadcastProcessor.create();
        this.subscribe(processor);
        return processor;
    }

}
