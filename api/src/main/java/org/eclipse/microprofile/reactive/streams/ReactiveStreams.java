/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.eclipse.microprofile.reactive.streams;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.CompletionStage;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Primary entry point into the Reactive Streams utility API.
 * <p>
 * This class provides factory methods for publisher and processor builders, which can then be subsequently manipulated
 * using their respective APIs.
 * <p>
 * The documentation for each operator uses marble diagrams to visualize how the operator functions. Each element
 * flowing in and out of the stream is represented as a coloured marble that has a value, with the operator
 * applying some transformation or some side effect, termination and error signals potentially being passed, and
 * for operators that subscribe to the stream, an output value being redeemed at the end.
 * <p>
 * Below is an example diagram labelling all the parts of the stream.
 * <p>
 * <img src="doc-files/example.png" alt="Example marble diagram">
 */
public class ReactiveStreams {

    private ReactiveStreams() {
    }

    private static ReactiveStreamsFactory factory() {
        Iterator<ReactiveStreamsFactory> factories = ServiceLoader.load(ReactiveStreamsFactory.class).iterator();

        if (factories.hasNext()) {
            return factories.next();
        }
        else {
            throw new IllegalStateException("No implementation of ReactiveStreamsFactory service could be found.");
        }
    }

    /**
     * Create a {@link PublisherBuilder} from the given {@link Publisher}.
     *
     * @param publisher The publisher to wrap.
     * @param <T>       The type of the elements that the publisher produces.
     * @return A publisher builder that wraps the given publisher.
     */
    public static <T> PublisherBuilder<T> fromPublisher(Publisher<? extends T> publisher) {
        return factory().fromPublisher(publisher);
    }

    /**
     * Create a {@link PublisherBuilder} that emits a single element.
     * <p>
     * <img src="doc-files/of-single.png" alt="of marble diagram">
     *
     * @param t   The element to emit.
     * @param <T> The type of the element.
     * @return A publisher builder that will emit the element.
     */
    public static <T> PublisherBuilder<T> of(T t) {
        return factory().of(t);
    }

    /**
     * Create a {@link PublisherBuilder} that emits the given elements.
     * <p>
     * <img src="doc-files/of-many.png" alt="of marble diagram">
     *
     * @param ts  The elements to emit.
     * @param <T> The type of the elements.
     * @return A publisher builder that will emit the elements.
     */
    public static <T> PublisherBuilder<T> of(T... ts) {
        return factory().of(ts);
    }

    /**
     * Create an empty {@link PublisherBuilder}.
     * <p>
     * <img src="doc-files/empty.png" alt="empty marble diagram">
     *
     * @param <T> The type of the publisher builder.
     * @return A publisher builder that will just emit a completion signal.
     */
    public static <T> PublisherBuilder<T> empty() {
        return factory().empty();
    }

    /**
     * Create a {@link PublisherBuilder} that will emit a single element if <code>t</code> is not null, otherwise will be
     * empty.
     * <p>
     * <img src="doc-files/ofNullable.png" alt="ofNullable marble diagram">
     *
     * @param t   The element to emit, <code>null</code> if to element should be emitted.
     * @param <T> The type of the element.
     * @return A publisher builder that optionally emits a single element.
     */
    public static <T> PublisherBuilder<T> ofNullable(T t) {
        return factory().ofNullable(t);
    }

    /**
     * Create a {@link PublisherBuilder} that will emits the elements produced by the passed in {@link Iterable}.
     * <p>
     * <img src="doc-files/fromIterable.png" alt="fromIterable marble diagram">
     *
     * @param ts  The elements to emit.
     * @param <T> The type of the elements.
     * @return A publisher builder that emits the elements of the iterable.
     */
    public static <T> PublisherBuilder<T> fromIterable(Iterable<? extends T> ts) {
        return factory().fromIterable(ts);
    }

    /**
     * Create a failed {@link PublisherBuilder}.
     * <p>
     * <img src="doc-files/failed.png" alt="failed marble diagram">
     * <p>
     * This publisher will just emit an error.
     *
     * @param t   The error te emit.
     * @param <T> The type of the publisher builder.
     * @return A publisher builder that completes the stream with an error.
     */
    public static <T> PublisherBuilder<T> failed(Throwable t) {
        return factory().failed(t);
    }

    /**
     * Create a {@link ProcessorBuilder}. This builder will start as an identity processor.
     * <p>
     * <img src="doc-files/identity.png" alt="identity marble diagram">
     *
     * @param <T> The type of elements that the processor consumes and emits.
     * @return The identity processor builder.
     */
    public static <T> ProcessorBuilder<T, T> builder() {
        return factory().builder();
    }

    /**
     * Create a {@link ProcessorBuilder} from the given {@link Processor}.
     *
     * @param processor The processor to be wrapped.
     * @param <T>       The type of the elements that the processor consumes.
     * @param <R>       The type of the elements that the processor emits.
     * @return A processor builder that wraps the processor.
     */
    public static <T, R> ProcessorBuilder<T, R> fromProcessor(Processor<? super T, ? extends R> processor) {
        return factory().fromProcessor(processor);
    }

    /**
     * Create a {@link SubscriberBuilder} from the given {@link Subscriber}.
     *
     * @param subscriber The subscriber to be wrapped.
     * @param <T>        The type of elements that the subscriber consumes.
     * @return A subscriber builder that wraps the subscriber.
     */
    public static <T> SubscriberBuilder<T, Void> fromSubscriber(Subscriber<? extends T> subscriber) {
        return factory().fromSubscriber(subscriber);
    }

    /**
     * Creates an infinite stream produced by the iterative application of the function {@code f} to an initial element
     * {@code seed} consisting of {@code seed}, {@code f(seed)}, {@code f(f(seed))}, etc.
     * <p>
     * <img src="doc-files/iterate.png" alt="iterate marble diagram">
     *
     * @param seed The initial element.
     * @param f    A function applied to the previous element to produce the next element.
     * @param <T>  The type of stream elements.
     * @return A publisher builder.
     */
    public static <T> PublisherBuilder<T> iterate(T seed, UnaryOperator<T> f) {
        return factory().iterate(seed, f);
    }

    /**
     * Creates an infinite stream that emits elements supplied by the supplier {@code s}.
     * <p>
     * <img src="doc-files/generate.png" alt="generate marble diagram">
     *
     * @param s   The supplier.
     * @param <T> The type of stream elements.
     * @return A publisher builder.
     */
    public static <T> PublisherBuilder<T> generate(Supplier<? extends T> s) {
        return factory().generate(s);
    }

    /**
     * Concatenates two publishers.
     * <p>
     * <img src="doc-files/concat.png" alt="concat marble diagram">
     * <p>
     * The resulting stream will be produced by subscribing to the first publisher, and emitting the elements it emits,
     * until it emits a completion signal, at which point the second publisher will be subscribed to, and its elements
     * will be emitted.
     * <p>
     * If the first publisher completes with an error signal, then the second publisher will be subscribed to but
     * immediately cancelled, none of its elements will be emitted. This ensures that hot publishers are cleaned up.
     * If downstream emits a cancellation signal before the first publisher finishes, it will be passed to both
     * publishers.
     *
     * @param a   The first publisher.
     * @param b   The second publisher.
     * @param <T> The type of stream elements.
     * @return A publisher builder.
     */
    public static <T> PublisherBuilder<T> concat(PublisherBuilder<? extends T> a,
                                                 PublisherBuilder<? extends T> b) {
        return factory().concat(a, b);
    }

    /**
     * Creates a publisher from a {@link CompletionStage}.
     * <p>
     * <img src="doc-files/fromCompletionStage.png" alt="fromCompletionStage marble diagram">
     * <p>
     * When the {@code CompletionStage} is redeemed, the publisher will emit the redeemed element, and then signal
     * completion. If the completion stage is redeemed with {@code null}, the stream will be failed with a
     * {@link NullPointerException}.
     * <p>
     * If the {@code CompletionStage} is completed with a failure, this failure will be propagated through the stream.
     *
     * @param completionStage The {@code CompletionStage} to create the publisher from.
     * @param <T> The type of the {@code CompletionStage} value.
     * @return A {@code PublisherBuilder} representation of this {@code CompletionStage}.
     */
    public static <T> PublisherBuilder<T> fromCompletionStage(CompletionStage<? extends T> completionStage) {
        return factory().fromCompletionStage(completionStage);
    }

    /**
     * Creates a publisher from a {@link CompletionStage}.
     * <p>
     * <img src="doc-files/fromCompletionStageNullable.png" alt="fromCompletionStage marble diagram">
     * <p>
     * When the {@code CompletionStage} is redeemed, the publisher will emit the redeemed element, and then signal
     * completion. If the completion stage is redeemed with {@code null}, the stream will be immediately completed
     * with no element, ie, it will be an empty stream.
     * <p>
     * If the {@code CompletionStage} is completed with a failure, this failure will be propagated through the stream.
     *
     * @param completionStage The {@code CompletionStage} to create the publisher from.
     * @param <T> The type of the {@code CompletionStage} value.
     * @return A {@code PublisherBuilder} representation of this {@code CompletionStage}.
     */
    public static <T> PublisherBuilder<T> fromCompletionStageNullable(CompletionStage<? extends T> completionStage) {
        return factory().fromCompletionStageNullable(completionStage);
    }

}
