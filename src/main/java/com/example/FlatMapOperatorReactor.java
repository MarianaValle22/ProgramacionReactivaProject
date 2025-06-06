package com.example;
import reactor.core.publisher.Flux;

public class FlatMapOperatorReactor {
    public static void main(String[] args) {
        Flux.just(1, 2, 3)
                .flatMap(item -> Flux.just(item * 10, item * 20))
                .subscribe(System.out::println);
    }
}
