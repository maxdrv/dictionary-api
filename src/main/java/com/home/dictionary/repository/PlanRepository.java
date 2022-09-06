package com.home.dictionary.repository;

import com.home.dictionary.model.plan.Plan;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlanRepository extends PagingAndSortingRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

}
