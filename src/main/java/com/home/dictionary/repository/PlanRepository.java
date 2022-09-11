package com.home.dictionary.repository;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.plan.Plan;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlanRepository extends PagingAndSortingRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    default Plan findByIdOrThrow(Long planId) {
        return findById(planId)
                .orElseThrow(() -> new ApiEntityNotFoundException("Plan with id " + planId + " not found"));
    }

}
