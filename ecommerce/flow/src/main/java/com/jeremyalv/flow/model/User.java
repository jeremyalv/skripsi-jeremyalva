package com.jeremyalv.flow.model;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jeremyalv.flow.config.InstantToMillisSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    @NotBlank(message = "Email is required")
    @Size(min = 1, max = 255, message = "Email must be at most 255 characters")
    private String email;
    

    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 50, message = "First Name must be at most 50 characters")
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "hashed_password", nullable = false, length = 255)
    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 255, message = "Password must be at most 255 characters")
    @JsonIgnore
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonSerialize(using = InstantToMillisSerializer.class)
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @JsonSerialize(using = InstantToMillisSerializer.class)
    private Instant updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}
