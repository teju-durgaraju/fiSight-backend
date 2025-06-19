package com.example.financialhealth.model;

import jakarta.persistence.*;
import lombok.*; // Added
import lombok.EqualsAndHashCode.Include; // Added
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
// Removed java.util.Objects

@Getter // Lombok
@Setter // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok for ID-based
@ToString // Lombok
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include // For Lombok @EqualsAndHashCode (ID-based)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255) // Nullable by default
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    // Manual constructors, getters, setters, equals, hashCode are removed.
    // @AllArgsConstructor will cover the `public Category(String name, String description)` if
    // id, createdAt, updatedAt are considered part of "all args".
    // If specific constructor for only name and description is vital, it might need to be re-added
    // or use a builder pattern. For now, relying on @AllArgsConstructor.
    // The previous equals/hashCode was more complex (name-based for transient).
    // Sticking to ID-based as per subtask instructions for simplicity with Lombok here.
}
