package tech.picnic.errorprone.refastertemplates;

import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.refaster.ImportPolicy;
import com.google.errorprone.refaster.Refaster;
import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import com.google.errorprone.refaster.annotation.Matches;
import com.google.errorprone.refaster.annotation.Repeated;
import com.google.errorprone.refaster.annotation.UseImportPolicy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Publisher;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tech.picnic.errorprone.migration.util.RxJavaReactorMigrationUtil;

/** The Refaster templates for the migration of the RxJava Flowable type to Reactor */
final class RxJavaFlowableToReactorTemplates {

  private RxJavaFlowableToReactorTemplates() {}

  // XXX: static Flowable amb(Iterable)

  static final class FlowableAmbArray<T> {
    @BeforeTemplate
    Flowable<T> before(@Repeated Publisher<T> sources) {
      return Flowable.ambArray(sources);
    }

    @AfterTemplate
    Flowable<T> after(@Repeated Publisher<T> sources) {
      return RxJava2Adapter.fluxToFlowable(Flux.firstWithSignal(Refaster.asVarargs(sources)));
    }
  }

  // XXX: static int bufferSize()
  // XXX: static Flowable combineLatest(Function,Publisher[])
  // XXX: static Flowable combineLatest(Iterable,Function)
  // XXX: static Flowable combineLatest(Iterable,Function,int)
  // XXX: static Flowable combineLatest(Publisher[],Function)
  // XXX: static Flowable combineLatest(Publisher[],Function,int)

  static final class FlowableCombineLatest<T1, T2, R> {
    @BeforeTemplate
    Flowable<R> before(
        Publisher<? extends T1> p1,
        Publisher<? extends T2> p2,
        BiFunction<? super T1, ? super T2, ? extends R> combiner) {
      return Flowable.combineLatest(p1, p2, combiner);
    }

    @AfterTemplate
    Flowable<R> after(
        Publisher<? extends T1> p1,
        Publisher<? extends T2> p2,
        BiFunction<? super T1, ? super T2, ? extends R> combiner) {
      return RxJava2Adapter.fluxToFlowable(
          Flux.<T1, T2, R>combineLatest(
              p1, p2, RxJavaReactorMigrationUtil.toJdkBiFunction(combiner)));
    }
  }

  // XXX: static Flowable combineLatest(Publisher,Publisher,Publisher,Function3)
  // XXX: static Flowable combineLatest(Publisher,Publisher,Publisher,Publisher,Function4)
  // XXX: static Flowable
  // combineLatest(Publisher,Publisher,Publisher,Publisher,Publisher,Function5)
  // XXX: static Flowable
  // combineLatest(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function6)
  // XXX: static Flowable
  // combineLatest(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function7)
  // XXX: static Flowable
  // combineLatest(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function8)
  // XXX: static Flowable
  // combineLatest(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function9)
  // XXX: static Flowable combineLatestDelayError(Function,int,Publisher[])
  // XXX: static Flowable combineLatestDelayError(Function,Publisher[])
  // XXX: static Flowable combineLatestDelayError(Iterable,Function)
  // XXX: static Flowable combineLatestDelayError(Iterable,Function,int)
  // XXX: static Flowable combineLatestDelayError(Publisher[],Function)
  // XXX: static Flowable combineLatestDelayError(Publisher[],Function,int)
  // XXX: static Flowable concat(Iterable)
  // XXX: static Flowable concat(Publisher)
  // XXX: static Flowable concat(Publisher,int)
  // XXX: static Flowable concat(Publisher,Publisher)
  // XXX: static Flowable concat(Publisher,Publisher,Publisher)
  // XXX: static Flowable concat(Publisher,Publisher,Publisher,Publisher)
  // XXX: static Flowable concatArray(Publisher[])
  // XXX: static Flowable concatArrayDelayError(Publisher[])
  // XXX: static Flowable concatArrayEager(int,int,Publisher[])
  // XXX: static Flowable concatArrayEager(Publisher[])
  // XXX: static Flowable concatArrayEagerDelayError(int,int,Publisher[])
  // XXX: static Flowable concatArrayEagerDelayError(Publisher[])
  // XXX: static Flowable concatDelayError(Iterable)
  // XXX: static Flowable concatDelayError(Publisher)
  // XXX: static Flowable concatDelayError(Publisher,int,boolean)
  // XXX: static Flowable concatEager(Iterable)
  // XXX: static Flowable concatEager(Iterable,int,int)
  // XXX: static Flowable concatEager(Publisher)
  // XXX: static Flowable concatEager(Publisher,int,int)
  // XXX: static Flowable create(FlowableOnSubscribe,BackpressureStrategy)

  // XXX: The types of the @Before and @After template are not matching.
  static final class FlowableDefer<T> {
    @BeforeTemplate
    Flowable<T> before(Callable<? extends Publisher<? extends T>> supplier) {
      return Flowable.defer(supplier);
    }

    @AfterTemplate
    Flowable<T> after(Callable<? extends Publisher<T>> supplier) {
      return RxJava2Adapter.fluxToFlowable(
          Flux.defer(RxJavaReactorMigrationUtil.callableAsSupplier(supplier)));
    }
  }

  static final class FlowableEmpty<T> {
    @BeforeTemplate
    Flowable<T> before() {
      return Flowable.empty();
    }

    @AfterTemplate
    Flowable<T> after() {
      return RxJava2Adapter.fluxToFlowable(Flux.empty());
    }
  }

  static final class FlowableErrorCallable<T> {
    @BeforeTemplate
    Flowable<T> before(Callable<? extends Throwable> throwable) {
      return Flowable.error(throwable);
    }

    @AfterTemplate
    Flowable<T> after(Callable<? extends Throwable> throwable) {
      return RxJava2Adapter.fluxToFlowable(
          Flux.error(RxJavaReactorMigrationUtil.callableAsSupplier(throwable)));
    }
  }

  static final class FlowableErrorThrowable<T> {
    @BeforeTemplate
    Flowable<T> before(Throwable throwable) {
      return Flowable.error(throwable);
    }

    @AfterTemplate
    Flowable<T> after(Throwable throwable) {
      return RxJava2Adapter.fluxToFlowable(Flux.error(throwable));
    }
  }

  // XXX: Add test
  static final class FlowableFromArraySingleElement<T> {
    @BeforeTemplate
    Flowable<T> before(T item) {
      return Flowable.fromArray(item);
    }

    @AfterTemplate
    Flowable<T> after(T item) {
      return RxJava2Adapter.fluxToFlowable(Flux.just(item));
    }
  }

  // XXX: Or should this be Object[] instead of T...? The test doesn't trigger.
  static final class FlowableFromArray<T> {
    @BeforeTemplate
    Flowable<T> before(T[] items) {
      return Flowable.fromArray(items);
    }

    @BeforeTemplate
    Flowable<T> before2(T... items) {
      return Flowable.fromArray(items);
    }

    @AfterTemplate
    Flowable<T> after(T[] items) {
      // Here do something with T[].
      return RxJava2Adapter.fluxToFlowable(Flux.fromArray(items));
    }
  }

  static final class FlowableFromCallable<T> {
    @BeforeTemplate
    Flowable<T> before(Callable<? extends T> supplier) {
      return Flowable.fromCallable(supplier);
    }

    @AfterTemplate
    Flowable<T> after(Callable<? extends T> supplier) {
      return RxJava2Adapter.monoToFlowable(Mono.fromCallable(supplier));
    }
  }

  // XXX: static Flowable fromFuture(Future)
  // XXX: static Flowable fromFuture(Future,long,TimeUnit)
  // XXX: static Flowable fromFuture(Future,long,TimeUnit,Scheduler)
  // XXX: static Flowable fromFuture(Future,Scheduler)

  static final class FlowableFromIterable<T> {
    @BeforeTemplate
    Flowable<T> before(Iterable<? extends T> iterable) {
      return Flowable.fromIterable(iterable);
    }

    @AfterTemplate
    Flowable<T> after(Iterable<? extends T> iterable) {
      return RxJava2Adapter.fluxToFlowable(Flux.fromIterable(iterable));
    }
  }

  static final class FlowableFromPublisher<T> {
    @BeforeTemplate
    Flowable<T> before(Publisher<? extends T> source) {
      return Flowable.fromPublisher(source);
    }

    @AfterTemplate
    Flowable<T> after(Publisher<? extends T> source) {
      return RxJava2Adapter.fluxToFlowable(Flux.from(source));
    }
  }

  // XXX: static Flowable generate(Callable,BiConsumer)
  // XXX: static Flowable generate(Callable,BiConsumer,Consumer)
  // XXX: static Flowable generate(Callable,BiFunction)
  // XXX: static Flowable generate(Callable,BiFunction,Consumer)
  // XXX: static Flowable generate(Consumer)
  // XXX: static Flowable interval(long,long,TimeUnit)
  // XXX: static Flowable interval(long,long,TimeUnit,Scheduler)
  // XXX: static Flowable interval(long,TimeUnit)
  // XXX: static Flowable interval(long,TimeUnit,Scheduler)
  // XXX: static Flowable intervalRange(long,long,long,long,TimeUnit)
  // XXX: static Flowable intervalRange(long,long,long,long,TimeUnit,Scheduler)

  static final class FlowableJust<T> {
    @BeforeTemplate
    Flowable<T> before(T t) {
      return Flowable.just(t);
    }

    @AfterTemplate
    Flowable<T> after(T t) {
      return RxJava2Adapter.fluxToFlowable(Flux.just(t));
    }
  }

  static final class FlowableJustTwo<T> {
    @BeforeTemplate
    Flowable<T> before(T t, @Repeated T arguments) {
      return Flowable.just(t, arguments);
    }

    @AfterTemplate
    Flowable<T> after(T t, @Repeated T arguments) {
      return RxJava2Adapter.fluxToFlowable(Flux.just(t, arguments));
    }
  }

  static final class FlowableJustThree<T> {
    @BeforeTemplate
    Flowable<T> before(T t1, T t2, T t3) {
      return Flowable.just(t1, t2, t3);
    }

    @AfterTemplate
    Flowable<T> after(T t1, T t2, T t3) {
      return RxJava2Adapter.fluxToFlowable(Flux.just(t1, t2, t3));
    }
  }

  static final class FlowableJustFour<T> {
    @BeforeTemplate
    Flowable<T> before(T t1, T t2, T t3, T t4) {
      return Flowable.just(t1, t2, t3, t4);
    }

    @AfterTemplate
    Flowable<T> after(T t1, T t2, T t3, T t4) {
      return RxJava2Adapter.fluxToFlowable(Flux.just(t1, t2, t3, t4));
    }
  }

  static final class FlowableJustFive<T> {
    @BeforeTemplate
    Flowable<T> before(T t1, T t2, T t3, T t4, T t5) {
      return Flowable.just(t1, t2, t3, t4, t5);
    }

    @AfterTemplate
    Flowable<T> after(T t1, T t2, T t3, T t4, T t5) {
      return RxJava2Adapter.fluxToFlowable(Flux.just(t1, t2, t3, t4, t5));
    }
  }

  // XXX: static Flowable just(Object,Object,Object,Object,Object,Object)
  // XXX: static Flowable just(Object,Object,Object,Object,Object,Object,Object)
  // XXX: static Flowable just(Object,Object,Object,Object,Object,Object,Object,Object)
  // XXX: static Flowable
  // just(Object,Object,Object,Object,Object,Object,Object,Object,Object)
  // XXX: static Flowable
  // just(Object,Object,Object,Object,Object,Object,Object,Object,Object,Object)
  // XXX: static Flowable merge(Iterable)
  // XXX: static Flowable merge(Iterable,int)
  // XXX: static Flowable merge(Iterable,int,int)
  // XXX: static Flowable merge(Publisher)
  // XXX: static Flowable merge(Publisher,int)
  // XXX: static Flowable merge(Publisher,Publisher)

  static final class FlowableMergePublisherPublisher<T> {
    @BeforeTemplate
    Flowable<T> before(Publisher<? extends T> source1, Publisher<? extends T> source2) {
      return Flowable.merge(source1, source2);
    }

    @AfterTemplate
    Flowable<T> after(Publisher<? extends T> source1, Publisher<? extends T> source2) {
      return RxJava2Adapter.fluxToFlowable(Flux.merge(source1, source2));
    }
  }

  // XXX: static Flowable merge(Publisher,Publisher,Publisher)
  // XXX: static Flowable merge(Publisher,Publisher,Publisher,Publisher)
  // XXX: static Flowable mergeArray(int,int,Publisher[])
  // XXX: static Flowable mergeArray(Publisher[])
  // XXX: static Flowable mergeArrayDelayError(int,int,Publisher[])
  // XXX: static Flowable mergeArrayDelayError(Publisher[])
  // XXX: static Flowable mergeDelayError(Iterable)
  // XXX: static Flowable mergeDelayError(Iterable,int)
  // XXX: static Flowable mergeDelayError(Iterable,int,int)
  // XXX: static Flowable mergeDelayError(Publisher)
  // XXX: static Flowable mergeDelayError(Publisher,int)
  // XXX: static Flowable mergeDelayError(Publisher,Publisher)
  // XXX: static Flowable mergeDelayError(Publisher,Publisher,Publisher)
  // XXX: static Flowable mergeDelayError(Publisher,Publisher,Publisher,Publisher)
  // XXX: static Flowable never()

  static final class FlowableRange {
    @BeforeTemplate
    Flowable<Integer> before(int start, int count) {
      return Flowable.range(start, count);
    }

    @AfterTemplate
    Flowable<Integer> after(int start, int count) {
      return RxJava2Adapter.fluxToFlowable(Flux.range(start, count));
    }
  }

  // XXX: Not migrating the `Flowable.rangeLong` callers with actual `long` arguments.
  static final class FlowableRangeLong {
    @BeforeTemplate
    Flowable<Long> before(int start, int count) {
      return Flowable.rangeLong(start, count);
    }

    @AfterTemplate
    Flowable<Integer> after(int start, int count) {
      return RxJava2Adapter.fluxToFlowable(Flux.range(start, count));
    }
  }

  // XXX: static Single sequenceEqual(Publisher,Publisher)
  // XXX: static Single sequenceEqual(Publisher,Publisher,BiPredicate)
  // XXX: static Single sequenceEqual(Publisher,Publisher,BiPredicate,int)
  // XXX: static Single sequenceEqual(Publisher,Publisher,int)
  // XXX: static Flowable switchOnNext(Publisher)
  // XXX: static Flowable switchOnNext(Publisher,int)
  // XXX: static Flowable switchOnNextDelayError(Publisher)
  // XXX: static Flowable switchOnNextDelayError(Publisher,int)
  // XXX: static Flowable timer(long,TimeUnit)
  // XXX: static Flowable timer(long,TimeUnit,Scheduler)
  // XXX: static Flowable unsafeCreate(Publisher)
  // XXX: static Flowable using(Callable,Function,Consumer)
  // XXX: static Flowable using(Callable,Function,Consumer,boolean)
  // XXX: static Flowable zip(Iterable,Function)
  // XXX: static Flowable zip(Publisher,Function)
  // XXX: static Flowable zip(Publisher,Publisher,BiFunction) --> Required

  static final class FlowableZip<T, U, R> {
    @BeforeTemplate
    Flowable<R> before(
        Publisher<? extends T> source1,
        Publisher<? extends U> source2,
        BiFunction<? super T, ? super U, ? extends R> zipper) {
      return Flowable.zip(source1, source2, zipper);
    }

    @AfterTemplate
    Flowable<R> after(
        Publisher<? extends T> source1,
        Publisher<? extends U> source2,
        BiFunction<? super T, ? super U, ? extends R> zipper) {
      return RxJava2Adapter.fluxToFlowable(
          Flux.<T, U, R>zip(source1, source2, RxJavaReactorMigrationUtil.toJdkBiFunction(zipper)));
    }
  }

  // XXX: static Flowable zip(Publisher,Publisher,BiFunction,boolean)
  // XXX: static Flowable zip(Publisher,Publisher,BiFunction,boolean,int)
  // XXX: static Flowable zip(Publisher,Publisher,Publisher,Function3)
  // XXX: static Flowable zip(Publisher,Publisher,Publisher,Publisher,Function4)
  // XXX: static Flowable zip(Publisher,Publisher,Publisher,Publisher,Publisher,Function5)
  // XXX: static Flowable
  // zip(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function6)
  // XXX: static Flowable
  // zip(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function7)
  // XXX: static Flowable
  // zip(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function8)
  // XXX: static Flowable
  // zip(Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Publisher,Function9)
  // XXX: static Flowable zipArray(Function,boolean,int,Publisher[])
  // XXX: static Flowable zipIterable(Iterable,Function,boolean,int)

  static final class FlowableAll<T> {
    @BeforeTemplate
    Single<Boolean> before(Flowable<T> flowable, Predicate<? super T> predicate) {
      return flowable.all(predicate);
    }

    @AfterTemplate
    Single<Boolean> after(Flowable<T> flowable, Predicate<? super T> predicate) {
      return RxJava2Adapter.monoToSingle(
          RxJava2Adapter.flowableToFlux(flowable)
              .all(RxJavaReactorMigrationUtil.toJdkPredicate(predicate)));
    }
  }

  // XXX: final Flowable ambWith(Publisher)

  static final class FlowableAny<T> {
    @BeforeTemplate
    Single<Boolean> before(Flowable<T> flowable, Predicate<? super T> predicate) {
      return flowable.any(predicate);
    }

    @AfterTemplate
    Single<Boolean> after(Flowable<T> flowable, Predicate<? super T> predicate) {
      return RxJava2Adapter.monoToSingle(
          RxJava2Adapter.flowableToFlux(flowable)
              .any(RxJavaReactorMigrationUtil.toJdkPredicate(predicate)));
    }
  }

  // XXX: final Object as(FlowableConverter)

  static final class FlowableBlockingFirst<T> {
    @BeforeTemplate
    Object before(Flowable<T> flowable) {
      return flowable.blockingFirst();
    }

    @AfterTemplate
    Object after(Flowable<T> flowable) {
      return RxJava2Adapter.flowableToFlux(flowable).blockFirst();
    }
  }

  // XXX: final Object blockingFirst(Object)
  // XXX: final void blockingForEach(Consumer)
  // XXX: final Iterable blockingIterable()
  // XXX: final Iterable blockingIterable(int)
  // XXX: final Object blockingLast()
  // XXX: final Object blockingLast(Object)
  // XXX: final Iterable blockingLatest()
  // XXX: final Iterable blockingMostRecent(Object)
  // XXX: final Iterable blockingNext()
  // XXX: final Object blockingSingle()
  // XXX: final Object blockingSingle(Object)
  // XXX: final void blockingSubscribe()
  // XXX: final void blockingSubscribe(Consumer)
  // XXX: final void blockingSubscribe(Consumer,Consumer)
  // XXX: final void blockingSubscribe(Consumer,Consumer,Action)
  // XXX: final void blockingSubscribe(Consumer,Consumer,Action,int)
  // XXX: final void blockingSubscribe(Consumer,Consumer,int)
  // XXX: final void blockingSubscribe(Consumer,int)
  // XXX: final void blockingSubscribe(Subscriber)
  // XXX: final Flowable buffer(Callable)
  // XXX: final Flowable buffer(Callable,Callable)
  // XXX: final Flowable buffer(Flowable,Function)
  // XXX: final Flowable buffer(Flowable,Function,Callable)
  // XXX: final Flowable buffer(int)
  // XXX: final Flowable buffer(int,Callable)
  // XXX: final Flowable buffer(int,int)
  // XXX: final Flowable buffer(int,int,Callable)
  // XXX: final Flowable buffer(long,long,TimeUnit)
  // XXX: final Flowable buffer(long,long,TimeUnit,Scheduler)
  // XXX: final Flowable buffer(long,long,TimeUnit,Scheduler,Callable)
  // XXX: final Flowable buffer(long,TimeUnit)
  // XXX: final Flowable buffer(long,TimeUnit,int)
  // XXX: final Flowable buffer(long,TimeUnit,Scheduler)
  // XXX: final Flowable buffer(long,TimeUnit,Scheduler,int)
  // XXX: final Flowable buffer(long,TimeUnit,Scheduler,int,Callable,boolean)
  // XXX: final Flowable buffer(Publisher)
  // XXX: final Flowable buffer(Publisher,Callable)
  // XXX: final Flowable buffer(Publisher,int)
  // XXX: final Flowable cache()
  // XXX: final Flowable cacheWithInitialCapacity(int)
  // XXX: final Flowable cast(Class)
  // XXX: final Single collect(Callable,BiConsumer)
  // XXX: final Single collectInto(Object,BiConsumer)
  // XXX: final Flowable compose(FlowableTransformer)

  static final class FlowableConcatMap<T, R> {
    @BeforeTemplate
    Flowable<R> before(
        Flowable<T> flowable, Function<? super T, ? extends Publisher<? extends R>> function) {
      return flowable.concatMap(function);
    }

    @AfterTemplate
    Flowable<R> after(
        Flowable<T> flowable, Function<? super T, ? extends Publisher<? extends R>> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .concatMap(RxJavaReactorMigrationUtil.toJdkFunction(function)));
    }
  }

  // XXX: final Flowable concatMap(Function,int)

  static final class FlowableConcatMapCompletable<T> {
    @BeforeTemplate
    Completable before(
        Flowable<T> flowable, Function<? super T, ? extends CompletableSource> function) {
      return flowable.concatMapCompletable(function);
    }

    @AfterTemplate
    Completable after(
        Flowable<T> flowable, Function<? super T, ? extends CompletableSource> function) {
      return RxJava2Adapter.monoToCompletable(
          RxJava2Adapter.flowableToFlux(flowable)
              .concatMap(
                  e ->
                      RxJava2Adapter.completableToMono(
                          Completable.wrap(
                              RxJavaReactorMigrationUtil.toJdkFunction(function).apply(e))))
              .then());
    }
  }

  // XXX: final Completable concatMapCompletable(Function,int)
  // XXX: final Completable concatMapCompletableDelayError(Function)
  // XXX: final Completable concatMapCompletableDelayError(Function,boolean)
  // XXX: final Completable concatMapCompletableDelayError(Function,boolean,int)
  // XXX: final Flowable concatMapDelayError(Function)
  // XXX: final Flowable concatMapDelayError(Function,int,boolean)
  // XXX: final Flowable concatMapEager(Function)
  // XXX: final Flowable concatMapEager(Function,int,int)
  // XXX: final Flowable concatMapEagerDelayError(Function,boolean)
  // XXX: final Flowable concatMapEagerDelayError(Function,int,int,boolean)
  // XXX: final Flowable concatMapIterable(Function)
  // XXX: final Flowable concatMapIterable(Function,int)

  static final class FlowableConcatMapMaybe<T, R> {
    @BeforeTemplate
    Flowable<R> before(Flowable<T> flowable, Function<T, ? extends MaybeSource<R>> mapper) {
      return flowable.concatMapMaybe(mapper);
    }

    @AfterTemplate
    Flowable<R> after(Flowable<T> flowable, Function<T, ? extends MaybeSource<R>> mapper) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .concatMap(
                  e ->
                      Maybe.wrap(RxJavaReactorMigrationUtil.toJdkFunction(mapper).apply(e))
                          .toFlowable()));
    }
  }

  // XXX: final Flowable concatMapMaybe(Function,int)

  static final class FlowableConcatMapMaybeDelayError<T, R, O extends R, M extends MaybeSource<O>> {
    @BeforeTemplate
    Flowable<O> before(
        Flowable<T> flowable, Function<? super T, ? extends MaybeSource<? extends O>> function) {
      return flowable.concatMapMaybeDelayError(function);
    }

    @AfterTemplate
    Flowable<O> after(Flowable<T> flowable, Function<T, M> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .concatMapDelayError(
                  e ->
                      Maybe.wrap(RxJavaReactorMigrationUtil.<T, M>toJdkFunction(function).apply(e))
                          .toFlowable()));
    }
  }

  // XXX: final Flowable concatMapMaybeDelayError(Function,boolean)
  // XXX: final Flowable concatMapMaybeDelayError(Function,boolean,int)
  // XXX: final Flowable concatMapSingle(Function)
  // XXX: final Flowable concatMapSingle(Function,int)
  // XXX: final Flowable concatMapSingleDelayError(Function)
  // XXX: final Flowable concatMapSingleDelayError(Function,boolean)
  // XXX: final Flowable concatMapSingleDelayError(Function,boolean,int)
  // XXX: final Flowable concatWith(CompletableSource)
  // XXX: final Flowable concatWith(MaybeSource)

  static final class FlowableConcatWithPublisher<T> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable, Publisher<T> source) {
      return flowable.concatWith(source);
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable, Publisher<T> source) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable).concatWith(source));
    }
  }

  // XXX: final Flowable concatWith(SingleSource)
  // XXX: final Single contains(Object)
  // XXX: final Single count()
  // XXX: final Flowable debounce(Function)
  // XXX: final Flowable debounce(long,TimeUnit)
  // XXX: final Flowable debounce(long,TimeUnit,Scheduler)
  // XXX: final Flowable defaultIfEmpty(Object)
  // XXX: final Flowable delay(Function)
  // XXX: final Flowable delay(long,TimeUnit)
  // XXX: final Flowable delay(long,TimeUnit,boolean)
  // XXX: final Flowable delay(long,TimeUnit,Scheduler)
  // XXX: final Flowable delay(long,TimeUnit,Scheduler,boolean)
  // XXX: final Flowable delay(Publisher,Function)
  // XXX: final Flowable delaySubscription(long,TimeUnit)
  // XXX: final Flowable delaySubscription(long,TimeUnit,Scheduler)
  // XXX: final Flowable delaySubscription(Publisher)
  // XXX: final Flowable dematerialize()
  // XXX: final Flowable dematerialize(Function)

  static final class FlowableDistinct<T> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable) {
      return flowable.distinct();
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.fluxToFlowable(RxJava2Adapter.flowableToFlux(flowable).distinct());
    }
  }

  // XXX: final Flowable distinct(Function)
  // XXX: final Flowable distinct(Function,Callable)
  // XXX: final Flowable distinctUntilChanged()
  // XXX: final Flowable distinctUntilChanged(BiPredicate)
  // XXX: final Flowable distinctUntilChanged(Function)
  // XXX: final Flowable doAfterNext(Consumer)
  // XXX: final Flowable doAfterTerminate(Action)
  // XXX: final Flowable doFinally(Action)
  // XXX: final Flowable doOnCancel(Action)
  // XXX: final Flowable doOnComplete(Action)
  // XXX: final Flowable doOnEach(Consumer)
  // XXX: final Flowable doOnEach(Subscriber)
  // XXX: final Flowable doOnError(Consumer)
  // XXX: final Flowable doOnLifecycle(Consumer,LongConsumer,Action)
  // XXX: final Flowable doOnNext(Consumer)
  // XXX: final Flowable doOnRequest(LongConsumer)
  // XXX: final Flowable doOnSubscribe(Consumer)
  // XXX: final Flowable doOnTerminate(Action)
  // XXX: final Maybe elementAt(long)
  // XXX: final Single elementAt(long,Object)
  // XXX: final Single elementAtOrError(long)

  static final class FlowableFilter<S, T extends S> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable, Predicate<S> predicate) {
      return flowable.filter(predicate);
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable, Predicate<S> predicate) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .filter(RxJavaReactorMigrationUtil.toJdkPredicate(predicate)));
    }
  }

  // XXX: final Single first(Object)

  static final class FlowableFirstElement<T> {
    @BeforeTemplate
    Maybe<T> before(Flowable<T> flowable) {
      return flowable.firstElement();
    }

    @AfterTemplate
    Maybe<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.monoToMaybe(RxJava2Adapter.flowableToFlux(flowable).next());
    }
  }

  // XXX: The `next()` is not perfect, it doesnt error in the case when the first value is not
  // available.
  static final class FlowableFirstOrError<T> {
    @BeforeTemplate
    Single<T> before(Flowable<T> flowable) {
      return flowable.firstOrError();
    }

    @AfterTemplate
    Single<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.monoToSingle(RxJava2Adapter.flowableToFlux(flowable).next().single());
    }
  }

  static final class FlowableFlatMap<I, T extends I, O, P extends Publisher<? extends O>> {
    @BeforeTemplate
    Flowable<O> before(
        Flowable<T> flowable, Function<? super I, ? extends Publisher<? extends O>> function) {
      return flowable.flatMap(function);
    }

    @UseImportPolicy(ImportPolicy.IMPORT_CLASS_DIRECTLY)
    @AfterTemplate
    Flowable<O> after(Flowable<I> flowable, Function<I, P> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .flatMap(RxJavaReactorMigrationUtil.toJdkFunction(function)));
    }
  }

  // XXX: final Flowable flatMap(Function,BiFunction)
  // XXX: final Flowable flatMap(Function,BiFunction,boolean)
  // XXX: final Flowable flatMap(Function,BiFunction,boolean,int)
  // XXX: final Flowable flatMap(Function,BiFunction,boolean,int,int)
  // XXX: final Flowable flatMap(Function,BiFunction,int)
  // XXX: final Flowable flatMap(Function,boolean)
  // XXX: final Flowable flatMap(Function,boolean,int)
  // XXX: final Flowable flatMap(Function,boolean,int,int)
  // XXX: final Flowable flatMap(Function,Function,Callable)
  // XXX: final Flowable flatMap(Function,Function,Callable,int)
  // XXX: final Flowable flatMap(Function,int)

  static final class FlowableFlatMapInt<I, T extends I, O, P extends Publisher<? extends O>> {
    @BeforeTemplate
    Flowable<O> before(
        Flowable<T> flowable,
        Function<? super I, ? extends Publisher<? extends O>> function,
        int i) {
      return flowable.flatMap(function, i);
    }

    @UseImportPolicy(ImportPolicy.IMPORT_CLASS_DIRECTLY)
    @AfterTemplate
    Flowable<O> after(Flowable<I> flowable, Function<I, P> function, int i) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .flatMap(RxJavaReactorMigrationUtil.toJdkFunction(function), i));
    }
  }

  static final class FlowableFlatMapCompletable<T, R extends CompletableSource> {
    @BeforeTemplate
    Completable before(
        Flowable<T> flowable, Function<? super T, ? extends CompletableSource> function) {
      return flowable.flatMapCompletable(function);
    }

    @AfterTemplate
    Completable after(Flowable<T> flowable, Function<T, R> function) {
      return RxJava2Adapter.monoToCompletable(
          RxJava2Adapter.flowableToFlux(flowable)
              .flatMap(
                  x ->
                      RxJava2Adapter.completableToMono(
                          Completable.wrap(
                              RxJavaReactorMigrationUtil.<T, R>toJdkFunction(function).apply(x))))
              .then());
    }
  }

  // XXX: final Completable flatMapCompletable(Function,boolean,int)

  static final class FlowableFlatMapIterable<T, R> {
    @BeforeTemplate
    Flowable<R> before(
        Flowable<T> flowable, Function<? super T, ? extends Iterable<? extends R>> mapper) {
      return flowable.flatMapIterable(mapper);
    }

    @AfterTemplate
    Flowable<R> after(
        Flowable<T> flowable, Function<? super T, ? extends Iterable<? extends R>> mapper) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .flatMapIterable(RxJavaReactorMigrationUtil.toJdkFunction(mapper)));
    }
  }

  // XXX: final Flowable flatMapIterable(Function,BiFunction)
  // XXX: final Flowable flatMapIterable(Function,BiFunction,int)
  // XXX: final Flowable flatMapIterable(Function,int)

  static final class FlowableFlatMapMaybe<
      S, T extends S, R, P extends R, M extends MaybeSource<P>> {
    @BeforeTemplate
    Flowable<R> before(
        Flowable<T> flowable, Function<? super S, ? extends MaybeSource<? extends R>> function) {
      return flowable.flatMapMaybe(function);
    }

    @AfterTemplate
    Flowable<R> after(Flowable<T> flowable, Function<S, M> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .flatMap(
                  e ->
                      RxJava2Adapter.maybeToMono(
                          Maybe.wrap(
                              RxJavaReactorMigrationUtil.<S, M>toJdkFunction(function).apply(e)))));
    }
  }

  // XXX: final Flowable flatMapMaybe(Function,boolean,int)

  static final class FlowableFlatMapSingle<S, T extends S, R, P extends R, M extends Single<P>> {
    @BeforeTemplate
    Flowable<R> before(
        Flowable<T> flowable, Function<? super S, ? extends SingleSource<? extends R>> function) {
      return flowable.flatMapSingle(function);
    }

    @AfterTemplate
    Flowable<R> after(Flowable<T> flowable, Function<S, M> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .flatMap(
                  e ->
                      RxJava2Adapter.singleToMono(
                          RxJavaReactorMigrationUtil.<S, M>toJdkFunction(function).apply(e))));
    }
  }

  // XXX: final Flowable flatMapSingle(Function,boolean,int)
  // XXX: final Disposable forEach(Consumer)
  // XXX: final Disposable forEachWhile(Predicate)
  // XXX: final Disposable forEachWhile(Predicate,Consumer)
  // XXX: final Disposable forEachWhile(Predicate,Consumer,Action)

  static final class FlowableGroupBy<K, V> {
    @BeforeTemplate
    Flowable<GroupedFlowable<K, V>> before(Flowable<V> flowable, Function<V, K> keySelector) {
      return flowable.groupBy(keySelector);
    }

    @AfterTemplate
    Flowable<GroupedFlowable<K, V>> after(Flowable<V> flowable, Function<V, K> keySelector) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .groupBy(RxJavaReactorMigrationUtil.toJdkFunction(keySelector))
              .map(RxJavaReactorMigrationUtil::groupedFluxToGroupedFlowable));
    }
  }

  // XXX: final Flowable groupBy(Function,boolean)
  // XXX: final Flowable groupBy(Function,Function)
  // XXX: final Flowable groupBy(Function,Function,boolean)
  // XXX: final Flowable groupBy(Function,Function,boolean,int)
  // XXX: final Flowable groupBy(Function,Function,boolean,int,Function)
  // XXX: final Flowable groupJoin(Publisher,Function,Function,BiFunction)
  // XXX: final Flowable hide()

  // XXX: Add test
  static final class FlowableIgnoreElements<T> {
    @BeforeTemplate
    Completable before(Flowable<T> flowable) {
      return flowable.ignoreElements();
    }

    @AfterTemplate
    Completable after(Flowable<T> flowable) {
      return RxJava2Adapter.monoToCompletable(
          RxJava2Adapter.flowableToFlux(flowable).ignoreElements().then());
    }
  }

  // XXX: final Single isEmpty()
  // XXX: final Flowable join(Publisher,Function,Function,BiFunction)
  // XXX: final Single last(Object)
  // XXX: final Maybe lastElement()
  // XXX: final Single lastOrError()
  // XXX: final Flowable lift(FlowableOperator)
  // XXX: final Flowable limit(long)

  static final class FlowableMap<I, T extends I, O> {
    @BeforeTemplate
    Flowable<O> before(Flowable<T> flowable, Function<I, O> function) {
      return flowable.map(function);
    }

    @AfterTemplate
    Flowable<O> after(Flowable<T> flowable, Function<I, O> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .map(RxJavaReactorMigrationUtil.toJdkFunction(function)));
    }
  }

  // XXX: final Flowable materialize()
  // XXX: final Flowable mergeWith(CompletableSource)
  // XXX: final Flowable mergeWith(MaybeSource)
  // XXX: final Flowable mergeWith(Publisher)

  static final class FlowableMergeWith<T> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable, SingleSource<T> source) {
      return flowable.mergeWith(source);
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable, SingleSource<T> source) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .mergeWith(RxJava2Adapter.singleToMono(Single.wrap(source))));
    }
  }

  // XXX: final Flowable observeOn(Scheduler)
  // XXX: final Flowable observeOn(Scheduler,boolean)
  // XXX: final Flowable observeOn(Scheduler,boolean,int)
  // XXX: final Flowable ofType(Class)
  // XXX: final Flowable onBackpressureBuffer()
  // XXX: final Flowable onBackpressureBuffer(boolean)
  // XXX: final Flowable onBackpressureBuffer(int)
  // XXX: final Flowable onBackpressureBuffer(int,Action)
  // XXX: final Flowable onBackpressureBuffer(int,boolean)
  // XXX: final Flowable onBackpressureBuffer(int,boolean,boolean)
  // XXX: final Flowable onBackpressureBuffer(int,boolean,boolean,Action)
  // XXX: final Flowable onBackpressureBuffer(long,Action,BackpressureOverflowStrategy)
  // XXX: final Flowable onBackpressureDrop()
  // XXX: final Flowable onBackpressureDrop(Consumer)
  // XXX: final Flowable onBackpressureLatest()
  // XXX: final Flowable onErrorResumeNext(Function) -> Required

  static final class FlowableOnErrorResumeNext<T> {
    @BeforeTemplate
    Flowable<T> before(
        Flowable<T> flowable,
        Function<? super Throwable, ? extends Publisher<? extends T>> function) {
      return flowable.onErrorResumeNext(function);
    }

    @AfterTemplate
    Flowable<T> after(
        Flowable<T> flowable,
        Function<? super Throwable, ? extends Publisher<? extends T>> function) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .onErrorResume(RxJavaReactorMigrationUtil.toJdkFunction(function)));
    }
  }

  // XXX: final Flowable onErrorReturn(Function)
  // XXX: final Flowable onErrorReturnItem(Object)
  // XXX: final Flowable onExceptionResumeNext(Publisher)
  // XXX: final Flowable onTerminateDetach()
  // XXX: final ParallelFlowable parallel()
  // XXX: final ParallelFlowable parallel(int)
  // XXX: final ParallelFlowable parallel(int,int)
  // XXX: final ConnectableFlowable publish()
  // XXX: final Flowable publish(Function)
  // XXX: final Flowable publish(Function,int)
  // XXX: final ConnectableFlowable publish(int)
  // XXX: final Flowable rebatchRequests(int)
  // XXX: final Maybe reduce(BiFunction)
  // XXX: final Single reduce(Object,BiFunction)
  // XXX: final Single reduceWith(Callable,BiFunction)
  // XXX: final Flowable repeat()
  // XXX: final Flowable repeat(long)
  // XXX: final Flowable repeatUntil(BooleanSupplier)
  // XXX: final Flowable repeatWhen(Function)
  // XXX: final ConnectableFlowable replay()
  // XXX: final Flowable replay(Function)
  // XXX: final Flowable replay(Function,int)
  // XXX: final Flowable replay(Function,int,long,TimeUnit)
  // XXX: final Flowable replay(Function,int,long,TimeUnit,Scheduler)
  // XXX: final Flowable replay(Function,int,Scheduler)
  // XXX: final Flowable replay(Function,long,TimeUnit)
  // XXX: final Flowable replay(Function,long,TimeUnit,Scheduler)
  // XXX: final Flowable replay(Function,Scheduler)
  // XXX: final ConnectableFlowable replay(int)
  // XXX: final ConnectableFlowable replay(int,long,TimeUnit)
  // XXX: final ConnectableFlowable replay(int,long,TimeUnit,Scheduler)
  // XXX: final ConnectableFlowable replay(int,Scheduler)
  // XXX: final ConnectableFlowable replay(long,TimeUnit)
  // XXX: final ConnectableFlowable replay(long,TimeUnit,Scheduler)
  // XXX: final ConnectableFlowable replay(Scheduler)
  // XXX: final Flowable retry()
  // XXX: final Flowable retry(BiPredicate)
  // XXX: final Flowable retry(long)
  // XXX: final Flowable retry(long,Predicate)
  // XXX: final Flowable retry(Predicate)
  // XXX: final Flowable retryUntil(BooleanSupplier)
  // XXX: final Flowable retryWhen(Function)
  // XXX: final void safeSubscribe(Subscriber)
  // XXX: final Flowable sample(long,TimeUnit)
  // XXX: final Flowable sample(long,TimeUnit,boolean)
  // XXX: final Flowable sample(long,TimeUnit,Scheduler)
  // XXX: final Flowable sample(long,TimeUnit,Scheduler,boolean)
  // XXX: final Flowable sample(Publisher)
  // XXX: final Flowable sample(Publisher,boolean)
  // XXX: final Flowable scan(BiFunction)
  // XXX: final Flowable scan(Object,BiFunction)
  // XXX: final Flowable scanWith(Callable,BiFunction)
  // XXX: final Flowable serialize()
  // XXX: final Flowable share()
  // XXX: final Single single(Object)

  static final class FlowableSingleDefault<T> {
    @BeforeTemplate
    Single<T> before(Flowable<T> flowable, T item) {
      return flowable.single(item);
    }

    @AfterTemplate
    Single<T> after(Flowable<T> flowable, T item) {
      return RxJava2Adapter.monoToSingle(RxJava2Adapter.flowableToFlux(flowable).single(item));
    }
  }

  static final class FlowableSingleElement<T> {
    @BeforeTemplate
    Maybe<T> before(Flowable<T> flowable) {
      return flowable.singleElement();
    }

    @AfterTemplate
    Maybe<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.monoToMaybe(RxJava2Adapter.flowableToFlux(flowable).singleOrEmpty());
    }
  }

  static final class FlowableSingleOrError<T> {
    @BeforeTemplate
    Single<T> before(Flowable<T> flowable) {
      return flowable.singleOrError();
    }

    @AfterTemplate
    Single<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.monoToSingle(RxJava2Adapter.flowableToFlux(flowable).single());
    }
  }

  // XXX: final Flowable skip(long)
  // XXX: final Flowable skip(long,TimeUnit)
  // XXX: final Flowable skip(long,TimeUnit,Scheduler)
  // XXX: final Flowable skipLast(int)
  // XXX: final Flowable skipLast(long,TimeUnit)
  // XXX: final Flowable skipLast(long,TimeUnit,boolean)
  // XXX: final Flowable skipLast(long,TimeUnit,Scheduler)
  // XXX: final Flowable skipLast(long,TimeUnit,Scheduler,boolean)
  // XXX: final Flowable skipLast(long,TimeUnit,Scheduler,boolean,int)
  // XXX: final Flowable skipUntil(Publisher)
  // XXX: final Flowable skipWhile(Predicate)

  static final class FlowableSorted<T> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable) {
      return flowable.sorted();
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.fluxToFlowable(RxJava2Adapter.flowableToFlux(flowable).sort());
    }
  }

  static final class FlowableSortedComparator<T> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable, Comparator<? super T> sortFunction) {
      return flowable.sorted(sortFunction);
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable, Comparator<? super T> sortFunction) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable).sort(sortFunction));
    }
  }

  // XXX: final Flowable startWith(Iterable)
  // XXX: final Flowable startWith(Object)
  // XXX: final Flowable startWith(Publisher)
  // XXX: final Flowable startWithArray(Object[])

  // XXX: Add test
  static final class FlowableSubscribe<T> {
    @BeforeTemplate
    Disposable before(Flowable<T> flowable) {
      return flowable.subscribe();
    }

    @AfterTemplate
    reactor.core.Disposable after(Flowable<T> flowable) {
      return RxJava2Adapter.flowableToFlux(flowable).subscribe();
    }
  }

  // XXX: Add test
  static final class FlowableSubscribeConsumer<T> {
    @BeforeTemplate
    Disposable before(Flowable<T> flowable, Consumer<? super T> consumer) {
      return flowable.subscribe(consumer);
    }

    @AfterTemplate
    reactor.core.Disposable after(Flowable<T> flowable, Consumer<? super T> consumer) {
      return RxJava2Adapter.flowableToFlux(flowable)
          .subscribe(RxJavaReactorMigrationUtil.toJdkConsumer(consumer));
    }
  }

  // XXX: Add test
  static final class FlowableSubscribeTwoConsumers<T> {
    @BeforeTemplate
    Disposable before(
        Flowable<T> flowable,
        Consumer<? super T> consumer1,
        Consumer<? super Throwable> consumer2) {
      return flowable.subscribe(consumer1, consumer2);
    }

    @AfterTemplate
    reactor.core.Disposable after(
        Flowable<T> flowable,
        Consumer<? super T> consumer1,
        Consumer<? super Throwable> consumer2) {
      return RxJava2Adapter.flowableToFlux(flowable)
          .subscribe(
              RxJavaReactorMigrationUtil.toJdkConsumer(consumer1),
              RxJavaReactorMigrationUtil.toJdkConsumer(consumer2));
    }
  }

  // XXX: Add test
  static final class FlowableSubscribeTwoConsumersWithAction<T> {
    @BeforeTemplate
    Disposable before(
        Flowable<T> flowable,
        Consumer<? super T> consumer1,
        Consumer<? super Throwable> consumer2,
        Action action) {
      return flowable.subscribe(consumer1, consumer2, action);
    }

    @AfterTemplate
    reactor.core.Disposable after(
        Flowable<T> flowable,
        Consumer<? super T> consumer1,
        Consumer<? super Throwable> consumer2,
        Action action) {
      return RxJava2Adapter.flowableToFlux(flowable)
          .subscribe(
              RxJavaReactorMigrationUtil.toJdkConsumer(consumer1),
              RxJavaReactorMigrationUtil.toJdkConsumer(consumer2),
              RxJavaReactorMigrationUtil.toRunnable(action));
    }
  }

  // XXX: final Disposable subscribe(Consumer,Consumer,Action,Consumer)
  // XXX: final void subscribe(FlowableSubscriber)
  // XXX: final void subscribe(Subscriber)
  // XXX: final Flowable subscribeOn(Scheduler)
  // XXX: final Flowable subscribeOn(Scheduler,boolean)
  // XXX: final Subscriber subscribeWith(Subscriber)

  static final class FlowableSwitchIfEmpty<T> {
    @BeforeTemplate
    Flowable<T> before(Flowable<T> flowable, Publisher<T> publisher) {
      return flowable.switchIfEmpty(publisher);
    }

    @AfterTemplate
    Flowable<T> after(Flowable<T> flowable, Publisher<T> publisher) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable).switchIfEmpty(publisher));
    }
  }

  // XXX: final Flowable switchMap(Function)
  // XXX: final Flowable switchMap(Function,int)
  // XXX: final Completable switchMapCompletable(Function)
  // XXX: final Completable switchMapCompletableDelayError(Function)
  // XXX: final Flowable switchMapDelayError(Function)
  // XXX: final Flowable switchMapDelayError(Function,int)
  // XXX: final Flowable switchMapMaybe(Function)
  // XXX: final Flowable switchMapMaybeDelayError(Function)
  // XXX: final Flowable switchMapSingle(Function)
  // XXX: final Flowable switchMapSingleDelayError(Function)
  // XXX: final Flowable take(long)
  // XXX: final Flowable take(long,TimeUnit)
  // XXX: final Flowable take(long,TimeUnit,Scheduler)
  // XXX: final Flowable takeLast(int)
  // XXX: final Flowable takeLast(long,long,TimeUnit)
  // XXX: final Flowable takeLast(long,long,TimeUnit,Scheduler)
  // XXX: final Flowable takeLast(long,long,TimeUnit,Scheduler,boolean,int)
  // XXX: final Flowable takeLast(long,TimeUnit)
  // XXX: final Flowable takeLast(long,TimeUnit,boolean)
  // XXX: final Flowable takeLast(long,TimeUnit,Scheduler)
  // XXX: final Flowable takeLast(long,TimeUnit,Scheduler,boolean)
  // XXX: final Flowable takeLast(long,TimeUnit,Scheduler,boolean,int)
  // XXX: final Flowable takeUntil(Predicate)
  // XXX: final Flowable takeUntil(Publisher)
  // XXX: final Flowable takeWhile(Predicate)
  // XXX: final Flowable throttleFirst(long,TimeUnit)
  // XXX: final Flowable throttleFirst(long,TimeUnit,Scheduler)
  // XXX: final Flowable throttleLast(long,TimeUnit)
  // XXX: final Flowable throttleLast(long,TimeUnit,Scheduler)
  // XXX: final Flowable throttleLatest(long,TimeUnit)
  // XXX: final Flowable throttleLatest(long,TimeUnit,boolean)
  // XXX: final Flowable throttleLatest(long,TimeUnit,Scheduler)
  // XXX: final Flowable throttleLatest(long,TimeUnit,Scheduler,boolean)
  // XXX: final Flowable throttleWithTimeout(long,TimeUnit)
  // XXX: final Flowable throttleWithTimeout(long,TimeUnit,Scheduler)
  // XXX: final Flowable timeInterval()
  // XXX: final Flowable timeInterval(Scheduler)
  // XXX: final Flowable timeInterval(TimeUnit)
  // XXX: final Flowable timeInterval(TimeUnit,Scheduler)
  // XXX: final Flowable timeout(Function)
  // XXX: final Flowable timeout(Function,Flowable)
  // XXX: final Flowable timeout(long,TimeUnit)
  // XXX: final Flowable timeout(long,TimeUnit,Publisher)
  // XXX: final Flowable timeout(long,TimeUnit,Scheduler)
  // XXX: final Flowable timeout(long,TimeUnit,Scheduler,Publisher)
  // XXX: final Flowable timeout(Publisher,Function)
  // XXX: final Flowable timeout(Publisher,Function,Publisher)
  // XXX: final Flowable timestamp()
  // XXX: final Flowable timestamp(Scheduler)
  // XXX: final Flowable timestamp(TimeUnit)
  // XXX: final Flowable timestamp(TimeUnit,Scheduler)
  // XXX: final Object to(Function)
  // XXX: final Future toFuture()

  static final class FlowableToList<T> {
    @BeforeTemplate
    Single<List<T>> before(Flowable<T> flowable) {
      return flowable.toList();
    }

    @AfterTemplate
    Single<List<T>> after(Flowable<T> flowable) {
      return RxJava2Adapter.monoToSingle(RxJava2Adapter.flowableToFlux(flowable).collectList());
    }
  }

  // XXX: final Single toList(Callable)
  // XXX: final Single toList(int)

  static final class FlowableToMap<I, T extends I, O> {
    @BeforeTemplate
    Single<Map<O, T>> before(Flowable<T> flowable, Function<I, O> function) {
      return flowable.toMap(function);
    }

    @AfterTemplate
    Single<Map<O, T>> after(Flowable<T> flowable, java.util.function.Function<I, O> function) {
      return RxJava2Adapter.monoToSingle(
          RxJava2Adapter.flowableToFlux(flowable).collectMap(function));
    }
  }

  // XXX: final Single toMap(Function,Function)
  // XXX: final Single toMap(Function,Function,Callable)
  // XXX: final Single toMultimap(Function)
  // XXX: final Single toMultimap(Function,Function)
  // XXX: final Single toMultimap(Function,Function,Callable)
  // XXX: final Single toMultimap(Function,Function,Callable,Function)

  static final class FlowableToObservable<T> {
    Observable<T> before(Flowable<T> flowable) {
      return flowable.toObservable();
    }

    Observable<T> after(Flowable<T> flowable) {
      return RxJava2Adapter.fluxToObservable(RxJava2Adapter.flowableToFlux(flowable));
    }
  }

  // XXX: final Single toSortedList()
  // XXX: final Single toSortedList(int)
  // XXX: final Single toSortedList(java.util.Comparator)
  // XXX: final Single toSortedList(java.util.Comparator,int)
  // XXX: final Flowable unsubscribeOn(Scheduler)
  // XXX: final Flowable window(Callable)
  // XXX: final Flowable window(Callable,int)
  // XXX: final Flowable window(long)
  // XXX: final Flowable window(long,long)
  // XXX: final Flowable window(long,long,int)
  // XXX: final Flowable window(long,long,TimeUnit)
  // XXX: final Flowable window(long,long,TimeUnit,Scheduler)
  // XXX: final Flowable window(long,long,TimeUnit,Scheduler,int)
  // XXX: final Flowable window(long,TimeUnit)
  // XXX: final Flowable window(long,TimeUnit,long)
  // XXX: final Flowable window(long,TimeUnit,long,boolean)
  // XXX: final Flowable window(long,TimeUnit,Scheduler)
  // XXX: final Flowable window(long,TimeUnit,Scheduler,long)
  // XXX: final Flowable window(long,TimeUnit,Scheduler,long,boolean)
  // XXX: final Flowable window(long,TimeUnit,Scheduler,long,boolean,int)
  // XXX: final Flowable window(Publisher)
  // XXX: final Flowable window(Publisher,Function)
  // XXX: final Flowable window(Publisher,Function,int)
  // XXX: final Flowable window(Publisher,int)
  // XXX: final Flowable withLatestFrom(Iterable,Function)
  // XXX: final Flowable withLatestFrom(Publisher,BiFunction)
  // XXX: final Flowable withLatestFrom(Publisher[],Function)
  // XXX: final Flowable withLatestFrom(Publisher,Publisher,Function3)
  // XXX: final Flowable withLatestFrom(Publisher,Publisher,Publisher,Function4)
  // XXX: final Flowable withLatestFrom(Publisher,Publisher,Publisher,Publisher,Function5)
  // XXX: final Flowable zipWith(Iterable,BiFunction)

  // XXX: Add test
  static final class FlowableZipWith<T, U, R> {
    @BeforeTemplate
    Flowable<R> before(
        Flowable<T> flowable,
        Iterable<U> iterable,
        BiFunction<? super T, ? super U, ? extends R> zipper) {
      return flowable.zipWith(iterable, zipper);
    }

    @AfterTemplate
    Flowable<R> after(
        Flowable<T> flowable,
        Iterable<U> iterable,
        BiFunction<? super T, ? super U, ? extends R> zipper) {
      return RxJava2Adapter.fluxToFlowable(
          RxJava2Adapter.flowableToFlux(flowable)
              .zipWithIterable(iterable, RxJavaReactorMigrationUtil.toJdkBiFunction(zipper)));
    }
  }

  // XXX: final Flowable zipWith(Publisher,BiFunction)
  // XXX: final Flowable zipWith(Publisher,BiFunction,boolean)
  // XXX: final Flowable zipWith(Publisher,BiFunction,boolean,int)
  // XXX: final TestSubscriber test(long)

  @SuppressWarnings("unchecked")
  static final class FlowableTestAssertResultItem<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, T item) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().assertResult(item),
          flowable.test().await().assertResult(item),
          flowable.test().await().assertResult(item).assertComplete(),
          flowable.test().await().assertComplete().assertResult(item),
          flowable.test().await().assertNoErrors().assertResult(item).assertComplete(),
          flowable.test().await().assertNoErrors().assertResult(item),
          flowable.test().assertValue(item),
          flowable.test().await().assertValue(item),
          flowable.test().await().assertNoErrors().assertValue(item),
          flowable.test().await().assertValue(item).assertComplete(),
          flowable.test().await().assertComplete().assertValue(item),
          flowable.test().await().assertNoErrors().assertValue(item).assertComplete());
    }

    @AfterTemplate
    void after(Flowable<T> flowable, T item) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectNext(item)
          .verifyComplete();
    }
  }

  static final class FlowableTestAssertResultItems<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, @Repeated T... results) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertResult(results), flowable.test().assertResult(results));
    }

    @AfterTemplate
    void after(Flowable<T> flowable, @Repeated T... results) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectNext(results)
          .verifyComplete();
    }
  }

  @SuppressWarnings("unchecked")
  static final class FlowableTestAssertResult<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable) throws InterruptedException {
      Refaster.anyOf(flowable.test().await().assertResult(), flowable.test().assertResult());
    }

    @AfterTemplate
    void after(Flowable<T> flowable) {
      RxJava2Adapter.flowableToFlux(flowable).as(StepVerifier::create).verifyComplete();
    }
  }

  // XXX: Requires investigation
  @SuppressWarnings("unchecked")
  static final class FlowableTestAwaitDoneAssertResultItem<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, long time, TimeUnit timeUnit, T item) {
      flowable.test().awaitDone(time, timeUnit).assertResult(item);
    }

    // XXX: Can be improved with: `Duration.of(time, timeUnit.toChronoUnit());`.
    // Since there is no Reactor equivalent, this is not behavior preserving.
    @AfterTemplate
    void after(Flowable<T> flowable, long time, TimeUnit timeUnit, T item) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectNext(item)
          .verifyComplete();
    }
  }

  static final class FlowableTestAssertValue<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, Predicate<T> predicate) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertValue(predicate),
          flowable.test().await().assertValue(predicate).assertComplete(),
          flowable.test().await().assertValue(predicate).assertNoErrors().assertComplete(),
          flowable.test().await().assertComplete().assertValue(predicate));
    }

    @AfterTemplate
    void after(Flowable<T> flowable, Predicate<T> predicate) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectNextMatches(RxJavaReactorMigrationUtil.toJdkPredicate(predicate))
          .verifyComplete();
    }
  }

  // XXX: Strictly speaking not equivalent because of the poor API design for `assertValueSet`.
  static final class FlowableAssertValueSet<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, Collection<? extends T> set) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertValueSet(set),
          flowable.test().await().assertNoErrors().assertValueSet(set).assertComplete(),
          flowable.test().await().assertValueSet(set).assertNoErrors().assertComplete());
    }

    @AfterTemplate
    void after(Flowable<T> flowable, Collection<? extends T> set) {
      RxJava2Adapter.flowableToFlux(flowable)
          .collect(toImmutableMultiset())
          .as(StepVerifier::create)
          .expectNext(ImmutableMultiset.copyOf(set))
          .verifyComplete();
    }
  }

  // XXX: Introduced this more specific one because the `Collection` did not see the ImmutableSet
  // variants.
  static final class FlowableAssertValueImmutableSet<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, ImmutableSet<? extends T> set) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertValueSet(set),
          flowable.test().await().assertNoErrors().assertValueSet(set).assertComplete(),
          flowable.test().await().assertValueSet(set).assertNoErrors().assertComplete());
    }

    @AfterTemplate
    void after(Flowable<T> flowable, ImmutableSet<? extends T> set) {
      RxJava2Adapter.flowableToFlux(flowable)
          .collect(toImmutableSet())
          .as(StepVerifier::create)
          .expectNext(ImmutableSet.copyOf(set))
          .verifyComplete();
    }
  }

  // XXX: Add test
  static final class FlowableAssertValueSetWithValueCount<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, Collection<? extends T> set, Integer count)
        throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertValueCount(count).assertValueSet(set),
          flowable
              .test()
              .assertComplete()
              .assertNoErrors()
              .assertValueCount(count)
              .assertValueSet(set),
          flowable
              .test()
              .await()
              .assertComplete()
              .assertNoErrors()
              .assertValueCount(count)
              .assertValueSet(set),
          flowable.test().assertValueCount(count).assertValueSet(set).assertComplete(),
          flowable
              .test()
              .await()
              .assertValueCount(count)
              .assertValueSet(set)
              .assertNoErrors()
              .assertComplete(),
          flowable
              .test()
              .await()
              .assertValueSet(set)
              .assertNoErrors()
              .assertValueCount(count)
              .assertComplete());
    }

    @AfterTemplate
    void after(Flowable<T> flowable, Collection<? extends T> set, Integer count) {
      RxJava2Adapter.flowableToFlux(flowable)
          .collect(toImmutableMultiset())
          .as(StepVerifier::create)
          .expectNextCount(count)
          .expectNext(ImmutableMultiset.copyOf(set))
          .verifyComplete();
    }
  }

  static final class FlowableTestAssertResultValues<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, @Repeated T item) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertResult(Refaster.asVarargs(item)),
          flowable.test().await().assertValues(Refaster.asVarargs(item)));
    }

    @AfterTemplate
    void after(Flowable<T> flowable, @Repeated T item) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectNext(item)
          .verifyComplete();
    }
  }

  static final class FlowableTestAssertComplete<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertComplete(),
          flowable.test().assertComplete(),
          flowable.test().await());
    }

    @AfterTemplate
    void after(Flowable<T> flowable) {
      RxJava2Adapter.flowableToFlux(flowable).as(StepVerifier::create).verifyComplete();
    }
  }

  static final class FlowableTestAssertErrorClass<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, Class<? extends Throwable> errorClass)
        throws InterruptedException {
      flowable.test().await().assertError(errorClass);
    }

    @AfterTemplate
    void after(Flowable<T> flowable, Class<? extends Throwable> errorClass) {
      RxJava2Adapter.flowableToFlux(flowable).as(StepVerifier::create).verifyError(errorClass);
    }
  }

  static final class FlowableTestAssertNoErrors<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable) throws InterruptedException {
      flowable.test().await().assertNoErrors();
    }

    @AfterTemplate
    void after(Flowable<T> flowable) {
      RxJava2Adapter.flowableToFlux(flowable).as(StepVerifier::create).verifyComplete();
    }
  }

  static final class FlowableTestAssertValueCount<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, int count) throws InterruptedException {
      flowable.test().await().assertValueCount(count);
    }

    @AfterTemplate
    void after(Flowable<T> flowable, int count) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectNextCount(count)
          .verifyComplete();
    }
  }

  // XXX: Add test
  @SuppressWarnings("unchecked")
  static final class FlowableTestAssertFailure<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, Class<? extends Throwable> error)
        throws InterruptedException {
      flowable.test().await().assertFailure(error);
    }

    @AfterTemplate
    void after(Flowable<T> flowable, Class<? extends Throwable> error) {
      RxJava2Adapter.flowableToFlux(flowable).as(StepVerifier::create).verifyError(error);
    }
  }

  // XXX: Add test
  static final class FlowableTestAssertNoValues<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable) throws InterruptedException {
      Refaster.anyOf(
          flowable.test().await().assertNoValues(),
          flowable.test().await().assertNoValues().assertComplete());
    }

    @AfterTemplate
    void after(Flowable<T> flowable) {
      RxJava2Adapter.flowableToFlux(flowable).as(StepVerifier::create).verifyComplete();
    }
  }

  // XXX: Add test
  // XXX: This introduces AssertJ dependency
  @SuppressWarnings("unchecked")
  static final class FlowableTestAssertFailureAndMessage<T> {
    @BeforeTemplate
    void before(Flowable<T> flowable, Class<? extends Throwable> error, String message)
        throws InterruptedException {
      flowable.test().await().assertFailureAndMessage(error, message);
    }

    @AfterTemplate
    void after(Flowable<T> flowable, Class<? extends Throwable> error, String message) {
      RxJava2Adapter.flowableToFlux(flowable)
          .as(StepVerifier::create)
          .expectErrorSatisfies(
              t -> assertThat(t).isInstanceOf(error).hasMessageContaining(message))
          .verify();
    }
  }

  // XXX: final TestSubscriber test(long,boolean)
}
