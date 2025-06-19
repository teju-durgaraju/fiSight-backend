package com.example.financialhealth.model;

import jakarta.persistence.*;
import lombok.*; // Added
import lombok.EqualsAndHashCode.Include; // Added
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter // Lombok
@Setter // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok
@ToString(exclude = {"passwordHash", "roles"}) // Lombok - excluded passwordHash from UserDetails, and roles
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include // For Lombok @EqualsAndHashCode
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Custom constructor if needed, e.g. for specific fields not covered by @AllArgsConstructor
    // or if @AllArgsConstructor is too broad.
    // For now, assuming @AllArgsConstructor is sufficient or this specific constructor is not critical.
    // public User(String username, String passwordHash) {
    //     this.username = username;
    //     this.passwordHash = passwordHash;
    // }

    // Manual Getters and Setters are removed as Lombok will generate them.

    // UserDetails methods - These must remain as they are specific implementations.
    // Lombok does not override methods from implemented interfaces automatically unless they match specific patterns.
    // @Override for getUsername() and getPassword() from UserDetails will be correctly handled by Lombok's @Getter
    // if the field names match.
    // `username` field matches `getUsername()`
    // `passwordHash` field needs a `getPassword()` method for UserDetails. Lombok's @Getter on passwordHash generates getPasswordHash().
    // So, we need to manually ensure getPassword() from UserDetails is correctly implemented.

    @Override
    public String getPassword() { // This manual method is crucial for UserDetails
        return this.passwordHash;
    }
    // Note: @Getter on `username` field will generate `getUsername()` which matches UserDetails.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // hashCode and equals are handled by Lombok's @EqualsAndHashCode(onlyExplicitlyIncluded = true) and @Include on id.
}
