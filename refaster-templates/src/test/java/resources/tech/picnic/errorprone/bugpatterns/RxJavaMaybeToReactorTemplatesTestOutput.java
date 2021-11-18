package resources.tech.picnic.errorprone.bugpatterns;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import java.util.concurrent.CompletableFuture;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tech.picnic.errorprone.migration.util.RxJavaReactorMigrationUtil;

final class RxJavaMaybeToReactorTemplatesTest implements RefasterTemplateTestCase {

  @Override
  public ImmutableSet<?> elidedTypesAndStaticImports() {
    return ImmutableSet.of(Observable.class);
  }

  Maybe<String> testMaybeAmb() {
    return RxJava2Adapter.monoToMaybe(
        Mono.firstWithSignal(
            Streams.stream(ImmutableList.of(Maybe.just("foo"), Maybe.just("bar")))
                .map(RxJava2Adapter::maybeToMono)
                .collect(ImmutableList.toImmutableList())));
  }

  // XXX: Template turned off for now.
  Maybe<String> testMaybeAmbArray() {
    return Maybe.ambArray(
        RxJava2Adapter.monoToMaybe(Mono.just("foo")), RxJava2Adapter.monoToMaybe(Mono.just("bar")));
  }

  Flowable<Integer> testMaybeConcatArray() {
    return Flowable.empty();
  }

  Mono<String> testMaybeDefer() {
    return Mono.defer(() -> RxJava2Adapter.maybeToMono(Maybe.just("test")));
  }

  Maybe<Integer> testMaybeEmpty() {
    return RxJava2Adapter.monoToMaybe(Mono.empty());
  }

  Maybe<Object> testMaybeErrorThrowable() {
    return RxJava2Adapter.monoToMaybe(Mono.error(new IllegalStateException()));
  }

  Maybe<Object> testMaybeErrorCallable() {
    return RxJava2Adapter.monoToMaybe(
        Mono.error(
            RxJavaReactorMigrationUtil.callableAsSupplier(
                () -> {
                  throw new IllegalStateException();
                })));
  }

  Maybe<Object> testMaybeFromAction() {
    return RxJava2Adapter.monoToMaybe(
        Mono.fromRunnable(
            RxJavaReactorMigrationUtil.toRunnable(
                () -> {
                  String s = "foo";
                })));
  }

  Maybe<Object> testMaybeFromCallable() {
    return RxJava2Adapter.monoToMaybe(
        Mono.fromSupplier(
            RxJavaReactorMigrationUtil.callableAsSupplier(
                () -> {
                  String s = "foo";
                  return null;
                })));
  }

  Maybe<Integer> testMaybeFromFuture() {
    return RxJava2Adapter.monoToMaybe(Mono.fromFuture(new CompletableFuture<>()));
  }

  Maybe<Integer> testMaybeFromRunnable() {
    return RxJava2Adapter.monoToMaybe(
        Mono.fromRunnable(
            () -> {
              int i = 1 + 1;
            }));
  }

  Maybe<Integer> testMaybeFromSingle() {
    return RxJava2Adapter.monoToMaybe(
        Mono.from(RxJava2Adapter.singleToMono(Single.wrap(Single.just(1)))));
  }

  Maybe<Integer> testMaybeJust() {
    return RxJava2Adapter.monoToMaybe(Mono.just(1));
  }

  Maybe<Integer> testMaybeWrap() {
    return Maybe.just(1);
  }

  Maybe<String> testMaybeAmbWith() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just("foo"))
            .or(RxJava2Adapter.maybeToMono(Maybe.just("bar"))));
  }

  Integer testMaybeBlockingGet() {
    return RxJava2Adapter.maybeToMono(Maybe.just(1)).block();
  }

  Maybe<String> testMaybeCastPositive() {
    return Maybe.just("string");
  }

  @SuppressWarnings("MaybeJust")
  Maybe<Object> testMaybeCastNegative() {
    return Maybe.just("string").cast(Object.class);
  }

  Maybe<Integer> testMaybeDefaultIfEmpty() {
    return RxJava2Adapter.monoToMaybe(RxJava2Adapter.maybeToMono(Maybe.just(1)).defaultIfEmpty(0));
  }

  Maybe<Integer> testMaybeDoOnError() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .doOnError(RxJavaReactorMigrationUtil.toJdkConsumer(System.out::println)));
  }

  Maybe<Integer> testMaybeDoOnSuccess() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .doOnSuccess(RxJavaReactorMigrationUtil.toJdkConsumer(System.out::println)));
  }

  Maybe<Integer> testMaybeFilter() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .filter(RxJavaReactorMigrationUtil.toJdkPredicate(i -> i > 1)));
  }

  @SuppressWarnings("MaybeJust")
  Maybe<Integer> testMaybeFlatMapFunction() {
    RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .flatMap(
                v ->
                    RxJava2Adapter.maybeToMono(
                        Maybe.wrap(
                            RxJavaReactorMigrationUtil.<Integer, MaybeSource<Integer>>toJdkFunction(
                                    this::exampleMethod)
                                .apply(v)))));

    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .flatMap(
                v ->
                    RxJava2Adapter.maybeToMono(
                        Maybe.wrap(
                            RxJavaReactorMigrationUtil.<Integer, MaybeSource<Integer>>toJdkFunction(
                                    exampleFunction())
                                .apply(v)))));
  }

  private io.reactivex.functions.Function<Integer, MaybeSource<Integer>> exampleFunction() {
    return null;
  }

  Maybe<Integer> testMaybeFlatMapLambda() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .flatMap(z -> Maybe.just(z * 2).as(RxJava2Adapter::maybeToMono)));
  }

  Maybe<Integer> testMaybeFlatMapMethodReference() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .flatMap(
                v ->
                    RxJava2Adapter.maybeToMono(
                        Maybe.wrap(
                            RxJavaReactorMigrationUtil.<Integer, MaybeSource<Integer>>toJdkFunction(
                                    this::exampleMethod)
                                .apply(v)))));
  }

  private Maybe<Integer> exampleMethod(Integer x) {
    return null;
  }

  Maybe<Integer> testMaybeFlatMapSingleElement() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .flatMap(
                e ->
                    RxJava2Adapter.singleToMono(
                        Single.wrap(
                            RxJavaReactorMigrationUtil.toJdkFunction(
                                    (Function<Integer, SingleSource<Integer>>) x -> Single.just(x))
                                .apply(e)))));
  }

  Completable testMaybeIgnoreElement() {
    return RxJava2Adapter.monoToCompletable(RxJava2Adapter.maybeToMono(Maybe.just(1)).then());
  }

  Single<Boolean> testMaybeIsEmpty() {
    return RxJava2Adapter.monoToSingle(RxJava2Adapter.maybeToMono(Maybe.just(1)).hasElement());
  }

  Maybe<String> testMaybeMap() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .map(RxJavaReactorMigrationUtil.toJdkFunction(String::valueOf)));
  }

  Maybe<Integer> testMaybeOnErrorReturn() {
    return RxJava2Adapter.monoToMaybe(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .onErrorResume(t -> Mono.just(Integer.valueOf(1))));
  }

  Single<Integer> testMaybeSwitchIfEmpty() {
    return RxJava2Adapter.monoToSingle(
        RxJava2Adapter.maybeToMono(Maybe.just(1))
            .switchIfEmpty(
                RxJava2Adapter.singleToMono(
                    Single.wrap(
                        Single.<Integer>error(
                            () -> {
                              throw new IllegalStateException();
                            })))));
  }

  Flowable<Integer> testMaybeToFlowable() {
    return RxJava2Adapter.fluxToFlowable(RxJava2Adapter.maybeToMono(Maybe.just(1)).flux());
  }

  Observable<Integer> testMaybeToObservable() {
    return RxJava2Adapter.fluxToObservable(RxJava2Adapter.maybeToMono(Maybe.just(1)).flux());
  }

  @SuppressWarnings("MaybeJust")
  private Maybe<Integer> getMaybe() {
    return Maybe.just(3);
  }

  void MaybeTestAssertResultItem() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1))
        .as(StepVerifier::create)
        .expectNext(1)
        .verifyComplete();
    RxJava2Adapter.maybeToMono(Maybe.just(2))
        .as(StepVerifier::create)
        .expectNext(2)
        .verifyComplete();
  }

  void MaybeTestAssertResult() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1)).as(StepVerifier::create).verifyComplete();
  }

  void MaybeTestAssertValue() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1))
        .as(StepVerifier::create)
        .expectNextMatches(RxJavaReactorMigrationUtil.toJdkPredicate(i -> i > 2))
        .verifyComplete();
    RxJava2Adapter.maybeToMono(Maybe.just(3))
        .as(StepVerifier::create)
        .expectNextMatches(RxJavaReactorMigrationUtil.toJdkPredicate(i -> i > 4))
        .verifyComplete();
  }

  void testMaybeTestAssertComplete() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1)).as(StepVerifier::create).verifyComplete();
  }

  void testMaybeTestAssertErrorClass() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1))
        .as(StepVerifier::create)
        .verifyError(InterruptedException.class);
  }

  void testMaybeTestAssertNoErrors() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1)).as(StepVerifier::create).verifyComplete();
  }

  void testMaybeTestAssertValueCount() throws InterruptedException {
    RxJava2Adapter.maybeToMono(Maybe.just(1))
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }
}
