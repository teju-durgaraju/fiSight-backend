package com.example.financialhealth.model;

import jakarta.persistence.*;
import lombok.*; // Added
import lombok.EqualsAndHashCode.Include; // Added
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
// Removed java.util.Objects

@Getter // Lombok
@Setter // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok
@ToString(exclude = {"user"}) // Lombok
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include // For Lombok @EqualsAndHashCode
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "goal_name", nullable = false, length = 255)
    private String goalName;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentAmount = BigDecimal.ZERO; // Default to zero

    @Column(name = "target_date") // Nullable
    private LocalDate targetDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // Manual constructors, getters, setters, equals, hashCode are removed.
    // @AllArgsConstructor will cover the previous constructors.
    // The constructor `public Goal(User user, String goalName, BigDecimal targetAmount, LocalDate targetDate)`
    // where currentAmount was explicitly set to ZERO is effectively handled by @AllArgsConstructor
    // because the field `currentAmount` is initialized to `BigDecimal.ZERO`.
    // If an @AllArgsConstructor is generated, it will include all fields.
    // If a constructor without `currentAmount` (expecting default) was desired,
    // a custom constructor or @Builder might be options.
    // For now, @AllArgsConstructor and the field default are fine.
}
