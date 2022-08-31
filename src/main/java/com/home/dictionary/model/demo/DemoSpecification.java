package com.home.dictionary.model.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


@RequiredArgsConstructor
public class DemoSpecification implements Specification<Demo> {

    private final DemoFilter filter;

    @Override
    public Predicate toPredicate(Root<Demo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                nameContains(root, criteriaBuilder),
                hasType(root, criteriaBuilder)
        );
    }

    private Predicate nameContains(Root<Demo> root, CriteriaBuilder criteriaBuilder) {
        if (filter.name() == null || filter.name().isBlank()) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.like(root.get("name"), filter.name());
    }

    private Predicate hasType(Root<Demo> root, CriteriaBuilder criteriaBuilder) {
        if (filter.type() == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("type"), filter.type());
    }

}
