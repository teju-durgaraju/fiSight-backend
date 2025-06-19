package com.example.financialhealth.model;

import jakarta.persistence.*;
import lombok.*; // Added
import lombok.EqualsAndHashCode.Include; // Added
import org.springframework.security.core.GrantedAuthority;

// Removed java.util.Objects import as Lombok will handle equals/hashCode

@Getter // Lombok
@Setter // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok
@ToString // Lombok
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include // For Lombok @EqualsAndHashCode
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Manual constructors, getters, setters, equals, hashCode are removed.

    @Override
    public String getAuthority() { // This is from GrantedAuthority, must be kept.
        return name;
    }

    // Lombok's @EqualsAndHashCode and @ToString will be used.
    // @EqualsAndHashCode.Include on 'id' makes it ID-based.
}
