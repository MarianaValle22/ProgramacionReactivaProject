package com.example;
import reactor.core.publisher.Flux;

public class FilterOperatorReactor {
    public static void main(String[] args) {
        Flux.just(1, 2, 3, 4, 5)
                .filter(item -> item % 2 == 0)
                .subscribe(System.out::println);
    }
}
