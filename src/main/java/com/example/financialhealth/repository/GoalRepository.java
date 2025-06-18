package com.example.financialhealth.repository;

import com.example.financialhealth.model.Goal;
import com.example.financialhealth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Finds all goals for a given user, ordered by their target date (ascending, nulls last typically)
     * and then by goal name (ascending).
     * @param user The user whose goals are to be retrieved.
     * @return A list of goals.
     */
    List<Goal> findByUserOrderByTargetDateAscGoalNameAsc(User user);

    /**
     * Finds a specific goal by its ID and the user who owns it.
     * This is useful for ensuring a user can only access their own goals.
     * @param id The ID of the goal.
     * @param user The user who owns the goal.
     * @return An Optional containing the goal if found and owned by the user, otherwise empty.
     */
    Optional<Goal> findByIdAndUser(Long id, User user);

}
