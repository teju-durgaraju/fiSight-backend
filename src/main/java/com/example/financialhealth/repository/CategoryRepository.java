package com.example.financialhealth.repository;

import com.example.financialhealth.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name, ignoring case.
     * Since category names are unique (case-sensitive by default unique constraint),
     * this method provides a case-insensitive way to look them up.
     * If multiple categories were to exist with names differing only by case (which the unique=true
     * on the entity's name field should prevent if the DB collation is case-sensitive),
     * this might return one of them unpredictably. However, with a case-sensitive unique constraint,
     * this will effectively find the unique category if its name matches ignoring case.
     *
     * @param name The name of the category to find (case-insensitive).
     * @return An Optional containing the category if found, otherwise empty.
     */
    Optional<Category> findByNameIgnoreCase(String name);
}
