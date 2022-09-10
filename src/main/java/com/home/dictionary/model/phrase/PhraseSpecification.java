package com.home.dictionary.model.phrase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class PhraseSpecification implements Specification<Phrase> {

    private final PhraseFilter filter;

    @Override
    public Predicate toPredicate(Root<Phrase> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(
                planIdEquals(root, criteriaBuilder)
        );
    }

    private Predicate planIdEquals(Root<Phrase> root, CriteriaBuilder criteriaBuilder) {
        if (filter.planId() == null) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }
        return criteriaBuilder.equal(root.get("plan").get("id"), filter.planId());
    }

}
