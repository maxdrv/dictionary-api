package com.home.dictionary.service.order;

import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class OrderStrategies {

    private final Map<OrderStrategyType, OrderStrategy> strategies;

    public OrderStrategies(List<OrderStrategy> orderStrategies) {
        this.strategies = StreamEx.of(orderStrategies)
                .mapToEntry(OrderStrategy::getType, Function.identity())
                .toMap();
    }

    public OrderStrategy getByType(OrderStrategyType type) {
        return strategies.get(type);
    }

}
