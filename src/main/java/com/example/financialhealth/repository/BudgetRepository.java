package com.example.financialhealth.repository;

import com.example.financialhealth.model.Budget;
import com.example.financialhealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserOrderByMonthDescCategoryAsc(User user);

    List<Budget> findByUserAndMonthOrderByCategoryAsc(User user, String month); // month in "YYYY-MM" format

    Optional<Budget> findByUserAndCategoryAndMonth(User user, String category, String month);

    Optional<Budget> findByIdAndUser(Long id, User user);

}
